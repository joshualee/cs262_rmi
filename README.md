CS262 RMI (Group 2)
=============

A three-tier, queued compute server system using RMI built for CS262: Introduction to Distributed Systems (Harvard Spring 2014).

Authors
-------
* Joshua Lee
* Andrew Liu
* Perry Green
* Esmail Fadae

Usage
-----------------
####WorkQueue

	java -Djava.security.policy=server.policy -Djava.rmi.server.codebase=http://example.com/rmi262.jar -Djava.rmi.server.useCodebaseOnly=false -classpath <path>workerServer.QueuedServer rmiPort rmiName

####ComputeServer

	 java -Djava.security.policy=server.policy -Djava.rmi.server.codebase=http://example.com/rmi262.jar -Djava.rmi.server.useCodebaseOnly=false -classpath <path> workerServer.WorkerServer rmiHost rmiPort rmiName

####Client

	java -Djava.security.policy=client.policy -Djava.rmi.server.codebase=http://example.com/rmi262.jar -Djava.rmi.server.useCodebaseOnly=false -classpath <path> client.SimpleClient rmiHost rmiPort rmiName