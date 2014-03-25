package edu.harvard.cs262.ComputeServer;

/**
 * Encapsulates all the logic needed to send work to
 * a {@link edu.harvard.cs262.ComputeServer.ComputeServer}, while also
 * pinging it to make sure that it is still running.
 */

public interface WorkAttempter {
  /**
   * Attempts to have worker perform WorkTask
   * @param worker The worker to perform the WorkTask
   * @param work The WorkTask
   * @param maxAttempts The maximum number of times the worker can fail to respond to ping in time
   * @param pingTimeout The amount of time given to respond to each ping, before considered a failed attempt
   * @return The result of the WorkTask
   * @throws WorkFailedException If the worked fails to respond to ping within pingTimeout seconds more than
   * maxAttempts times.
   */
  public Object attemptWork(ComputeServer worker, WorkTask work, int maxAttempts, int pingTimeout) throws WorkFailedException;

}
