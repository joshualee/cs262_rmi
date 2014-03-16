package edu.harvard.cs262.ComputeServer;

/**
 * A WorkAttempter encapsulates all the logic needed to send work to
 * a {@link edu.harvard.cs262.ComputeServer.ComputeServer}, while also
 * pinging it to make sure that it is still running.
 */

public interface WorkAttempter {

  public Object attemptWork(ComputeServer worker, WorkTask work, int maxAttempts, int pingTimeout) throws WorkFailedException;

}
