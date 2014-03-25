package workTasks;

import edu.harvard.cs262.ComputeServer.WorkTask;

import java.io.File;
import java.io.Serializable;
import java.util.Scanner;
/**
 * Has the Worker print something to their stdout, then returns "I printed this: "
 * followed by what it printed.
 */
public class PrintTask implements WorkTask, Serializable {
  private String str;
  private static final long serialVersionUID = 1L;

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
