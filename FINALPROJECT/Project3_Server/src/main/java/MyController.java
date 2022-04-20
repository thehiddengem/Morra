import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MyController implements Initializable {
	
    static Thread t;
    static ThreadedServer s;
	
    @FXML
    private Label l_player_count;
    
	@FXML
	private VBox vbox_1;
	
	@FXML
	private Button button_b1;
	@FXML
	private Button button_b2;
	
	@FXML
	private BorderPane root;
    @FXML
    private TextField tf_port;
    
    @FXML
    private TextField tf_center;
    
    @FXML
    private TextField tf_right;
    
    @FXML
    private TextField putText;
    
    //static so each instance of controller can access to update 
    private static String textEntered = "";
    
	
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
        
	}
    //method so that the controller created for second view can update the text
    //field with the text from the first view
   
	
	public void button_1Method(ActionEvent e) throws IOException {
		System.out.println("start button pressed");
		button_b1.setDisable(true); 
		button_b1.setText("Starting...");
		try {
			Integer port = Integer.parseInt(tf_port.getText());
			s = new ThreadedServer(port);
		    t = new Thread(s);
			t.start();
			button_b1.setText("Server Running!");
			
		} catch (NumberFormatException error) {
			tf_port.setText("Error, enter valid port");
			button_b1.setDisable(false); 
			button_b1.setText("Start");
			error.printStackTrace();
			
		} catch (Exception error2) {
			error2.printStackTrace();
		}

        
	}

	

	public void button_2Method(ActionEvent e) throws IOException{
		
		if (s == null) {
			System.out.println("ThreadedServer is null, cannot fetch gameQueue");
		}
		else {
			s.printPlayersInfo();
			
		}
	
	}
	
	
	

}
