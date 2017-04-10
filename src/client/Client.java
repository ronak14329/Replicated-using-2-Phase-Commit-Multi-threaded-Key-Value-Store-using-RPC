package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;
import compute.ServerInterface;

public class Client {

	public static void main(String[] args) throws Exception 
	{	
    	Helper hl = new Helper();
		hl.ClientParseArgs(args);
		ServerInterface[] stubs = new ServerInterface[5];
		Registry[] registries = new Registry[5];

	try {
		for (int i = 0 ; i < hl.serverPorts.length ; i++)
			{
		    registries[i] = LocateRegistry.getRegistry("LOCALHOST",hl.serverPorts[0]);
	     stubs[i] = (ServerInterface) registries[i].lookup("compute.ServerInterface");  
			}
		
			hl.log(stubs[0].KeyValue(UUID.randomUUID(), "PUT", "s1", "1"));	    
		    hl.log(stubs[1].KeyValue(UUID.randomUUID(), "PUT", "s2", "2"));	
		    hl.log(stubs[2].KeyValue(UUID.randomUUID(), "PUT", "s3", "3"));	
		    hl.log(stubs[3].KeyValue(UUID.randomUUID(), "PUT", "s4", "4"));	
		    hl.log(stubs[4].KeyValue(UUID.randomUUID(), "PUT", "s5", "5"));	
		  
		    hl.log(stubs[0].KeyValue(UUID.randomUUID(), "GET", "s1", ""));	    
		    hl.log(stubs[1].KeyValue(UUID.randomUUID(), "GET", "s2", ""));	
		    hl.log(stubs[2].KeyValue(UUID.randomUUID(), "GET", "s3", ""));	
		    hl.log(stubs[3].KeyValue(UUID.randomUUID(), "GET", "s4", ""));	
		    hl.log(stubs[4].KeyValue(UUID.randomUUID(), "GET", "s5", ""));	
		  
		    
		    hl.log(stubs[0].KeyValue(UUID.randomUUID(), "DEL", "s1", ""));	   
		    hl.log(stubs[4].KeyValue(UUID.randomUUID(), "DEL", "s2", ""));	
		    hl.log(stubs[3].KeyValue(UUID.randomUUID(), "DEL", "s3", ""));	
		    hl.log(stubs[3].KeyValue(UUID.randomUUID(), "DEL", "s4", ""));	
		    hl.log(stubs[4].KeyValue(UUID.randomUUID(), "DEL", "s5", ""));
		 
		    hl.log(stubs[0].KeyValue(UUID.randomUUID(), "PUT", "t1", "1"));	    
		    hl.log(stubs[1].KeyValue(UUID.randomUUID(), "PUT", "t2", "2"));	
		    hl.log(stubs[2].KeyValue(UUID.randomUUID(), "PUT", "t3", "3"));	
		    hl.log(stubs[3].KeyValue(UUID.randomUUID(), "PUT", "t4", "4"));	
		    hl.log(stubs[4].KeyValue(UUID.randomUUID(), "PUT", "t5", "5"));	
		   
		System.out.print("Please enter machine number, function and values:");
		System.out.print("We have 5 machines, you should choose between 0-4" + "\n");
		System.out.print("If it is a PUT, the input format is: <SERVER No.> <PUT> <KEY> <VALUE>" +  "\n" );
		System.out.print("If it is a GET or DEL, the format is: <SERVER No.> <GET/DEL> <KEY>" + "\n");
		System.out.print("Example  0 PUT 100 188 or 0 DEL 100"+ "\n");
		String input = GetStringFromTerminal();
		
		String[] formattedInput = input.split(" ");
		if (formattedInput.length == 3)
		{
		  hl.log(stubs[Integer.parseInt(formattedInput[0])].KeyValue(UUID.randomUUID(), formattedInput[1], formattedInput[2], ""));
		}
		if (formattedInput.length == 4)
		{
			hl.log(stubs[Integer.parseInt(formattedInput[0])].KeyValue(UUID.randomUUID(), formattedInput[1], formattedInput[2], formattedInput[3]));
		}
		 
	    for (int i = 0; i < hl.serverPorts.length ; i++)
	    	{
	    	System.exit(hl.serverPorts[i]);
	    	}
	    
		} catch (Exception e) {
		hl.log(e.getMessage());
		} 
		}  
	
public static String GetStringFromTerminal() throws IOException 
{
				BufferedReader stringIn = new BufferedReader (new InputStreamReader(System.in));
				return  stringIn.readLine();
			}
}

	   
  