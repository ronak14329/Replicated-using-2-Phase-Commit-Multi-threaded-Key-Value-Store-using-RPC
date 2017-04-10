package client;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper {

	 public int[] serverPorts = new int[5];
	 public String key;
	 public String value;
	 public String functionality;
	 
	 public void ClientParseArgs(String [] args) throws Exception {
		 if(args[0].equalsIgnoreCase("Quit"))
			 System.exit(serverPorts[0]);
		 else
		 {
		 	 if (args.length != 5) 
		 	 {
		 	 	 String message = "You specified just " + args.length + " arguments.\n" + "Please specify 5 ports";
		 	  	 throw new IllegalArgumentException(message);
		 	 	}
		 	  for (int i = 0 ; i < args.length; i++)
		 	 {
		 	   serverPorts[i] = Integer.parseInt(args[0]);
		 	 }
		 }
	 }
	 
	 public static String getCurrentTimeStamp() 
	 {
	 	 return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date());
	 }
	 
	 public void log(String message) 
	 {
	 	 System.out.println(getCurrentTimeStamp() + ": " + message);
	 }
}
