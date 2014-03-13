package workTasks;

import edu.harvard.cs262.ComputeServer.WorkTask;

/**
 * Created by perry on 3/13/14.
 */
public class SleepTask implements WorkTask {
  private int sleepSeconds;
  
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
