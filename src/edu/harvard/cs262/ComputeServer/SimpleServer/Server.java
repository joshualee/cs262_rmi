package edu.harvard.cs262.ComputeServer.SimpleServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

import edu.harvard.cs262.ComputeServer.ComputeServer;
import edu.harvard.cs262.ComputeServer.WorkTask;

public class Server implements ComputeServer {
	private static final long serialVersionUID = 1L;
	public Server(){
		super();
	}
	
	@Override
	public Object sendWork(WorkTask work) throws RemoteException {
		return work.doWork();
	}
	
	public static void main(String args[]){
		try{
			if (System.getSecurityManager()==null){
				System.setSecurityManager(new SecurityManager());
			}
			Server mySrv = new Server();
			ComputeServer stub = (ComputeServer)UnicastRemoteObject.exportObject(mySrv);
			
			Registry registry = LocateRegistry.getRegistry();
			registry.bind("SimpleServer", stub);
			
			System.out.println("Server ready");
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
		}
	}

}
