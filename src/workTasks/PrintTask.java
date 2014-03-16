package workTasks;

import edu.harvard.cs262.ComputeServer.WorkTask;

import java.io.File;
import java.util.Scanner;
/**
 * Created by perry on 3/16/14.
 */
public class PrintTask implements WorkTask {
  private String str;

  public PrintTask() {
    this.str = fileAsString("./src/workTasks/trollface.txt");
  }
  /*
  http://stackoverflow.com/questions/3402735/what-is-simplest-way-to-read-a-file-into-string
   */
  private String fileAsString(String filename) {
    try {
      return (new Scanner(new File(filename)).useDelimiter("\\Z")).next();
    } catch (Exception e) {
      e.printStackTrace();
      return "uh oh";
    }
  }

  public PrintTask(String str) {
    this.str = str;
  }

  @Override
  public Object doWork() {
    System.out.println(str);
    try {
      Thread.sleep(4);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return "I printed this:\n\n"+str;
  }
}
