package workTasks;

import edu.harvard.cs262.ComputeServer.WorkTask;

import java.io.Serializable;

/**
 * Created by perry on 3/13/14.
 */
public class SleepTask implements WorkTask, Serializable {
  private int sleepSeconds;
  private static final long serialVersionUID = 1L;

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
