package workerServer;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.UUID;

import edu.harvard.cs262.ComputeServer.ComputeServer;
import edu.harvard.cs262.ComputeServer.WorkQueue;
import edu.harvard.cs262.ComputeServer.WorkTask;

public class QueuedServer implements ComputeServer, WorkQueue {

	private Hashtable<UUID, ComputeServer> workers;
	private LinkedList<UUID> freeWorkers, busyWorkers;
	
	private QueuedServer(){
		super();
		workers = new Hashtable<UUID, ComputeServer>();
		freeWorkers = new LinkedList<UUID>();
		busyWorkers = new LinkedList<UUID>();
	}
	
	@Override
	public UUID registerWorker(ComputeServer server) throws RemoteException {
		UUID key = UUID.randomUUID();
		workers.put(key, server);
		freeWorkers.add(key);
		return key;
	}

	@Override
	public boolean unregisterWorker(UUID workerID) throws RemoteException{
		if (null == workers.get(workerID)){
			return true;
		}
		
		workers.remove(workerID);
		freeWorkers.remove(workerID);
		busyWorkers.remove(workerID);
		return true;
	}
		
	@Override
	public Object sendWork(WorkTask work) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean PingServer() throws RemoteException {
		return true;
	}

}
