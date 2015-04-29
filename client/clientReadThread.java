/*
 * Peter Dirks
 * cs 4850
 * Lab 3
 * Due April 28, 2015
 * 
 * Description: a simple chat room that includes a client and a server. This file contains one of the files used by
 * 		the client
 */
import java.lang.Thread;
import java.util.Scanner;
import java.io.*;
import java.net.*;

class clientReadThread extends Thread{
	
	private Thread t;
	private PrintStream outs;
	
/*
 * constructor - creates printstream once called
 */
	clientReadThread( Socket echoClient ){
		try{
			outs = new PrintStream(echoClient.getOutputStream());
		} catch(Exception e){
			System.out.println("[debug] some problem with outs...");// debug
		}
		//System.out.println("[debug] creating read thread");	// debug
	}// end constructor
	
/*
 * start() - intializes thread
 */
	public void start(){
		//System.out.println("[debug] starting read thread");	// debug
		t = new Thread(this, "clientReadThread");
		t.start();
		
	}// end start

/*
 * run() - (what the thread does when actually started) takes user input and sends to the server
 */
	public void run(){
		//System.out.println("[debug] running reader");
		System.out.println("[debug] ready!");
		Scanner sc = new Scanner(System.in);
		String input;
		
		while(true){
			//System.out.print(": ");
			input = sc.nextLine();
			outs.println(input);			
		}// input loop
	}
	
}// end clientReadThread