/**
 * 
 */
package edu.harvard.cs262.ComputeServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 * A WorkQueue is a server that allows other {@code ComputeServer} to register
 * with it, and then hands out the work to all the registered servers</p>
 * 
 * This type of server may not do any computation itself, but will allow elastic
 * scaling of the compute server infrastructure by allowing others to register
 * and do work.</p>
 * 
 * This interface does not provide a mechanism for a service implementing this
 * interface to tell if the {@link ComputeServer} registered with the server is
 * still alive. This could cause work to be assigned to a crashed server (which
 * would end up throwing a {@link RemoteException}, or have the server waiting
 * (potentially forever) for the completion of an assigned {@link WorkTask}
 * forever. So their probably needs to be an extension of either this interface
 * or the basic {@link ComputeServer} interface to allow such failure detection.
 * 
 */
public interface WorkQueue extends Remote {
	/**
	 * Register a {@link ComputerServer} with the work queue, allowing that
	 * server to be assigned {@link WorkTask} objects to perform.
	 * 
	 * @param server
	 *            the {@link ComputeServer} to be added to the pool of workers
	 * @return the {@link UUID} generated to identify the worker; this will be
	 *         used in calls to {@link unregisterWorker}
	 * @throws RemoteException
	 */
	public UUID registerWorker(ComputeServer server) throws RemoteException;

	/**
	 * Unregister a {@link ComputeServer} from the pool of workers
	 * 
	 * @param workerID
	 *            the {@link UUID} that identifies the server to be removed from
	 *            the pool; this was returned by {@link registerWorker} when the
	 *            server was added to the pool
	 * @return {@code true} if the removal was successful; {@code false} if not
	 * @throws RemoteException
	 */
	public boolean unregisterWorker(UUID workerID) throws RemoteException;
}
