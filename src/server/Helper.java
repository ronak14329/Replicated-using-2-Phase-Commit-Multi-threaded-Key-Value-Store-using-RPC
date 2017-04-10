package server;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper {
	
	public int port;
	
	 public int[] servers = new int[5];
	 
	public void ServerParseArgs(String[] args) throws Exception {
	 	  if (args.length < 5) {
	 	 	 String message =  "Please specify Port Numbers ";
	 	 	 throw new IllegalArgumentException(message);
	 	 }
	 	 for (int i = 0 ; i < args.length ; i++)
	 	 {
	 	    servers[i] = Integer.parseInt(args[i]);
	 	 }
		
	 }
	 
	 public static String getCurrentTimeStamp() {
	 	 return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date());
	 }
	 
	 public void log(String message) {
	 	  System.out.println(getCurrentTimeStamp() + ": " + message);
	 }
	 
	 public void log(String format, Object[] objs)
	 {
		 System.out.println(String.format(format, objs));
	 }
}
