package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import compute.ServerInterface;
	

public class Server extends Thread implements ServerInterface 
{
	static Helper hl = new Helper();
	private int[] otherServers = new int[4];
	private  int myPort;
	private  Map<UUID, Value> pendingChanges = Collections.synchronizedMap(new HashMap<UUID, Value>());
	private  Map<UUID,Map<Integer,Ack>> pendingPrepareAcks = Collections.synchronizedMap(new HashMap<UUID,Map<Integer,Ack>>());
	private  Map<UUID,Map<Integer,Ack>> pendingGoAcks = Collections.synchronizedMap(new HashMap<UUID,Map<Integer,Ack>>());
	ReadWriteLock rwl=new ReadWriteLock();
	
	public String KeyValue(String functionality,String key,String value) 
		{    
			String message="";	
			String fileName = "keyValueStore_" + myPort + ".txt";
			KeyList k1=new KeyList(fileName);
			try {	
			if (functionality.equalsIgnoreCase("GET")) 
					{
						hl.log("Looking for key: "
						+ key
						+ " - from client: ");
						rwl.lockRead();
					message += key + " : "
									+ k1.isInStore(key);
						rwl.unlockRead();
				} else if (functionality.equalsIgnoreCase("PUT"))
						{
							hl.log("Writing the key: "
										+ key
										+ " and value: "
										+ value
										+ " - from client: ");
							rwl.lockWrite();
							message += key + " : "
										+ k1.putInStore(key, value);
							rwl.unlockWrite();
							} else 
								{
								hl.log("Deleting "+key);
								rwl.lockWrite();
									message += key + " : "
											+ k1.deleteKeyValue(key);
										rwl.unlockWrite();
								}																		
					}			
					catch (Exception e) 
					{
						hl.log(e.getMessage());
					}
					return(message + "\n");
			}
	
	public String KeyValue(UUID messageId, String functionality, String key,
			String value) throws RemoteException 
		{
		if (functionality.equalsIgnoreCase("GET"))
		{
			return KeyValue(functionality, key, value);
		}
		addToTempStorage(messageId,  functionality,  key, value);
		tellToPrepare(messageId, functionality, key, value);
		boolean prepareSucc = waitAckPrepare(messageId, functionality, key, value);
		if (!prepareSucc)
		{
			return "fail";
		}
		
		tellToGo(messageId);
		boolean goSucc = waitToAckGo(messageId);
		if (!goSucc)
		{
			return "fail";
		}
		
		Value v = this.pendingChanges.get(messageId);
		
		if ( v == null)
		{
			throw new IllegalArgumentException("The message is not in the storage");
		}
		
		String message = this.KeyValue(v.function, v.key, v.value);
		this.pendingChanges.remove(messageId);
		return message;
	}

	private boolean waitToAckGo(UUID messageId) {

		int areAllAck = 0;
		int retry = 3;
		
		while (retry != 0)
		{
			try{
			  Thread.sleep(100);
			}catch(Exception ex)
			{
				hl.log("wait fail.");
			}
			
			areAllAck = 0;
			retry--;
			Map<Integer,Ack> map = this.pendingGoAcks.get(messageId);
			
			for (int server : this.otherServers)
			{
				if (map.get(server).isAcked)
				{
					areAllAck++;
				}
				else
				{
					callGo(messageId, server);
				}
			}
			if (areAllAck == 4)
			{
				return true;
			}
		}
		
		return false;
	}

	private boolean waitAckPrepare(UUID messageId, String functionality, String key, String value) {
		
		int areAllAck = 0;
		int retry = 3;
		
		while (retry != 0)
		{
			try{
			  Thread.sleep(100);
			}catch(Exception ex)
			{
				hl.log("wait fail.");
			}
			areAllAck = 0;
			retry--;
			Map<Integer,Ack> map = this.pendingPrepareAcks.get(messageId);
			for (int server : this.otherServers)
			{
				if (map.get(server).isAcked)
				{
					areAllAck++;
				}
				else
				{
					callPrepare(messageId, functionality, key, value, server);
				}
			}
			
			if (areAllAck == 4)
			{
				return true;
			}
		}
		
		return false;
	}

