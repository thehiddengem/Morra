import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.HashMap;

import static javafx.application.Application.launch;

//To do:
//Add error handling messages
//Reset client # after quit


public class GuiClient extends Application {
	Client clientConnection;
	HashMap<String, Scene> sceneMap = new HashMap<>();
	ListView<String> listItems2;
	
	//Start scene
	TextField portBox;
	Text portText;
	TextField ipBox;
	Text ipText;
	Button startButton;
	Scene startScene;

	// main screen variables
	int player1Score = 0;
	int player2Score = 0;
	TextField answerBox;
	Button submitButton;
	Button quit;
	Pane mainPane;
	Scene main;
	Text player1ScoreText;
	Text player2ScoreText;
	Text opponentPlay;
	int clientGuess = -1;
	
	//End scene
	Text player1EndGame;
	Text player2EndGame;
	Pane endPane;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Connect to Morra Server");

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});

		// set up start screen:
		portBox = new TextField();

		// prevent user from being able to enter more than 4 characters for port
		portBox.setTextFormatter(new TextFormatter<String>(change ->
				change.getControlNewText().length() <= 4 ? change : null));

		// textbox for entering port
		portText = new Text("Enter a port:");
		portText.setFont(Font.font ("Verdana", 22));
		portText.setStyle("-fx-font-weight: bold");
		portText.setFill(Color.LIGHTGREEN);

		// textbox for entering ip address
		ipBox = new TextField();
		ipText = new Text("Enter an IP:");
		ipText.setFont(Font.font ("Verdana", 22));
		ipText.setStyle("-fx-font-weight: bold");
		ipText.setFill(Color.LIGHTGREEN);

		startButton = new Button("Let's play!");

		Pane startPane = new Pane();
		startPane.setBackground(new Background(new BackgroundImage(new Image("connect.png", 532, 720, false,true), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,  BackgroundSize.DEFAULT)));

		startPane.getChildren().addAll(ipText, ipBox, portText, portBox, startButton);
		portText.relocate(185, 200);
		portBox.relocate(165,230);
		ipText.relocate(190,300);
		ipBox.relocate(165, 330);
		startButton.relocate(210, 400);

		// set up waiting screen:
		player1EndGame = new Text("Player 1 ");
		player1EndGame.setFont(Font.font ("Verdana", 40));
		player1EndGame.setStyle("-fx-font-weight: bold");
		player1EndGame.setFill(Color.INDIGO);

		player2EndGame = new Text ("Player 2 ");
		player1EndGame.setFont(Font.font ("Verdana", 40));
		player1EndGame.setStyle("-fx-font-weight: bold");
		player1EndGame.setFill(Color.INDIGO);

		endPane = new Pane(player1EndGame, player2EndGame);
		endPane.setBackground(new Background(new BackgroundImage(new Image("endGame.png", 1100, 495, false,true), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,  BackgroundSize.DEFAULT)));

		player1EndGame.relocate(220, 250);
		player2EndGame.relocate(220, 200);

		// set up main screen:
		listItems2 = new ListView<String>();
		listItems2.setStyle("-fx-font-family: Verdana; -fx-font-weight: bold");
		listItems2.setPrefSize(390,475);

		answerBox = new TextField();

		quit = new Button("Quit");

		submitButton = new Button("Submit guess");
		//submitButton.setOnAction(e->{clientConnection.send(answerBox.getText()); answerBox.clear();});

		mainPane = new Pane();
		mainPane.setBackground(new Background(new BackgroundImage(new Image("forest.png", 1100, 495, false,true), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,  BackgroundSize.DEFAULT)));

		// buttons for guessing
		ImageView quess0 = new ImageView(new Image("quess0.png", 100, 100, false, true));
		ImageView quess1 = new ImageView(new Image("quess1.png", 100, 100, false, true));
		ImageView quess2 = new ImageView(new Image("quess2.png", 100, 100, false, true));
		ImageView quess3 = new ImageView(new Image("quess3.png",100, 100, false, true));
		ImageView quess4 = new ImageView(new Image("quess4.png", 100, 100, false, true));
		ImageView quess5 = new ImageView(new Image("quess5.jpg", 100, 100, false, true));

		HBox guessImages = new HBox(18, quess0, quess1, quess2, quess3, quess4, quess5);

		player1ScoreText = new Text("Player 1 score: " + player1Score);
		player1ScoreText.setFont(Font.font ("Verdana", 20));
		player1ScoreText.setStyle("-fx-font-weight: bold");
		player1ScoreText.setFill(Color.INDIGO);

		player2ScoreText = new Text("Player 2 score: " + player2Score);
		player2ScoreText.setFont(Font.font ("Verdana", 20));
		player2ScoreText.setStyle("-fx-font-weight: bold");
		player2ScoreText.setFill(Color.INDIGO);

		opponentPlay = new Text("Opponent played:");
		opponentPlay.setFont(Font.font ("Verdana", 20));
		opponentPlay.setStyle("-fx-font-weight: bold");
		opponentPlay.setFill(Color.INDIGO);


		mainPane.getChildren().addAll(answerBox, submitButton, listItems2, guessImages, player1ScoreText, player2ScoreText, opponentPlay, quit);
		answerBox.relocate(20,170);
		submitButton.relocate(20, 210);
		quit.relocate(20, 250);
		listItems2.relocate(700, 10);
		guessImages.relocate(0, 380);
		opponentPlay.relocate(5, 10);
		player1ScoreText.relocate(275,10);
		player2ScoreText.relocate(275, 50);


		// implement clicking functionality for buttons:

		// quit button
		quit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.exit(0);
			}
		});

		// "Let's play!" button
		startButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// initialize client object once port and ip are entered
				clientConnection = new Client(data->{
					Platform.runLater(()->{listItems2.getItems().add(data.toString());
						int lastMessage = listItems2.getItems().size();
						listItems2.scrollTo(lastMessage);
					});
				}, Integer.parseInt(portBox.getText()), ipBox.getText());
				clientConnection.start();

				sceneMap.put("main screen", new Scene(mainPane,1100,495));
				primaryStage.setScene(sceneMap.get("main screen"));
			}
		});

		// "finger" choices
		quess0.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				quess4.setDisable(true);
				quess1.setDisable(true);
				quess2.setDisable(true);
				quess3.setDisable(true);
				quess5.setDisable(true);
				
				quess4.setVisible(false);
				quess1.setVisible(false);
				quess2.setVisible(false);
				quess3.setVisible(false);				
				quess5.setVisible(false);

				clientGuess = 0;
			}
		});

		quess1.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				quess0.setDisable(true);
				quess4.setDisable(true);
				quess2.setDisable(true);
				quess3.setDisable(true);
				quess5.setDisable(true);
				
				quess0.setVisible(false);
				quess4.setVisible(false);
				quess2.setVisible(false);
				quess3.setVisible(false);				
				quess5.setVisible(false);

				clientGuess = 1;

			}
		});

		quess2.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				quess0.setDisable(true);
				quess1.setDisable(true);
				quess4.setDisable(true);
				quess3.setDisable(true);
				quess5.setDisable(true);
				
				quess0.setVisible(false);
				quess1.setVisible(false);
				quess4.setVisible(false);
				quess3.setVisible(false);				
				quess5.setVisible(false);


				clientGuess = 2;

			}
		});

		quess3.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				
				quess0.setDisable(true);
				quess1.setDisable(true);
				quess2.setDisable(true);
				quess4.setDisable(true);
				quess5.setDisable(true);
				
				quess0.setVisible(false);
				quess1.setVisible(false);
				quess2.setVisible(false);
				quess4.setVisible(false);				
				quess5.setVisible(false);


				clientGuess = 3;

			}
		});

		quess4.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				quess0.setDisable(true);
				quess1.setDisable(true);
				quess2.setDisable(true);
				quess3.setDisable(true);
				quess5.setDisable(true);
				
				quess0.setVisible(false);
				quess1.setVisible(false);
				quess2.setVisible(false);
				quess3.setVisible(false);				
				quess5.setVisible(false);

				clientGuess = 4;
			}
		});

		quess5.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				quess0.setDisable(true);
				quess1.setDisable(true);
				quess2.setDisable(true);
				quess3.setDisable(true);
				quess4.setDisable(true);
				
				quess0.setVisible(false);
				quess1.setVisible(false);
				quess2.setVisible(false);
				quess3.setVisible(false);
				quess4.setVisible(false);

				clientGuess = 5;

			}
		});

		// submit guess button:
		submitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// if guess was valid, then handle round end actions
				int guess = Integer.parseInt(answerBox.getText());

				if (guess >= 0 && guess <= 10) {
					
					//clientConnection.send(clientGuess);

					// display opponent's pick
					if (clientConnection.clientInfo.getpNum() == 1) {
						System.out.println(clientConnection.clientInfo.getP2Plays());
						//ImageView opponentPick = new ImageView(new Image("quess" + clientConnection.clientInfo.getP2Plays() , 100, 100, false, true));
					    //mainPane.getChildren().add(opponentPick);
					   // opponentPick.relocate(50, 40);
					}
					else {
						System.out.println(clientConnection.clientInfo.getP1Plays());
						//ImageView opponentPick = new ImageView(new Image("quess" + clientConnection.clientInfo.getP1Plays(), 100, 100, false, true));
					    //mainPane.getChildren().add(opponentPick);
					    //opponentPick.relocate(50, 40);
					}

					clientConnection.send(clientGuess, guess);

					// clear textbox and show numbers
					answerBox.clear();

					quess0.setDisable(false);
					quess1.setDisable(false);
					quess2.setDisable(false);
					quess3.setDisable(false);
					quess4.setDisable(false);
					quess5.setDisable(false);
					
					quess0.setVisible(true);
					quess1.setVisible(true);
					quess2.setVisible(true);
					quess3.setVisible(true);
					quess4.setVisible(true);
					quess5.setVisible(true);
				}
			}
		});

		// show the start screen
		sceneMap.put("START", new Scene(startPane, 500, 63));
		primaryStage.setScene(sceneMap.get("START"));
		primaryStage.show();
	}

}