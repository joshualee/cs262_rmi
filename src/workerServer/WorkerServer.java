package workerServer;

import edu.harvard.cs262.ComputeServer.ComputeServer;
import edu.harvard.cs262.ComputeServer.WorkTask;

import java.rmi.RemoteException;

/**
 * Created by perry on 3/13/14.
 */
public class WorkerServer implements ComputeServer {
  @Override
  public Object sendWork(WorkTask work) throws RemoteException {
    return work.doWork();
  }

  @Override
  public boolean PingServer() throws RemoteException {
    return true;
  }

  private boolean registerSelf() {
    return true;
  }

  public static void main() {

  }
}
