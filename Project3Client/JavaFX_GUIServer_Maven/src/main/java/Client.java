import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread{
    Socket socketClient;
    MorraInfo clientInfo = new MorraInfo();
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
                clientInfo = (MorraInfo) in.readObject();
                callback.accept(clientInfo.getPlayerString());
            }
            catch(Exception e) {}
        }

    }

    //checks to see if player 1 or player 2 who and returns a value
    public int whoWon(){
        if(clientInfo.getP1Points() == 2){
            callback.accept("Player 1 won!");
            return 1;
        }
        else if(clientInfo.getP2Points() == 2){
            callback.accept("Player 2 won!");
            return 2;
        }
        return 0;
    }

    //sends the morraInfo class to the Server
    public void send(String data) {
        try {
            if(clientInfo.getpNum() == 1){
                clientInfo.setP1Plays(data);
            }
            else if(clientInfo.getpNum() == 2){
                clientInfo.setP2Plays(data);
            }
            out.writeObject(clientInfo);
            out.reset();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}