package workTasks;

import edu.harvard.cs262.ComputeServer.WorkTask;

import java.io.Serializable;

/**
 * A SleepTask is a {@link edu.harvard.cs262.ComputeServer.WorkTask} that sleeps
 * for some period of time, and then returns Integer(1337).
 */
public class SleepTask implements WorkTask, Serializable {
  private int sleepSeconds;
  private static final long serialVersionUID = 1L;

  /**
   * Constructor that takes in a length of time to sleep for before
   * returning.
   *
   * @param sleepSeconds Length of time to sleep before returning, in seconds.
   */
  public SleepTask(int sleepSeconds) {
	  this.sleepSeconds = sleepSeconds;
  }

  @Override
  public Object doWork() {
    try {
      Thread.sleep(this.sleepSeconds * 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return new Integer(1337);
  }
}
