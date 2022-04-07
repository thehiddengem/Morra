import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;


public class Client extends Thread{
	int clients;
	int score;
	boolean replay, p1WishToReplay, p2WishToReplay; 
	boolean endOfRound;
	Integer port;
	String ip; 
	Socket socketClient;
	
	ObjectOutputStream out;
	ObjectInputStream in;
	
	private Consumer<Serializable> callback;
	
	Client(Consumer<Serializable> call){
		clients = 0;
		score = 0;
		replay = false;
		endOfRound = false;
		callback = call;
	}
	
	Client(){
		clients = 0;
		score = 0;
		replay = false;
		endOfRound = false;

	}
	
	public void run() {
		try {
			socketClient= new Socket(ip,port);
		    out = new ObjectOutputStream(socketClient.getOutputStream());
		    in = new ObjectInputStream(socketClient.getInputStream());
		    socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {System.out.println("Error input/outputStream exception");}

		while(true) {
			try {
				MorraInfo temp = (MorraInfo)in.readObject();
				endOfRound = false;
				if(temp.hasScoreUpdate) {
					if(temp.playerNumber == 1) {
						score = temp.p1Points;					
				  }
					else if (temp.playerNumber ==2) {
						score = temp.p2Points;
						
					}
					callback.accept(temp.universalMessage );
					temp.hasScoreUpdate = false;
					endOfRound = true;
				}
				
				else if(temp.hasUniversalMessage) {
					System.out.println("message: " + temp.universalMessage);
					callback.accept(temp.universalMessage );
					temp.hasUniversalMessage = false;
				}
				
				
				if(temp.playerNumber == 1) {
					clients = 1;
				}
				else if(temp.playerNumber == 2) {
					clients = 2;
				}
				
			}
			catch(Exception e) {
				System.out.println("Except: Could not readObject from Instream!");
				break;
			}
		}
	
    }
	
	public void setPort(Integer data) {
		port = data;
	}
	
	public Integer getPort() {
		return port;
	}
	
	public String getIP() {
		return ip;
	}
	
	public void createConnection(String data) {
		ip = data;
	}
	public boolean startReplay() {
		if (p1WishToReplay && p2WishToReplay) {
			return true;
		}
		
		return false;
	}
	
	public void send(MorraInfo data) {
		try {
			if (data.p1WishToReplay )
				p1WishToReplay = true;
			if(data.p2WishToReplay)
				p2WishToReplay = true;
			if(p1WishToReplay && p2WishToReplay) {
				replay = true;
			}
			out.writeObject(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}