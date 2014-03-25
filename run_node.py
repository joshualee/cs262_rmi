#!/bin/python
import argparse
import os.path
import boto.ec2
import boto.manage.cmdshell
import time


if __name__ == '__main__':
  parser = argparse.ArgumentParser()
  subparsers = parser.add_subparsers()

  # TODO: This unfortunate serving of copypasta could be remedied by subclassing
  client_subparser = subparsers.add_parser('client')
  client_subparser.set_defaults(command='client')
  client_subparser.add_argument("host", help="IP address or hostname of server.")
  client_subparser.add_argument("port", help="Server port to connect to.")
  client_subparser.add_argument("key_path", help="Location of the '.pem' key file to use for the client EC2 instance (file name must match EC2 registration).")

  worker_subparser = subparsers.add_parser('worker')
  worker_subparser.set_defaults(command='worker')
  worker_subparser.add_argument("host", help="IP address or hostname of server.")
  worker_subparser.add_argument("port", help="Server port to connect to.")
  worker_subparser.add_argument("key_path", help="Location of the '.pem' key file to use for the worker EC2 instance (file name must match EC2 registration).")

  server_subparser = subparsers.add_parser('server')
  server_subparser.set_defaults(command='server')
  server_subparser.add_argument("port", help="Port to serve from.")
  server_subparser.add_argument("key_path", help="Location of the '.pem' key file to use for the server EC2 instance (file name must match EC2 registration).")

  args = parser.parse_args()

  args.key_path = os.path.abspath(os.path.expanduser(args.key_path))
  key_name = os.path.basename(args.key_path).split('.pem')[0]

  conn = boto.ec2.connect_to_region("us-east-1")

  # TODO: Reuse if already exists
  # Create security group
  rmi_security_group = conn.create_security_group('cs262_c2_security_group', 'CS262 Coding Assignment 2 Security group')
  try:
    rmi_security_group.authorize('tcp', 22, 22, '0.0.0.0/0')                  # SSH (for commands and file deployment)
    rmi_security_group.authorize('tcp', 80, 80, '0.0.0.0/0')                  # HTTP (for client to distribute JAR) 
    rmi_security_group.authorize('tcp', 1099, 1099, '0.0.0.0/0')              # Java RMI
    rmi_security_group.authorize('tcp', args.port, args.port, '0.0.0.0/0')    # Remote object port

    # Start new instance
    reservation = conn.run_instances('ami-d7a18dbe', instance_type='t1.micro', security_groups=[rmi_security_group.name], key_name=key_name)
    instance = reservation.instances[0]
    print 'Waiting for EC2 instance to start running...'
    try:
      while instance.update() != 'running':
        time.sleep(5)

      print 'Waiting for EC2 instance to become accessible...'
      # FIXME: Shitty, brittle workaround (can't remember the super secret correct way)
      time.sleep(80)
      cmd = boto.manage.cmdshell.sshclient_from_instance(instance, args.key_path, user_name='ec2-user')
      
      print 'Connected.'
      # Copy JAR and security policy to instance
      cmd.put_file(os.path.abspath('bin/rmi262.jar'), 'rmi262.jar')
      cmd.put_file(os.path.abspath('server.policy'), 'server.policy')    # All node types really use the same policy
      
      # Prepare the command string to be executed
      if args.command == 'client':
        command_string = \
          ('java -Djava.security.policy=server.policy'
           ' -Djava.rmi.server.codebase=http://'+args.host+'/rmi262.jar'
           ' -Djava.rmi.server.useCodebaseOnly=false -classpath rmi262.jar'
           ' client.SimpleClient '+args.host+' '+args.port+' group2')
      elif args.command == 'worker':
        command_string = \
          ('java -Djava.security.policy=server.policy'
           ' -Djava.rmi.server.codebase=http://'+args.host+'/rmi262.jar'
           ' -Djava.rmi.server.useCodebaseOnly=false -classpath rmi262.jar'
           ' workerServer.WorkerServer '+args.host+' '+args.port+' group2')
      elif args.command == 'server':
        command_string = \
          ('java -Djava.security.policy=server.policy'
           ' -Djava.rmi.server.codebase=http://'+instance.dns_name+'/rmi262.jar'
           ' -Djava.rmi.server.useCodebaseOnly=false -classpath rmi262.jar'
           ' workerServer.QueuedServer '+args.port+' group2')
      
      # If running a client, need to host the JAR
      if args.command == 'client':
        channel = cmd.run_pty('sudo yum install -y httpd')
        channel.recv_exit_status()    # Wait for command to finish
        channel = cmd.run_pty('sudo service httpd start')
        channel.recv_exit_status()    # Wait for command to finish
        channel = cmd.run_pty('sudo cp rmi262.jar /var/www/html/')
        channel.recv_exit_status()    # Wait for command to finish
        channel = cmd.run_pty('sudo chmod a+r /var/www/html/rmi262.jar')
        channel.recv_exit_status()    # Wait for command to finish

      print 'Running new '+args.command+' node at: '+instance.dns_name
      channel = cmd.run_pty(command_string)
          
      # Wait for user to quit
      print 'Press CTRL+c (once) to clean and quit.'
      while not channel.exit_status_ready():
        time.sleep(5)

      print '\nService stopped unexpectedly!\n'
      print 'STDOUT:\n'
      print channel.recv(1000)
      print '\nSTDERR\n'
      print channel.recv_stderr(1000)

    except:
      raise
    finally:
      print 'Terminating EC2 instance.'
      instance.terminate()
      while instance.update() != 'terminated':
        time.sleep(5)

  except KeyboardInterrupt:
    None
  except:
    raise
  finally:
    print 'Removing generated security group.'
    # TODO: Preserve if already existed
    rmi_security_group.delete()

