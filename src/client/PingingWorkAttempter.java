package client;

import edu.harvard.cs262.ComputeServer.ComputeServer;
import edu.harvard.cs262.ComputeServer.WorkAttempter;
import edu.harvard.cs262.ComputeServer.WorkFailedException;
import edu.harvard.cs262.ComputeServer.WorkTask;

import java.rmi.RemoteException;
import java.util.concurrent.*;

/**
 * Encapsulates the logic behind attempting to get work done, while continuously pinging to
 * make sure that the worked is still responding.
 */
public class PingingWorkAttempter implements WorkAttempter {

  /**
   * Callable that pings the worker once.
   */
  private class PingTask implements Callable<Boolean> {

    private ComputeServer server;

    public PingTask(ComputeServer server) {
      this.server = server;
    }

    public Boolean call() throws RemoteException {
      return server.PingServer();
    }

  }

  /**
   * Callable that sends the WorkTask to the worker.
   */
  private class WorkTaskWrapper implements Callable<Object> {

    private ComputeServer server;
    private WorkTask work;

    public WorkTaskWrapper(ComputeServer server, WorkTask work) {
      this.server = server;
      this.work = work;
    }

    public Object call() throws RemoteException {
      return server.sendWork(work);
    }
  }

  @Override
  /**
   * Attempts to get the WorkTask performed by the worker. If they fail to respond to ping in time
   * more than maxAttempts times, throws a WorkFailed exception.
   */
  public Object attemptWork(ComputeServer worker, WorkTask work, int maxAttempts, int pingTimeout) throws WorkFailedException {
    Future<Boolean> pingFuture;

    ExecutorService pool = Executors.newFixedThreadPool(2);
    PingTask pingCallable = new PingTask(worker);
    WorkTaskWrapper workCallable = new WorkTaskWrapper(worker, work);

    Future<Object> workFuture = pool.submit(workCallable);

    int failedAttempts = 0;
    while (failedAttempts < maxAttempts) {
      pingFuture = pool.submit(pingCallable);

      try {
        pingFuture.get(pingTimeout, TimeUnit.SECONDS);
        if (workFuture.isDone()) {
          return workFuture.get();
        }
        failedAttempts = 0;
        Thread.sleep(1000);
      }
//			catch (InterruptedException|ExecutionException|TimeoutException e) {
      catch (Exception e) {
				/*
				 * Our worker server failed by either:
				 * (1) RMI Remote Exception
				 * (2) Ping timed out (server did not respond in time)
				 * (3) Someone cancelled our ping task
				 */
        failedAttempts++;
      }
    }

		/*
		 * While it is possible that the server may not respond to ping
		 * but will complete the work, we only accept completed work
		 * from a server that also responds to ping.
		 */
    workFuture.cancel(true);

    throw new WorkFailedException();
  }
}
