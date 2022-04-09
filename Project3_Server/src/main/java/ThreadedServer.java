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
private Integer port;
private static ArrayList<ClientRunnable> cl2;
private Queue<ClientRunnable> gameQueue;
	
	
	//no access to Thread methods
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
        	
        	//this is with class implementing runnable
        	threadCheck2();
        	ClientRunnable cr = new ClientRunnable(mysocket.accept(), count);
        	Thread t = new Thread(cr);
        	cl2.add(cr);
        	gameQueue.add(cr);
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
	public void run()  {
		
		try {
			Runnable serverCodeRunnable = new Runnable() {

				@Override
				public void run() {
					serverCode();
					
				}
				
			};
			
			cl2 = new ArrayList<ClientRunnable>();
			gameQueue = new LinkedList<>();
			//serverCode();
			Thread scr = new Thread(serverCodeRunnable);
			scr.start();
			
			while(true) {
				pairUpPlayers();
			}
			

			
		} catch(Exception e) {
			e.printStackTrace();
		}
		}

		
	//}
	

	private void pairUpPlayers() {
		if (gameQueue.size() > 1) {
			ClientRunnable p1 = gameQueue.poll();
			ClientRunnable p2 = gameQueue.poll();
			p1.mi.playerNumber = 1;
			p2.mi.playerNumber = 2;
			GameLogicServer game = new GameLogicServer(p1, p2);
			Thread gameThread = new Thread(game);
			gameThread.start();
		}
	}

	// Only Nested Classes below this point
	class GameLogicServer implements Runnable{
		boolean stillPlaying = true;
		ArrayList<ClientRunnable> clientList;
		MorraInfo serverDB;
		ClientRunnable p1;
		ClientRunnable p2;
		GameLogicServer(ClientRunnable p1, ClientRunnable p2){
			this.p1 = p1;
			this.p2 = p2;
		}
		
		@Override
		public void run() {
			clientList = cl2;
			serverDB = new MorraInfo();
			serverDB.p1Points = 0;
			serverDB.p2Points = 0;
			
			while(true) {
				try {
					TimeUnit.SECONDS.sleep(3);
					updateGameState();
				}
				catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
			
			
		}
		public void updateGameState() {
			System.out.println("Game in session! with players:" + "Player:" + p1.count + " and Player: " + p2.count);
		}
	}
	
	
	class ClientRunnable implements Runnable{
		MorraInfo mi;
		Socket connection;
		int count;
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
				in = new ObjectInputStream(connection.getInputStream());
				out = new ObjectOutputStream(connection.getOutputStream());
				mi = new MorraInfo();
				connection.setTcpNoDelay(true);
			}
			catch(Exception e) {
				System.out.println("Streams not open");
			}
				
		
			
			 while(true) {
				    try {
				    	mi = (MorraInfo) in.readObject();
				    	//String data = in.readObject().toString();
				    	System.out.println("Server received: " +  " from client: " + count);
				    	out.writeObject(mi);
				    	//out.writeObject(data.toUpperCase());
				    	}
				    	catch(Exception e) {
				    		System.out.println("OOOOPPs...Something wrong with the socket from client: " + count +"....closing down!");
				    		try {
								connection.close();
							} catch (IOException e1) {
								
								e1.printStackTrace();
							}
				    		break;
				    	}
			}
			
		}
		
	}

		
}

