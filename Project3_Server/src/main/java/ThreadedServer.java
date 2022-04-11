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
private static ArrayList<ClientRunnable> cl2;
private static ArrayList<GameLogicServer> gl2;
private Queue<ClientRunnable> gameQueue;
	
	
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
	public void run()  { //Threaded server's run
		
		try {
			Runnable serverCodeRunnable = new Runnable() {  //Used to separate serverCode() into own thread. serverCode() has blocking calls

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
				if (debug ) {
				 System.out.println("Pairing up Players loop...");
				}
				pairUpPlayers();
				
				//If there IS player's waiting to play, wait 2 seconds, else wait 5
				if (gameQueue.size() < 2) {
				TimeUnit.SECONDS.sleep(5);
				}
				
			else {
				TimeUnit.SECONDS.sleep(2);
				}
			}
			

			
		} catch(Exception e) {
			e.printStackTrace();
		}
		}

		
	//}
	
	// If gameQueue has more than 2 players waiting, it will pair them up into a game.
	private void pairUpPlayers() {
		// System.out.println(gameQueue.size());
		if (gameQueue.size() > 1) {
		// System.out.println("pairup if Statement");
			ClientRunnable p1 = gameQueue.poll();
			ClientRunnable p2 = gameQueue.poll();
			
			int indexp1 = cl2.indexOf(p1);
			int indexp2 = cl2.indexOf(p2);
			
			// Verify that both clients are still in the ArrayList. If not in List, they disconnected while not inside of a game.
			if (indexp1 == -1 || indexp2 == -1) {
				
				// Which ever client IS in the list gets added back to the queue
				if (indexp1 != -1) {
					gameQueue.add(p1);
				}
				if (indexp2 != -1) {
					gameQueue.add(p2);
				}
				return;
			}
			
			p1.mi.setpNum(1);
			p2.mi.setpNum(2);
			GameLogicServer game = new GameLogicServer(p1, p2);
			Thread gameThread = new Thread(game);
			gameThread.start();
		}
	}
	public void printPlayersInfo() {
		String s = "";
		s += "Printing Player gameQueue...\n";
		for (ClientRunnable p: gameQueue) {
			
			s += "Player " + p.count + " in gameQueue\n";
		}
		s += "Printing Player ArrayList...\n";
		for (ClientRunnable c: cl2) {
			try {
			s +="Player " + c.count + ": isClosed:" + c.connection.isClosed() + "\n";
			}
			catch (Exception e) {
			s += " Error adding player so string in printPlayersInfo \n";
			}
		}
		System.out.println(s);
	}

	// Only classes NESTED inside ThreadedServer below this point----------------------------------------------> These classes can use ThreadedServers's datamembers and cuntions
	class GameLogicServer implements Runnable{
		boolean stillPlaying = true;
		Integer pointsToWin = 5;
		ArrayList<ClientRunnable> clientList;
		MorraInfo serverDB;
		MorraInfo morraP1;
		MorraInfo morraP2;
		ClientRunnable p1;
		ClientRunnable p2;
		GameLogicServer(ClientRunnable p1, ClientRunnable p2){
			this.p1 = p1;
			this.p2 = p2;
			this.morraP1 = p1.mi;
			this.morraP2 = p2.mi;
			p1.inGame = true;
			p2.inGame = false;
		}
		
		@Override
		public void run() { // GameLogicServers run
			clientList = cl2;
			serverDB = new MorraInfo();
			try {
				initGame();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			while(stillPlaying) {
				try {
					if (playersConnected() == false) {
						//Cleanup Code
						
						//Case: Both players quit. Remove them from ArrayList, and return
						if (p1.connection.isClosed() && p2.connection.isClosed()) {
							removePlayerFromList(p1);
							removePlayerFromList(p2);
							return;
						} else if(p1.connection.isClosed()) {
							removePlayerFromList(p1);
							serverDB.setWinner(2);
							serverDB.setPlayerString("Your Opponent has left the game...");
							sendPacketOne(p2);
						} else if (p2.connection.isClosed()){
							removePlayerFromList(p2);
							serverDB.setWinner(1);
							serverDB.setPlayerString("Your Opponent has left the game...");
							sendPacketOne(p1);
						}
						stillPlaying = false;
						// remove the players that disconnected from the ArrayList
						// set serverDb GameOverFlag to true
						// send final serverDb with score and points to remaining player.
					}
					
					if (serverDB.getP1Points() == pointsToWin) {
						serverDB.setWinner(1);
						break;
					}
					else if (serverDB.getP2Points() == pointsToWin) {
						serverDB.setWinner(2);
						break;
					}
					if (stillPlaying) {
					updateGameState();
					}
					
					
					TimeUnit.SECONDS.sleep(2);
					
				}
				catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
			
			// if GameOver {
			// At this point Game has either ended normally, or one of the players has quit. Ask remaining players (by checking if their indexOf in cl2 ArrayList returns anything other than -1
			// If they want to play again. If yes, add them to gameQueue. If no, close their socketConnection, and remove from ArrayList.
			// set stillPlayer to False to break loop. GameLogicServer run() will return, ending the thread.
			
			
		}
		public void initGame() throws IOException {
			serverDB.setP1Points(0);
			serverDB.setP2Points(0);
			serverDB.setPlayerString("Starting Game! First to " + pointsToWin + "wins!");
			initRound();
			serverDB.setTwoPlayers(true);
			serverDB.setWinner(-1);
			sendPacketBoth();
			
			
		}
		public void initRound() throws IOException {
			serverDB.setGuessing(true);
			
			serverDB.setP1Fingers(-1);
			serverDB.setP2Fingers(-1);
			
			serverDB.setP1Guess(-1);
			serverDB.setP2Guess(-1);
			
			
		}
		
		public void updateGameState() {
			if (debug) {
			System.out.println("Game in session! with players:" + "Player:" + p1.count + " and Player: " + p2.count);
			}

			 try {
				 morraP1 = p1.mi;
				 morraP2 = p2.mi;
				 serverDB.setP1Fingers(morraP1.getP1Fingers());
				 serverDB.setP2Fingers(morraP2.getP2Fingers());
				 serverDB.setP1Guess(morraP1.getP1Guess());
				 serverDB.setP2Guess(morraP2.getP2Guess());
				 String s = "";
				 s += serverDB.getP1Fingers();
				 s += serverDB.getP1Guess();
				 s += serverDB.getP2Fingers();
				 s += serverDB.getP2Guess();
				 System.out.println(s);
				 
				// Do calculations to update p1 points and p2 points
				// set serverDb yourNumber to 1;
				 if (playersHaveAnswered()) {
					 sendPacketBoth();
					 initRound();
					 
				 }
				// If player disconnects, the cleanupcode in GameServer's run() should take care of the rest.
				// If player doesn't want to play again have them send playagain to false. Code will compare p1.mi.playagain to p2.mi.playagain
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		public void sendPacketOne(ClientRunnable player) throws IOException {
			if (player == p1) {
				serverDB.setpNum(1);
			}
			else {
				serverDB.setpNum(2);
			}
			player.out.writeObject(serverDB);
		}
		
		public void sendPacketBoth() throws IOException {
			if (playersConnected() == true) {
				serverDB.setpNum(1);
				p1.out.writeObject(serverDB);
				serverDB.setpNum(2);
				p2.out.writeObject(serverDB);
			}
			else {
				System.out.println("Error: Attempted to sendPacketBoth() but playerconnected returned false");
			}
		}
		
		public boolean playersConnected() {
			if (p1.connection.isClosed() || p2.connection.isClosed()) {
				return false;
			}
			return true;
		}
		
		public void removePlayerFromList(ClientRunnable client) {
			if (client == null) {
					System.out.println("Error: removePlayerFromList attempted to remove a null client!");
					return;
			}
			int i = cl2.indexOf(client);
			if (i != -1) {
				cl2.remove(i);
			}
		}
		
		public boolean playersHaveAnswered() {
			return (serverDB.getP1Fingers() > -1 && serverDB.getP2Fingers() > -1 &&  serverDB.getP1Guess() > -1 &&  serverDB.getP2Guess() > -1);
		}


	}
	
	
	class ClientRunnable implements Runnable{
		boolean inGame = false;
		MorraInfo mi;
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
				    	System.out.println("Server received MorraObject: " +  " from client: " + count);
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

