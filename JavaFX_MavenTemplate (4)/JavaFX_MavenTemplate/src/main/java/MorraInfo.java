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

    // Number of players
	int playerNumber;
}