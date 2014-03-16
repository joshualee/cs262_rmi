package workerServer;

import client.PingingWorkAttempter;
import edu.harvard.cs262.ComputeServer.*;
import securityManagers.DumbSecurityManager;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class QueuedServer implements ComputeServer, WorkQueue {
	private final int MAXATTEMPTS = 2;
	private final static int RMIPORT = 8888;
	private final int PINGTIMEOUT = 3; // in seconds
	private final static String RMINAME = "G2QueuedServer";
  private final WorkAttempter workAttempter;

	private ConcurrentHashMap<UUID, ComputeServer> workers;
	private LinkedBlockingQueue<UUID> freeWorkers, busyWorkers;

	private QueuedServer(){
		super();
		workers = new ConcurrentHashMap<UUID, ComputeServer>();
		freeWorkers = new LinkedBlockingQueue<UUID>();
		busyWorkers = new LinkedBlockingQueue<UUID>();
    workAttempter = new PingingWorkAttempter();
	}

	@Override
	public UUID registerWorker(ComputeServer server) throws RemoteException {
		UUID key = UUID.randomUUID();

		workers.put(key, server);
    try {
      freeWorkers.put(key);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return key;
	}

	@Override
	public boolean unregisterWorker(UUID workerID) throws RemoteException{
		if (null == workers.get(workerID)){
			return true;
		}
		/*
		 * Blindly remove from each queue, even if not present
		 */
		workers.remove(workerID);
		freeWorkers.remove(workerID);
		busyWorkers.remove(workerID);
		return true;
	}

	@Override
	public Object sendWork(WorkTask work) throws RemoteException {
		UUID workerUUID = null;
		ComputeServer worker;
		Object returnVal;
//    return work.doWork();

    try {
      workerUUID = freeWorkers.take();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    worker = workers.get(workerUUID);

    if (worker != null) {
      try {
        busyWorkers.put(workerUUID);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      try {
        returnVal = workAttempter.attemptWork(worker, work, MAXATTEMPTS, PINGTIMEOUT);
        busyWorkers.remove(workerUUID);
        try {
          freeWorkers.put(workerUUID);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        return returnVal;
      } catch (WorkFailedException e) {
        unregisterWorker(workerUUID);
        // call recursively call sendWork below so another
        // server picks up the work
      }
    }
    /*
     * worker should never be null because workerUUID should always be
     * valid if it is, we have a UUID for a server that doesn't exist,
     * so clean up by not re-adding to freeWorkers
     */

		return this.sendWork(work);
	}

	@Override
	public boolean PingServer() throws RemoteException {
		return true;
	}

  public static void main(String[] args) {
      if (System.getSecurityManager() == null) {
          System.setSecurityManager(new DumbSecurityManager());
    // TODO: write policy file
//        if (System.getSecurityManager() == null) {
//            System.setSecurityManager(new SecurityManager());
      }
      try {
        ComputeServer queuedServer = new QueuedServer();
        ComputeServer stub =
            (ComputeServer) UnicastRemoteObject.exportObject(queuedServer, 0);
        Registry registry = LocateRegistry.createRegistry(RMIPORT);
        registry.rebind(RMINAME, stub);
        System.out.println("Group 2 Queued Server bound");
      } catch (Exception e) {
        System.out.println("Group 2 Queued Server exception");
        e.printStackTrace();
      }
  }
}
