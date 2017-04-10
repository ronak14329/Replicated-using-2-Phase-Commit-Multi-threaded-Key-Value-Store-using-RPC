package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import compute.ServerInterface;

public class KeyList {
	
	private Helper hl = new Helper();
	private String fileName;

	public KeyList(String fileName)
	{
		this.fileName = fileName;
	}
	
	public String isInStore(String queryKey) throws IOException 
	{
		Properties configProperties = new Properties();
		String file = new File("").getAbsolutePath();
		file = file + File.pathSeparator + fileName;
		File f = new File(file);
		f.createNewFile();
		FileInputStream configFStream = new FileInputStream(file);
		configProperties.load(configFStream);
		String value = configProperties.getProperty(queryKey);
		configFStream.close();
		return value;
	}

	public boolean putInStore(String key, String value) throws IOException 
	{
		boolean result = false;
		String file = new File("").getAbsolutePath();
		file = file + File.pathSeparator + fileName;
		File f = new File(file);
		f.createNewFile();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		String line = "";
		List list = new ArrayList();
		int count = 0;
		while ((line = bufferedReader.readLine()) != null) 
		{
			if (line.contains(key)) 
			{
				line += ", " + value;
				count++;
			}
			list.add(line);
		}
		if (count == 0) 
		{
			list.add(key + "=" + value);
		}
		bufferedReader.close();
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) 
		{
			line = (String) iterator.next();
			bufferedWriter.write(line);
			bufferedWriter.newLine();
		}
		bufferedWriter.close();
		String returnValue = isInStore(key);
		if (returnValue.contains(value)) 
		{
			System.out.println("The value is " + returnValue);
			result = true;
		}
		return result;
	}

	public String deleteKeyValue(String key) throws IOException 
	{
		String value = isInStore(key);
		String message = "";
		if (value.isEmpty()) 
		{
			message = "Key not found";
		} else 
		{
			String file = new File("").getAbsolutePath();
			file = file + File.pathSeparator + fileName;
			File f = new File(file);
			f.createNewFile();
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					file));
			String line = "";
			List list = new ArrayList();
			while ((line = bufferedReader.readLine()) != null) 
			{
				if (!(line.contains(key))) 
				{
					list.add(line);
				}
			}
			bufferedReader.close();
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
					file));
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				line = (String) iterator.next();
				bufferedWriter.write(line);
				bufferedWriter.newLine();
			}
			bufferedWriter.close();
			message = "Key " + key + " is deleted";
		}
		return message;
	}
}