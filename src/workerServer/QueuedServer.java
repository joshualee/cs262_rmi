package workerServer;

import edu.harvard.cs262.ComputeServer.ComputeServer;
import edu.harvard.cs262.ComputeServer.WorkQueue;
import edu.harvard.cs262.ComputeServer.WorkTask;
import securityManagers.DumbSecurityManager;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;
import java.util.concurrent.*;

public class QueuedServer implements ComputeServer, WorkQueue {
	private final int MAXATTEMPTS = 2;
	private final static int RMIPORT = 8888;
	private final int PINGTIMEOUT = 3; // in seconds
	private final static String RMINAME = "G2QueuedServer";

	private ConcurrentHashMap<UUID, ComputeServer> workers;
	private LinkedBlockingQueue<UUID> freeWorkers, busyWorkers;

	private QueuedServer(){
		super();
		workers = new ConcurrentHashMap<UUID, ComputeServer>();
		freeWorkers = new LinkedBlockingQueue<UUID>();
		busyWorkers = new LinkedBlockingQueue<UUID>();
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

	private class PingTask implements Callable<Boolean> {

		private ComputeServer server;

		public PingTask(ComputeServer server) {
			this.server = server;
		}

		public Boolean call() throws RemoteException {
			return server.PingServer();
		}

	}

	private class WorkTaskWrapper implements Callable<Object> {

		private ComputeServer server;
		private WorkTask work;

		public WorkTaskWrapper(ComputeServer server, WorkTask work) {
			this.server = server;
			this.work = work;
		}

		public Object call() throws RemoteException {
			return server.sendWork(work);
		}
	}

	private class WorkServerFailedException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	private Object attemptWork(ComputeServer worker, WorkTask work, int maxAttempts) throws WorkServerFailedException {
		Future<Boolean> pingFuture;

		ExecutorService pool = Executors.newFixedThreadPool(2);
		PingTask pingCallable = new PingTask(worker);
		WorkTaskWrapper workCallable = new WorkTaskWrapper(worker, work);

		Future<Object> workFuture = pool.submit(workCallable);

		int failedAttempts = 0;
		while (failedAttempts < maxAttempts) {
			pingFuture = pool.submit(pingCallable);

			try {
				pingFuture.get(PINGTIMEOUT, TimeUnit.SECONDS);
				if (workFuture.isDone()) {
					return workFuture.get();
				}
				failedAttempts = 0;
			}
//			catch (InterruptedException|ExecutionException|TimeoutException e) {
				catch (Exception e) {
				/*
				 * Our worker server failed by either:
				 * (1) RMI Remote Exception
				 * (2) Ping timed out (server did not respond in time)
				 * (3) Someone cancelled our ping task
				 */
				failedAttempts++;
			}
		}


		/*
		 * While it is possible that the server may not respond to ping
		 * but will complete the work, we only accept completed work
		 * from a server that also responds to ping.
		 */
		workFuture.cancel(true);

		throw new WorkServerFailedException();
	}

	@Override
	public Object sendWork(WorkTask work) throws RemoteException {
		UUID workerUUID = null;
		ComputeServer worker;
		Object returnVal;
    return work.doWork();
//
//    try {
//      workerUUID = freeWorkers.take();
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
//    worker = workers.get(workerUUID);
//
//    if (worker != null) {
//      try {
//        busyWorkers.put(workerUUID);
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
//      try {
//        returnVal = attemptWork(worker, work, MAXATTEMPTS);
//        busyWorkers.remove(workerUUID);
//        try {
//          freeWorkers.put(workerUUID);
//        } catch (InterruptedException e) {
//          e.printStackTrace();
//        }
//        return returnVal;
//      } catch (WorkServerFailedException e) {
//        unregisterWorker(workerUUID);
//        // call recursively call sendWork below so another
//        // server picks up the work
//      }
//    }
//    /*
//     * worker should never be null because workerUUID should always be
//     * valid if it is, we have a UUID for a server that doesn't exist,
//     * so clean up by not re-adding to freeWorkers
//     */
//
//		return this.sendWork(work);
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
