package edu.harvard.cs262.ComputeServer;

/**
 * A WorkTask is the general interface to a closure that can be passed around to
 * be done on some other machine</p>
 * 
 * The WorkTask will need to be self-contained; that is, it needs to contain all
 * of the data that will be needed for the work (or handles in a
 * location-independent form that allows the data to be found) and the code that
 * actually does the work. It returns an Object, which the receiver will need to
 * cast into the correct form. There is a single method, {@code doWork()}, that
 * will be called by the receiver of the object to get the work done.
 * 
 */
public interface WorkTask {
	/**
	 * The one method that will cause the closure encapsulated by the object
	 * implementing this interface to be done. All of the work that is performed
	 * in this method should reference either data that is held inside of the
	 * object itself or is accessed through interfaces that are known to be
	 * available on the server.
	 * 
	 * @return an {@link Object} that can be cast into something more useful,
	 *         that contains any information that is the result of the method
	 */
	public Object doWork();
}
