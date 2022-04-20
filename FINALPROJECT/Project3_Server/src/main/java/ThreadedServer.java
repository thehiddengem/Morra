import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

public class ThreadedServer implements Runnable{
	
ThreadedServer(Integer port){
	this.port = port;
}
private static boolean debug = false;
private Integer port;
static ArrayList<ClientRunnable> cl2;
Queue<DataPacket> packetQueue;

	
	
	public void threadCheck2(){
		
		for(int i = 0; i<cl2.size(); i++) {
			ClientRunnable t = cl2.get(i);
			System.out.println("Client #: " + t.count);
		}
	}
	
	public void serverCode(){
	
		int count = 1;
		
		try(ServerSocket mysocket = new ServerSocket(port);){
        System.out.println("Server is waiting for a client!");
		
        while(true) {
        	
        	
        	threadCheck2();
        	ClientRunnable cr = new ClientRunnable(mysocket.accept(), count);
        	Thread t = new Thread(cr);
        	cl2.add(cr);
        	t.start();
        	
		count++;
        }
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println("Server socket did not launch");
		}
		
	
	}
	

	@Override
	public void run()  { //Threaded server's run
		
		try {
			Runnable serverCodeRunnable = new Runnable() {  //Used to separate serverCode() into own thread. serverCode() has blocking calls

				@Override
				public void run() {
					serverCode();
					
				}
				
			};
			
			cl2 = new ArrayList<ClientRunnable>();
		    packetQueue = new LinkedList<>();
			//serverCode();
			Thread scr = new Thread(serverCodeRunnable);
			scr.start();
			
			while(true) {
				if (debug ) {
				 System.out.println("processPacket() loop...");
				}
				if(!packetQueue.isEmpty()) {
				processPacket();
				}
				
				TimeUnit.SECONDS.sleep(1);
				
			}
			

			
		} catch(Exception e) {
			e.printStackTrace();
		}
		}

		
	//}
	
	// processPacket():
	// Server gets packet.
	// Server checks packet type. If broadcast , send to all players. If multi-message, send to all listed players
	private void processPacket() {
		DataPacket p = packetQueue.poll();
		if (p.messageType == 0) {
			broadCast(p);
		}

		}
	private void addPacket(DataPacket d) {
		DataPacket temp = new DataPacket();
		temp = d;
		packetQueue.add(temp);
	}
	
	private void broadCast(DataPacket d) {
		d.message = "[All] " + "Client " + d.clientNumber + ": " + d.message;
		synchronized(this) {
			for (ClientRunnable client : cl2) {
				try {
					//System.out.println(d.message);
					client.out.writeObject(d);
					client.out.reset();
				} catch (IOException e) {
					System.out.println("Attempt to send broadcast failed");
				}
			}
		}
		
	}
	
	public void printPlayersInfo() {
		String s = "";
		s += "Printing Player ArrayList...\n";
		for (ClientRunnable c: cl2) {
			try {
			s +="Player " + c.count + ": isClosed:" + c.connection.isClosed() + "\n";
			}
			catch (Exception e) {
			s += " Error adding player so string in printPlayersInfo \n";
			}
			
		}
		s += "Printing Stored DataPackets...\n";
		for (DataPacket d: packetQueue) {
			try {
			s +="Player " + d.clientNumber + ": Message: " + d.message + "\n";
			}
			catch (Exception e) {
			s += " Error adding player so string in printPlayersInfo \n";
			}
			
		}
		System.out.println(s);
	}

	// Only classes NESTED inside ThreadedServer below this point----------------------------------------------> These classes can use ThreadedServers's datamembers and cuntions
	
	
	class ClientRunnable implements Runnable{
		boolean inGame = false;
		DataPacket dp;
		Socket connection;
		int count;
		int points = 0;
		int wins = 0;
		ObjectInputStream in;
		ObjectOutputStream out;
		
		ClientRunnable(Socket s, int count){
			this.connection = s;
			this.count = count;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				in =  new ObjectInputStream(connection.getInputStream());
				out = new ObjectOutputStream(connection.getOutputStream());
				dp =  new DataPacket();
				connection.setTcpNoDelay(true);
				dp.clientNumber = count;
				dp.message = "Welcome to the Server... \nFetching available clients for you!";
				dp.messageType = 3;
				out.writeObject(dp);
				out.reset();
				
			}
			catch(Exception e) {
				System.out.println("Streams not open");
			}
				
		
			
			 while(true) {
				    try {
				    	
						dp = (DataPacket) in.readObject();
						dp.clientNumber = count;
				    	System.out.println("Server received datapacket: " +  " from client: " + count);
				    	addPacket(dp);
				    	
				    	
				    	//out.writeObject(mi);
				    	//out.writeObject(data.toUpperCase());
				    	}
				    	catch(Exception e) {
				    		System.out.println("OOOOPPs...Something wrong with the socket from client: " + count +"....closing down!");
				    		try {
								connection.close();
								if (inGame == false) {
									// If client disconnects, and is not in a game, removes them from priorityQueue, and arrayList.
									int index = cl2.indexOf(this);
									cl2.remove(index);
								}
								
							} catch (IOException e1) {
								e1.printStackTrace();
							}
				    		break;
				    	}
			}
			
		}
		
	}

		
}

