/*
 * Peter Dirks
 * cs 4850
 * Lab 3
 * Due April 28, 2015
 * 
 * Description: a simple chat room that includes a client and a server. This file contains one of the 
 * 		threads used by the client
 */
import java.lang.Thread;
import java.io.*;
import java.net.*;

class clientListenThread extends Thread {
	private Thread t;
	private BufferedReader ins;

/*
 * constructor - creates buffered reader when called
 */
	clientListenThread( Socket echoClient ){
		//System.out.println("[debug] creating listen thread");	// debug
		try{
			ins = new BufferedReader(new InputStreamReader(echoClient.getInputStream()));
		} catch(Exception e){
			System.out.println("some problem with ins...");// debug
		}
	}// end constructor

/*
 * start() - intializes thread
 */
	public void start(){
		//System.out.println("[debug] starting listen thread");	// debug
		t = new Thread(this, "clientListenThread");
		t.start();
	}// end start
	
/*
 * run() - (actual processes of thread) waits for input from server, then prints to screen
 */
	public void run(){
		while(true){
			try{
				String input = ins.readLine();
				if(input.equals("null")){
					System.exit(0);
				}
				System.out.println(input);
				
			} catch( IOException e){
				System.out.println("server print error");// debug
			}
		}// end listen loop
	}// end run
	
}// end clientListenThread