import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static javafx.application.Application.launch;

//To do:
//Add error handling messages
//Reset client # after quit


public class GuiClient extends Application {
	public void updateClientID() {
		String s = "You are Client " + clientNumber;
		text_ClientID.setText(s);
	}
	static int clientNumber = -1;
	static Set<Integer> onlineClients;
	//Set<Integer> receivers2;
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
	
	Button sendBroadcastButton;
	Button multiMessageButton;
	ListView<String> clientList;
	Label format;
	String s;
	
	Button quit;
	Pane mainPane;
	Scene main;
	Scene secretScene;
	Text text_ClientID;
	int clientGuess = 0;
	
	//End scene
	Text player1EndGame;
	Text player2EndGame;
	Pane endPane;
	Stage window;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		window =primaryStage;
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

		// no more that 5 for port
		portBox.setTextFormatter(new TextFormatter<String>(change ->
				change.getControlNewText().length() <= 5 ? change : null));

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
		
		
		clientList =new ListView<String>();
		clientList.setStyle("-fx-font-family: Verdana; -fx-font-weight: bold");
		clientList.setPrefSize(230,200);
		clientList.getItems().add("Available Clients:");
        
		
		
		answerBox = new TextField();
		format = new Label("Message @1 @2....");

		quit = new Button("Quit");
		Button playAgain = new Button("Play Again");

		sendBroadcastButton = new Button("Send Broadcast");
		multiMessageButton = new Button("Send multi-message");

		mainPane = new Pane();
		mainPane.setBackground(new Background(new BackgroundImage(new Image("forest.png", 1100, 495, false,true), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,  BackgroundSize.DEFAULT)));
		
		text_ClientID = new Text("Client ID:");
		text_ClientID.setFont(Font.font ("Verdana", 20));
		text_ClientID.setStyle("-fx-font-weight: bold");
		text_ClientID.setFill(Color.INDIGO);

		Label label1= new Label("Secret scene=)");
		Button secret =new Button("Pressss me");
		secret.setOnAction(e->window.setScene(secretScene));
		
		VBox layout1 = new VBox(20);
		layout1.getChildren().addAll(label1,secret);
		main = new Scene(layout1, 200,200);
	
		
		Button back =new Button("This scene sucks, close me");
		back.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.exit(0);
			}
		});

		StackPane layout2 = new StackPane();
		layout2.getChildren().add(back);
		secretScene =new Scene(layout2, 600,300);
		window.show();


		mainPane.getChildren().addAll(answerBox,format, sendBroadcastButton, multiMessageButton, clientList, listItems2, text_ClientID,playAgain,secret, quit);
		answerBox.relocate(20,170);
		format.relocate(175, 172);

		sendBroadcastButton.relocate(20, 210);
		multiMessageButton.relocate(150, 210);
		playAgain.relocate(20, 240);
		quit.relocate(20, 270);
		secret.relocate(50, 100);
		clientList.relocate(500,10);
		listItems2.relocate(700, 10);
		text_ClientID.relocate(5, 10);



		// implement clicking functionality for buttons:
		
		playAgain.setDisable(false);
		playAgain.setOnAction(e->{
			
			answerBox.clear();
			});

		
		

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
					Platform.runLater(()->{
						DataPacket d = clientConnection.clientInfo;
						// Update Clients List
						if (d.messageType == 2) {
							clientList.getItems().clear();
							for (Integer i : d.onlineClients) {
								String s = "Client ";
								s += i + "\n";
								clientList.getItems().add(s);
							}
							
							//clientList.getItems().add(d.message);
							int lastMessage = clientList.getItems().size();
							clientList.scrollTo(lastMessage);
							
							/*
							System.out.println("You are in messageType 2");
							updateClientID();
							//String[] array = d.onlineClients.toArray(new String[0]);
							Set<String> onlineClientsNew = d.onlineClients.stream()
					                .map(String::valueOf)
					                .collect(Collectors.toSet());
							clientList.getItems().addAll(onlineClientsNew);
							//clientList.getItems().addAll(array);  //Add array of clients to client list view
							int lastClient = clientList.getItems().size();
							clientList.scrollTo(lastClient);
							*/
						}
						
						// Welcome Message
						else if (d.messageType == 3) {
							updateClientID();
							clientList.getItems().clear();
							for (Integer i : d.onlineClients) {
								String s = "Client ";
								s += i + "\n";
								clientList.getItems().add(s);
							}
							
							listItems2.getItems().add(d.message);
							int lastMessage = listItems2.getItems().size();
							listItems2.scrollTo(lastMessage);
						}
						// User recieved a message
						else { 
							listItems2.getItems().add(d.message);
							int lastMessage = listItems2.getItems().size();
							listItems2.scrollTo(lastMessage);
						}


					});
				}, Integer.parseInt(portBox.getText()), ipBox.getText());
				clientConnection.start();

				sceneMap.put("main screen", new Scene(mainPane,1100,495));
				primaryStage.setScene(sceneMap.get("main screen"));
			}
		});

		

		// submit guess button to broadcast for all clients:
		sendBroadcastButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// if guess was valid, then handle round end actions
				String message = (answerBox.getText());

				if (message.length() > 1) {

					clientConnection.sendBroadcast(message);
					// clear textbox and show numbers
					answerBox.clear();

				}
			}
		});
		
		// submit guess button to send multi-message
		// add to the set of receivers 
		multiMessageButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// if guess was valid, then handle round end actions
				String message = (answerBox.getText());

				if (message.length() > 1) {
					String[] arrOfStr = message.split("@", -10);  // get user names (1, 2...n)
					//System.out.println(arrOfStr[0]); get only the message
					//System.out.println(arrOfStr[1]);  get first client #
				   String withoutClientNumber = arrOfStr[0];
				   int index =0;

				   for (int i = index; i < arrOfStr.length-1; i++) {  //remove text and leave receivers #(1,2,3..)
					   arrOfStr[i] = arrOfStr[i + 1];
					}
				   
				   // Now add senders to Set<Integers> receivers
				   String[] onlyClients= Arrays.copyOf(arrOfStr, arrOfStr.length-1);  // Delete duplicate from the array
				   // Change Array of string to array of integers
				   int[] numbers = new int[onlyClients.length];
				   for(int i = 0;i < onlyClients.length;i++)
				   {
					  
				      numbers[i] = Integer.parseInt(onlyClients[i].trim()); //Remove the trailing space
				   }
				   //System.out.println(numbers[0]);

					clientConnection.sendMultiMessage(withoutClientNumber, numbers);  //send the message and the set of receivers to the server

					// clear textbox and show numbers
					answerBox.clear();

				}
			}
		});
		
		// show the start screen
		sceneMap.put("START", new Scene(startPane, 500, 630));
		primaryStage.setScene(sceneMap.get("START"));
		primaryStage.show();
	}

}