	private void tellToPrepare(UUID messageId, String functionality, String key,
			String value) {
		
		this.pendingPrepareAcks.put(messageId, Collections.synchronizedMap(new HashMap<Integer,Ack>()));
		
		for (int server : this.otherServers)
		{
			callPrepare(messageId, functionality, key, value, server);
		}
		
	}
    
	private void tellToGo(UUID mesUuid)
	{
		this.pendingGoAcks.put(mesUuid, Collections.synchronizedMap(new HashMap<Integer, Ack>()));
		
		for (int server : this.otherServers)
		{
			callGo(mesUuid, server);
		}
	}
	
	private void callGo(UUID messageId, int server)
	{
		try{
			Ack a = new Ack();
			a.isAcked = false;
			this.pendingGoAcks.get(messageId).put(server, a);
			 Registry registry = LocateRegistry.getRegistry(server);
		     ServerInterface stub = (ServerInterface) registry.lookup("compute.ServerInterface");
		    stub.go(messageId, myPort);
		}catch(Exception ex)
		{
			hl.log("Something went wrong in sending go, removing data from temporary storage");
		}
		
		hl.log("call go for worked. target: " + server);
	}
	
	private void callPrepare(UUID messageId, String functionality, String key,String value, int server)
	{
		try{
			Ack a = new Ack();
			a.isAcked = false;
			this.pendingPrepareAcks.get(messageId).put(server, a);
			 Registry registry = LocateRegistry.getRegistry(server);
		     ServerInterface stub = (ServerInterface) registry.lookup("compute.ServerInterface");
		    stub.prepareKeyValue(messageId, functionality, key, value, myPort);
		}catch(Exception ex)
		{
			hl.log("Something went wrong in sending Ack, removing data from temporary storage");
		}
		
		hl.log("call prepare for worked. target: " + server);
	}

	public void ackMe(UUID messageId, int yourPort, AckType type) throws RemoteException{

         if (type == AckType.ackGo)
         {
        	 this.pendingGoAcks.get(messageId).get(yourPort).isAcked = true ;
         } else if (type == AckType.AkcPrepare)
         {
        	 this.pendingPrepareAcks.get(messageId).get(yourPort).isAcked = true;
         }
         hl.log("Ack received from: " + yourPort);
	}
	
	public void go(UUID messageId, int callBackServer) throws RemoteException{
		
		Value v = this.pendingChanges.get(messageId);
		
		if ( v == null)
		{
			throw new IllegalArgumentException("The message is not in the storage");
		}
		
		this.KeyValue(v.function, v.key, v.value);
		this.pendingChanges.remove(messageId);
		this.sendAck(messageId, callBackServer, AckType.ackGo);
	}
	
	public void prepareKeyValue(UUID messageId, String functionality, String key,
			String value, int callBackServer) throws RemoteException{
		
		if (this.pendingChanges.containsKey(messageId)){
			
			sendAck(messageId, callBackServer, AckType.AkcPrepare);
		}
		
		this.addToTempStorage(messageId, functionality, key, value);
		sendAck(messageId, callBackServer, AckType.AkcPrepare);
	}

	public void setServersInfo(int[] otherServersPorts, int yourPort)
			throws RemoteException {
		
		this.otherServers = otherServersPorts;
		this.myPort = yourPort;
	}
    
	public int getPort() throws RemoteException{
		
		return this.myPort;
	}
	
	private void sendAck(UUID messageId, int destination, AckType type)
	{
		try{
			 Registry registry = LocateRegistry.getRegistry(destination);
		    ServerInterface stub = (ServerInterface) registry.lookup("compute.ServerInterface");
		    
		    stub.ackMe(messageId, myPort, type);
		    
		}catch(Exception ex)
		{
			hl.log("Something went wrong in sending Ack, removing data from temporary storage");
			this.pendingChanges.remove(messageId);
		}
	}
    
	private void addToTempStorage(UUID messageId, String functionality,
			String key, String value) {
		Value v = new Value();
		v.function = functionality;
		v.key = key;
		v.value = value;
	
		this.pendingChanges.put(messageId, v);
	}
}

class Value 
{
	String function; 
	String key;
	String value;
}

class Ack
{
   public boolean isAcked;
}
