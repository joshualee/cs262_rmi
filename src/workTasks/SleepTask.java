package workTasks;

import edu.harvard.cs262.ComputeServer.WorkTask;

/**
 * Created by perry on 3/13/14.
 */
public class SleepTask implements WorkTask {
  private final int SECONDS = 10;

  @Override
  public Object doWork() {
    try {
      Thread.sleep(1000 * this.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return null;
  }
}
