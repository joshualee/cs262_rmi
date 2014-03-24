package workerServer;

import edu.harvard.cs262.ComputeServer.ComputeServer;
import edu.harvard.cs262.ComputeServer.WorkQueue;
import edu.harvard.cs262.ComputeServer.WorkTask;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 * The {@link ComputeServer} implementation that does the work sent to it on its
 * own machine.<p>
 */
public class WorkerServer implements ComputeServer {
  public static int VERBOSE = 1;

  /**
   * See {@link edu.harvard.cs262.ComputeServer.ComputeServer#sendWork(WorkTask)}.
   *
   * @param work - the {@link WorkTask} object to be executed via doWork
   * @return the result of the doWork call on work
   * @throws RemoteException
   */
  @Override
  public Object sendWork(WorkTask work) throws RemoteException {
	Object result = work.doWork();
    if (VERBOSE > 0) {
    	System.out.println(result);
    }
    return result;
  }

  /**
   * Pings the server.
   *
   * @return true if the server is still responding
   * @throws RemoteException
   * @see edu.harvard.cs262.ComputeServer.ComputeServer#PingServer()
   */
  @Override
  public boolean PingServer() throws RemoteException {
	if (VERBOSE > 0) {
		System.out.println("got pinged");
	}
    return true;
  }

  public static void main(String args[]) {
	// Parse RMI arguments: host, port, name of QueuedServer
    int rmiPort;
    String rmiHost, rmiName;

    if (args.length != 3) {
      System.err.println("usage: java WorkerServer host port rminame");
      System.exit(1);
    }

    rmiHost = args[0];
    rmiPort = Integer.parseInt(args[1]);
    rmiName = args[2];

    // Ensure it is safe to download remote security definitions with Security
    // Manager
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new SecurityManager());
    }
    try {
      // Locate the Queue Server and register self with it
      Registry registry = LocateRegistry.getRegistry(rmiHost, rmiPort);
      WorkQueue queueServer = (WorkQueue) registry.lookup(rmiName);
      ComputeServer workerServer = new WorkerServer();
      UUID id = queueServer.registerWorker((ComputeServer)
    		  UnicastRemoteObject.exportObject(workerServer, 0));
      
      if (VERBOSE > 0) {
    	  System.out.println("Successfully registered with UUID" + id);
      }
    } catch (Exception e) {
      System.err.println("Worker Server exception:");
      e.printStackTrace();
    }
  }
}
