import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
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
                else if(clientInfo.messageType ==2) {
                	GuiClient.onlineClients = clientInfo.onlineClients;
                	GuiClient.clientNumber = clientInfo.clientNumber;
                }
                callback.accept(clientInfo);
            }
            catch(Exception e) {}
        }

    }


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
    
    //sends the morraInfo class with the message and the set of receivers
    public void sendMultiMessage( String message, int[] numbers)  {
        try {
            Set<Integer> temp = new HashSet<>();
            clientInfo.message = message;
            for (int v : numbers) {
                temp.add(v);
            }

            System.out.println(clientInfo.receivers);
            clientInfo.messageType = 1;
            clientInfo.receivers = temp;
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
