/* 
 * a simple chat room that includes multiple clients and a server. This file contains the server portion
 * 
 */

import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.Thread;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EchoServer
{
	String sfile = "users.txt";
	ArrayList<String> usernames;
	ArrayList<String> passwords;
	ArrayList<String> text;
	ArrayList<clientServer> threads;

	public ArrayList conn_clients;
	
	public static void main(String args[])
	{
		// arraylist for handling clientThreads
		
		if(args.length != 1){
			System.out.println("error - include port num in args");
			System.exit(0);
		}
		if( args[0].length() != 5 ){
			System.out.println("error - port must be 5 nums long");
			System.exit(0);
		}

		boolean end = false;
		String activeClient="null";
		
		EchoServer d = new EchoServer();
		d.starter(args[0]);
	}// end main
	
	EchoServer(){
		conn_clients = new ArrayList();
		
		threads = new ArrayList<clientServer>();
		usernames = new ArrayList<String>();
		passwords = new ArrayList<String>();
		text = new ArrayList<String>();
	}// end drive
	public void starter(String Sport){
		int MAX_CLIENTS = 3;
		int client_count = 0;
		try
		{ 
			int port = Integer.parseInt(Sport);
			ServerSocket echoServer = new ServerSocket(port);	// may need to use multiple sockets...
			//Try not to use port number < 2000. 
			// active loop to work on
			while(true){
				int thread_index = 0;
				//Serversockets.add( new ServerSocket(port) );
				System.out.println("Waiting for a client to connect...");
				// create new socket for serverSocket
				//sockets.add( echoServer.accept() );
				//if(client_count > MAX_CLIENTS){
					Socket s = echoServer.accept();
					client_count++;
					System.out.println("Client Connected. addr: "+ s.getRemoteSocketAddress().toString());
					// will need to work out  logic for how to index threads after a deletion
					clientServer cl = new clientServer(s, thread_index++);
					synchronized (conn_clients){
						conn_clients.add(cl);
						threads.add(cl);
						cl.start();					 
					}// end sync
				//
			}// end while
			
		}// end try
		catch (IOException e)
		{
			System.out.println(e);
		}// end catch
	}// end starter

	protected class clientServer extends Thread{
		// ends thread when when set to false
		private volatile boolean run = true;
		// vairables for validating the client & flow control
		public String ClientAddr = "";
		boolean valid = false;
		public String activeClient="";
		public String clmessage;
		public PrintWriter pw;
		public String local_usrname;
		Socket s;
		int threadID;
		private Thread t;
		BufferedReader ins;		// read from buffer object
		PrintStream outs;		// print to buffer object
		
		public clientServer( Socket s, int id ) throws IOException{
			local_usrname = "";
			pw = new PrintWriter(s.getOutputStream(), true);
			this.s = s;
			threadID = id;
			try{
				ClientAddr = s.getRemoteSocketAddress().toString();
				//setup reader and stream
				ins = new BufferedReader(new InputStreamReader(s.getInputStream()));
				outs = new PrintStream(s.getOutputStream());
			}
			catch(IOException e){
				System.out.println("error - "+e);
			}
		}// end constructor
		
	/*
	 * run() - actual processes of thread as it runs
	 */
		public void run(){
			loader();
			String activeClient="";
			try{
				System.out.println("[debug] server thread ready! client:"+ClientAddr+" threadID:"+ threadID);
				while(run==true){
					String line = ins.readLine();
					String arr[] = line.split(" ");
					System.out.println("[debug : "+ClientAddr+" ] " +line );
					
					if(arr[0].equals("quit")){
						//System.out.println("[debug] disconnecting client");
						System.out.println("user "+ClientAddr+" logged out");
						kill();
						broadcast("[server] "+activeClient+" has logged out");
						outs.println("null");
						break;
					}
				/*
				 * login handler
				 */
					if( arr[0].equals("login") && valid == false){
						//System.out.println("[debug] attempting login");
						if( usernames.contains(arr[1]) && arr.length > 2 ){
							int tempindex = usernames.indexOf(arr[1]);
							//System.out.println("[debug] username match");
							
							// finally check if password matches
							if( passwords.get(tempindex).equals(arr[2]) ){
								activeClient = arr[1];
								valid = true;
								//append( arr[1], arr[2] );
								//System.out.println("[debug] password match - logging in");
								System.out.println("user "+activeClient+" logged in");
								local_usrname = activeClient;
								//outs.println("[server] logged in");
								broadcast("[server] "+activeClient+" has logged in");
							}// end password match check
							else{
								outs.println("[server] login error");
							}//login fail
						}// end username check
						else{
							outs.println("[server] login error");
						}
					}// end login attempt check
				
				/*
				 * send handler
				 */
					else if( arr[0].equals("send") && arr[1].equals("all") && valid == true ){		
						String regex = "\\s*\\bsend\\b\\s*";
						line = line.replaceAll(regex, "");
						String outLine = "["+activeClient+"] "+line;
						System.out.println(outLine);
						broadcast(outLine);
						//outs.println(outLine);
					}// end send check
					else if( arr[0].equals("send") && valid == true ){
						String target = arr[1];
						String regex = "\\s*\\bsend\\b\\s*";
						line = line.replaceAll(regex, "");
						String outLine = "["+activeClient+"] "+line;
						System.out.println(outLine);
						
						for( int i = 0; i < conn_clients.size(); i++ ){
							clientServer clsr = (clientServer) conn_clients.get(i);
							if(clsr.local_usrname.equals(target)){
								clsr.send(outLine);
							}
						}// end client loop
					}
					else if( arr[0].equals("send") && valid == false ){
						outs.println("[server] need to login first");
					}
				/*
				 * new user handler
				 */
					else if( arr[0].equals("newuser") ){
						if( handleNewUser(arr, outs) ){
							loader();	// reload usernames & passwords
						}
					}// end newuser check
				/*
				 * who handler
				 */
					else if( arr[0].equals("who") && valid == true ){
						outs.print("[server] users: ");
						for( int i = 0; i < conn_clients.size(); i++ ){
							clientServer clsr = (clientServer) conn_clients.get(i);
							outs.print(clsr.local_usrname);
							if( i != conn_clients.size() - 1 )
								outs.print(", ");
						}// end client loop
						outs.println();
					}
					else{
						outs.println("[server] error");
					}
					
						
				}// end main loop
			}
			catch(IOException e){
				System.out.println("runtime error: "+e);
			}
		}// end run

	/*
	 * append() - adds input to users.txt file for storage
	 * input:	uname - new username to enter
	 * 			pass - new password to enter
	 */
		public boolean append(String uname, String pass){	
			try( PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("users.txt", true))) ){
				out.println(uname+","+pass);
			}
			catch(IOException e){
				return false;
			}
			return true;
		}// end append
		
	/*
	 * loader() - loads usernames and passwords
	 */
		public void loader(){
			usernames.clear();
			passwords.clear();
			File file = new File("users.txt");
			//System.out.println("[debug] loading profiles");		// debug
			try {
	            List<String> lines = Files.readAllLines(Paths.get("users.txt"),
	                    Charset.defaultCharset());
	            for (String line : lines) {
	                text.add(line);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			for( String line : text ){
				String [] res = line.split(",");
				//System.out.println("[debug] loading "+res[0]+", "+res[1]+" as users");	// debug
				usernames.add(res[0]);
				passwords.add(res[1]);
			}
		}// end loader

	/* 
	 * handleNewUser() - runs checks on args before creating new user
	 * input:	arr - input from user to process
	 * 			outs - PrintStream to be used to diplay error messages
	 */
		public boolean handleNewUser(String[] arr, PrintStream outs){
			// first we check for proper arg length and if username exists in database already
			if( arr.length == 3 && !usernames.contains(arr[1]) ){				
				//check for username length
				if(arr[1].length() >= 32){
					outs.println("[server] login error: username > 32 char");
					return false;
				}
				// next, we check for password length check
				if( arr[2].length() <= 8 && arr[2].length() >= 4 ){
					append(arr[1], arr[2]);
					outs.println("[server] user added, please login");
					return true;
				}
				else{
					outs.println("[server] login error: password must be between 4 and 8 characters in length");
					return false;
				}
				
			}// username copy check
			else{
				return false;
			}			
		}// end handleNewUser	

	/*
	 * broadcast() - 
	 */
		public void broadcast(String s){
			for( int i = 0; i < conn_clients.size(); i++ ){
				clientServer clsr = (clientServer) conn_clients.get(i);
				clsr.send(s);
			}
		}
	/*
	 * send() - 
	 */
		public void send(String s){
			pw.println(s);
		}
		
	/*
	 * kill() - ends main process
	 */
		public void kill(){
			
		}
		
	}// end clientServer class
}// end class


