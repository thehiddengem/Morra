import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
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
Set<Integer> availableClients;

	
	
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
			availableClients = new HashSet<Integer>();
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
		else if (p.messageType == 1) {
			multiMessage(p);
		}

		}
	private void addPacket(DataPacket d) {
		DataPacket temp = new DataPacket();
		temp = d;
		packetQueue.add(temp);
	}
	private synchronized void addClient(Integer c) {
		availableClients.add(c);
	}
	
	private synchronized void remClient(ClientRunnable client, Integer c) {
		int index = cl2.indexOf(client);
		cl2.remove(index);
		availableClients.remove(c);
	}
	
	private void updateClients() {
		DataPacket temp = new DataPacket();
		temp.messageType = 2;
		temp.message = "Updating clients list!\n";
		temp.onlineClients = availableClients;
		// Sender is the server
		temp.clientNumber = -1;
		synchronized(this) {
		for (ClientRunnable client : cl2) {
			try {
				client.out.writeObject(temp);
				client.out.reset();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		}
		
		System.out.println("Sent update packet to all clients!");
		String log = "Update Packet: ";
		for (Integer c : availableClients) {
			log += "Client: ";
			log += c;
			log += " ";
		}
		System.out.println(log);
		
		
	}
	
	private void multiMessage(DataPacket d) {
		Set<Integer> r = new HashSet<Integer>();
		Set<ClientRunnable> exist = new HashSet<>();
		ClientRunnable origin = null;
		r = d.receivers;
		synchronized(this) {
		for (ClientRunnable client : cl2) {
			if (r.contains(client.count)) {
				exist.add(client);
				r.remove(client.count);
			}
			if (d.clientNumber == client.count) {
				origin = client;
			}
		}

		
		d.message = "[Solo/Group] " + "Client " + d.clientNumber + ": " + d.message + "\n";
		String s = d.message;

		for (ClientRunnable client : exist) {
			try {
				//System.out.println(d.message);
				client.out.writeObject(d);
				client.out.reset();
			} catch (IOException e) {
				System.out.println("Attempt to send multimessage failed");
			}
		}
		s += "\n";
		if (r.isEmpty() == false) {
			s += "Failures: ";
			for (Integer i : r) {
				
				s += "Client ";
				s += i + " ";
			}
			s += "\n";
			s += "Used the correct format?  \"Hello World @2 @1\"\n";
		}
		
		d.message = s;
		
		try {
			origin.out.writeObject(d);
			origin.out.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		}
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
			addClient(this.count);
			updateClients();

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
				    	
				    	}
				    	catch(Exception e) {
				    		System.out.println("OOOOPPs...Something wrong with the socket from client: " + count +"....closing down!");
				    		try {
								connection.close();
								remClient(this, this.count);
								updateClients();
														
							} catch (IOException e1) {
								e1.printStackTrace();
							}
				    		break;
				    	}
			}
			
		}
		
	}

		
}

