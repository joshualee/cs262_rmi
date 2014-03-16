package edu.harvard.cs262.ComputeServer;

/**
 * A WorkAttempter is an encapsulates all the logic needed to send work
 * to a {@link edu.harvard.cs262.ComputeServer.ComputeServer}
 */

public interface WorkAttempter {

  public Object attemptWork(ComputeServer worker, WorkTask work, int maxAttempts, int pingTimeout) throws WorkFailedException;

}
