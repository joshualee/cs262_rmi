package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import workTasks.SleepTask;
import workerServer.QueuedServer;

public class SimpleClient {
	
	public static void main(String args[]) {
		int rmiPort;
		String rmiHost, rmiName;
		SleepTask sleepTask1 = new SleepTask(1);
		Integer returnVal;
		
		if (args.length != 3) {
			System.err.println("usage: java SimpleClient host port rminame");
			System.exit(1);
		}
		
		rmiHost = args[0];
		rmiPort = Integer.parseInt(args[1]);
		rmiName = args[2];
		
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            Registry registry = LocateRegistry.getRegistry(rmiHost, rmiPort);
            QueuedServer server = (QueuedServer) registry.lookup(rmiName);

            // make some tasks
            returnVal = (Integer) server.sendWork(sleepTask1);
            
            System.out.println(returnVal);
            
        } catch (Exception e) {
            System.err.println("Simple Client exception:");
            e.printStackTrace();
        }
    } 
}
