import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ThreadedServer implements Runnable{
	
private ArrayList<ClientRunnable> cl2;
	
	
	//no access to Thread methods
	public void threadCheck2(){
		
		for(int i = 0; i<cl2.size(); i++) {
			ClientRunnable t = cl2.get(i);
			System.out.println("Client #: " + t.count);
		}
	}
	
	public void serverCode(){
	
		int count = 1;
		
		try(ServerSocket mysocket = new ServerSocket(5555);){
        System.out.println("Server is waiting for a client!");
		
        while(true) {
        	
        	//this is with class implementing runnable
        	threadCheck2();
        	ClientRunnable cr = new ClientRunnable(mysocket.accept(), count);
        	Thread t = new Thread(cr);
        	cl2.add(cr);
        	t.start();
        	
		count++;
        }
		}
		catch(Exception e) {
			System.out.println("Server socket did not launch");
		}
		
	
	}
	

	@Override
	public void run()  {
		try {
			this.cl2 = new ArrayList<ClientRunnable>();
			this.serverCode();
			
		} catch(Exception e) {
			
		}

		
	}
	
}


class ClientRunnable implements Runnable{

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
			connection.setTcpNoDelay(true);
		}
		catch(Exception e) {
			System.out.println("Streams not open");
		}
			
	
		
		 while(true) {
			    try {
			    	String data = in.readObject().toString();
			    	System.out.println("Server received: " + data + " from client: " + count);
			    	out.writeObject(data.toUpperCase());
			    	}
			    	catch(Exception e) {
			    		System.out.println("OOOOPPs...Something wrong with the socket from client: " + count +"....closing down!");
			    		break;
			    	}
		}
		
	}
	
}

