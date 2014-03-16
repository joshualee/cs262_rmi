package workerServer;

import edu.harvard.cs262.ComputeServer.ComputeServer;
import edu.harvard.cs262.ComputeServer.WorkQueue;
import edu.harvard.cs262.ComputeServer.WorkTask;
import securityManagers.DumbSecurityManager;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

/**
 * Created by perry on 3/13/14.
 */
public class WorkerServer implements ComputeServer {
  public static int VERBOSE = 1; 
  
@Override
  public Object sendWork(WorkTask work) throws RemoteException {
	Object result = work.doWork();
    if (VERBOSE > 0) {
    	System.out.println(result);
    }
    return result;
  }

  @Override
  public boolean PingServer() throws RemoteException {
	if (VERBOSE > 0) {
		System.out.println("got pinged");
	}
    return true;
  }

  public static void main(String args[]) {
    int rmiPort;
    String rmiHost, rmiName;

    if (args.length != 3) {
      System.err.println("usage: java WorkerServer host port rminame");
      System.exit(1);
    }

    rmiHost = args[0];
    rmiPort = Integer.parseInt(args[1]);
    rmiName = args[2];

    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new DumbSecurityManager());
    }
    try {
      Registry registry = LocateRegistry.getRegistry(rmiHost, rmiPort);
      WorkQueue queueServer = (WorkQueue) registry.lookup(rmiName);
      ComputeServer workerServer = new WorkerServer();
      queueServer.registerWorker((ComputeServer) UnicastRemoteObject.exportObject(workerServer, 0));
    } catch (Exception e) {
      System.err.println("Worker Server exception:");
      e.printStackTrace();
    }
  }
}
