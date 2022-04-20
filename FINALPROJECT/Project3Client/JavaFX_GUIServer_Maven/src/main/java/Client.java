import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

import javafx.application.Platform;

public class Client extends Thread{
    Socket socketClient;
    DataPacket clientInfo = new DataPacket();
    int portNum;
    String ip;
    ObjectOutputStream out;
    ObjectInputStream in;

    private Consumer<Serializable> callback;

    Client(Consumer<Serializable> call, int p, String ipAddress){
        ip = ipAddress;
        portNum = p;
        callback = call;
    }

    public void run() {

        try {
            socketClient= new Socket(ip,portNum);
            out = new ObjectOutputStream(socketClient.getOutputStream());
            in = new ObjectInputStream(socketClient.getInputStream());
            socketClient.setTcpNoDelay(true);
        }
        catch(Exception e){}

        while(true) {
            //receives the MorraInfo class from the server
            try {
                clientInfo = (DataPacket) in.readObject();
                if (clientInfo.messageType == 3) {
                	GuiClient.clientNumber = clientInfo.clientNumber;
                }
                callback.accept(clientInfo);
            }
            catch(Exception e) {}
        }

    }

    //checks to see if player 1 or player 2 who and returns a value


    //sends the morraInfo class to the Server
    public void sendBroadcast( String message) {
        try {
        	clientInfo.message = message;
        	clientInfo.messageType = 0;
            out.writeObject(clientInfo);
            out.reset();
            
            // Don't think in.reset() is needed because server will do out.reset on every message
            //in.reset();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}