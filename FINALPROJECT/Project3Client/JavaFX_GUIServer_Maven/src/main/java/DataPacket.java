import java.io.IOException;
import java.io.Serializable;
import java.util.Set;



public class DataPacket implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	// server will tell client, what their number is
	int clientNumber;
	Set<Integer> receivers;  //update receivers
	Set<Integer> onlineClients; // every online client updates when I get the packet  
	String message;
	// 0 = broadcast, 1 = multi message, 2 = update available clients, 3 = welcome packet
	int messageType;
	

    private int p1Points = 0; //points for player 1
    private int p2Points = 0; //points for player 2
    
    // Wont be used. Kept it here in-case your code depends on it so it doesn't break.
    private String p1Plays; //player 1 fingers and guess
    private String p2Plays; //player 2 plays fingers and guess
    
    private int p1Guess = -1;
    private int p2Guess = -1;
    
    private int p1Fingers = -1;
    private int p2Fingers = -1;
    
    private int yourNumber; //tells which player they are
    
    private int winner;
    public boolean playAgain;
    public boolean gameOver;
    
    private String playerString;
    private boolean twoPlayers;  //returns true if has 2 clients connected
    private boolean guessing;

    public boolean isGuessing() {
        return guessing;
    }

    public void setGuessing(boolean guessing) {
        this.guessing = guessing;
    }

    public boolean hasTwoClients() {
        return twoPlayers;
    }

    public void setTwoPlayers(boolean have2Players) {
        this.twoPlayers = have2Players;
    }

    public String getPlayerString() {
        return playerString;
    }

    public void setPlayerString(String playerString) {
        this.playerString = playerString;
    }

    public int getpNum() {
        return yourNumber;
    }

    public void setpNum(int pNum) {
        this.yourNumber = pNum;
    }

    public String getP1Plays() {
        return p1Plays;
    }

    public void setP1Plays(String p1Plays) {
        this.p1Plays = p1Plays;
    }

    public String getP2Plays() {
        return p2Plays;
    }

    public void setP2Plays(String p2Plays) {
        this.p2Plays = p2Plays;
    }
    
    public int getP1Points() {
        return p1Points;
    }

    public void setP1Points(int p1Points) {
        this.p1Points = p1Points;
    }

    public int getP2Points() {
        return p2Points;
    }

    public void setP2Points(int p2Points) {
        this.p2Points = p2Points;
    }
    
    public int won(int p1, int p2, int total) {
        if(p1 == total && p2 != total){
            return 1;
        }
        else if(p2 == total && p1 != total){
            return 2;
        }
        return 0;
    }
    
    //  My Added Functions --------->
    
    public int getP1Guess() {
        return p1Guess;
    }

    public void setP1Guess(int guess) {
        this.p1Guess = guess;
    }

    
    public int getP1Fingers() {
        return p1Fingers;
    }

    public void setP1Fingers(int fingers) {
        this.p1Fingers = fingers;
    }
    
    //
    public int getP2Guess() {
        return p2Guess;
    }

    public void setP2Guess(int guess) {
        this.p2Guess = guess;
    }

    
    public int getP2Fingers() {
        return p2Fingers;
    }

    public void setP2Fingers(int fingers) {
        this.p2Fingers = fingers;
    }
    
    public void setWinner(int winner) {
    	this.winner = winner;
    }
    
    public int getWinner() {
    	return this.winner;
    }


}
