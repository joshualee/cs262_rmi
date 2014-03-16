package edu.harvard.cs262.ComputeServer;

public interface WorkAttempter {

  public Object attemptWork(ComputeServer worker, WorkTask work, int maxAttempts, int pingTimeout) throws WorkFailedException;

}
