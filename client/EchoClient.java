/*
 * Peter Dirks
 * cs 4850
 * Lab 3
 * Due April 28, 2015
 * 
 * Description: a simple chat room that includes a client and a server. This file is the main driver for the client.
 */

import java.io.*;
import java.net.*;

public class EchoClient
{
	public static void main(String args[])
	{
		if(args.length != 1){
			System.out.println("error - include port num in args");
			System.exit(0);
		}
		if( args[0].length() != 5 ){
			System.out.println("error - port must be 5 nums long");
			System.exit(0);
		}
		
		//System.out.println("args length: "+args.length);	// debug
	
		// create stream & buffer reader
		InputStreamReader convert = new InputStreamReader(System.in);
		BufferedReader stdin = new BufferedReader(convert);
		// create socket
		try
		{
			int port = 12345;
			port = Integer.parseInt(args[0]);
			Socket echoClient = new Socket("localhost", port);
			
			// spin up threads
			clientReadThread reader = new clientReadThread(echoClient);
			clientListenThread listener = new clientListenThread(echoClient);
			
			// start threads
			reader.start();
			listener.start();
			
			// loop forever
			while(true){}
			
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
		
	}
}


