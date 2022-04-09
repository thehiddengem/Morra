import java.io.Serializable;


public class MorraInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	int p1Hand;
	int p2Hand;
	
	int p1Guess;
	int p2Guess;
	
	String p1Plays;
	String p2Plays;
	
	String universalMessage;
	boolean roundHasEnded;
	boolean hasUniversalMessage;
	boolean hasTwoPlayers;
	boolean hasScoreUpdate;
	boolean p1WishToReplay;
	boolean p2WishToReplay;
	
	boolean playerOneMoveMade;
	boolean playerTwoMoveMade;
	
	int p1Points;
	int p2Points;

	int playerNumber;
	
	MorraInfo(){
		resetMorra();	 
	}
	public void resetMorra() {
		 p1Hand = -1;
		 p2Hand = -1;
		
	     p1Guess = -1;
		 p2Guess = -1;
		 
		 p1Points = -1;
		 p2Points = -1;
		 
		 p1Plays = "na";
		 p2Plays = "na";
		 
		 universalMessage = "";
		 
		 playerNumber = -1;
		 roundHasEnded = false;
		 hasUniversalMessage = false;
		 hasTwoPlayers = false;
		 hasScoreUpdate = false;
		 p1WishToReplay = false;
		 p2WishToReplay = false;
			
		 playerOneMoveMade = false;
		 playerTwoMoveMade = false;
	}
}