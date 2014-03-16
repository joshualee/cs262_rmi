package client;

import edu.harvard.cs262.ComputeServer.ComputeServer;
import edu.harvard.cs262.ComputeServer.WorkAttempter;
import edu.harvard.cs262.ComputeServer.WorkFailedException;
import edu.harvard.cs262.ComputeServer.WorkTask;
import securityManagers.DumbSecurityManager;
import workTasks.SleepTask;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * A PingingClient
 */
public class PingingClient {
  private static final int VERBOSE = 1;
  private WorkTask work;
  private ComputeServer server;
  private WorkAttempter workAttempter;
  private final int MAXATTEMPTS = 3;
  private final int PINGTIMEOUT = 3;


  public PingingClient(WorkTask work, ComputeServer server) {
    this.work = work;
    this.server = server;
    this.workAttempter = new PingingWorkAttempter();
  }

  public Object getAnswer() throws WorkFailedException {
    return workAttempter.attemptWork(server, work, MAXATTEMPTS, PINGTIMEOUT);
  }

  public static void main(String args[]) {
	// Parse RMI arguments: host, port, name of ComputeServer
    int rmiPort;
    String rmiHost, rmiName;
    WorkTask sleepTask = new SleepTask(10);

    if (args.length != 3) {
      System.err.println("usage: java PingingClient host port rminame");
      System.exit(1);
    }

    rmiHost = args[0];
    rmiPort = Integer.parseInt(args[1]);
    rmiName = args[2];
    
    // Ensure it is safe to download remote security definitions with Security
    // Manager
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new DumbSecurityManager());
    }
    try {
      // Locate the ComputeServer and start sending it work
      Registry registry = LocateRegistry.getRegistry(rmiHost, rmiPort);
      ComputeServer server = (ComputeServer) registry.lookup(rmiName);
      if (VERBOSE > 0)
        System.out.println("Connected to registry");
      PingingClient client = new PingingClient(sleepTask, server);
      Object answer = client.getAnswer();
      System.out.println("Answer: " + answer);

    } catch (WorkFailedException e) {
      System.out.println("Work failed");
    }
    catch (Exception e) {
      System.err.println("Pinging Client exception:");
      e.printStackTrace();
    }
  }
}
