package assignment7;

import java.io.*;
import java.net.*;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets; 
import javafx.geometry.Pos; 
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label; 
import javafx.scene.control.ScrollPane; 
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField; 
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
//import javax.swing.*;

public class Client extends Application {
	private BufferedReader reader;
	private PrintWriter writer;
	private TextArea ta;
	//private String clientID;

	public static void main(String[] args) {
		try {
			launch(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override // Override the start method in the Application 
	public void start(Stage primaryStage) {
		BorderPane paneForTextField = new BorderPane();
		paneForTextField.setPadding(new Insets(9, 9, 9, 9));
		paneForTextField.setStyle("-fx-border-color: green");
		paneForTextField.setLeft(new Label("Press Enter: "));
		TextField tf = new TextField(); 
		tf.setAlignment(Pos.TOP_RIGHT); 
		paneForTextField.setCenter(tf);
		
		BorderPane mainPane = new BorderPane(); 
		// Text area to display contents 
		ta = new TextArea(); 
		ta.setPrefWidth(578);
		mainPane.setCenter(new ScrollPane(ta)); 
		mainPane.setBottom(paneForTextField);
		
	    HBox hbox = new HBox();
	    hbox.setPadding(new Insets(10, 7, 10, 7));
	    hbox.setSpacing(10);
	    hbox.setStyle("-fx-background-color: #336699;");

	    Button set = new Button("Set Profile");
	    set.setPrefSize(100, 15);

	    Button view = new Button("View Profiles");
	    view.setPrefSize(100, 15);
	    hbox.getChildren().addAll(set, view);
	    mainPane.setTop(hbox);
	    
	    
	    set.setOnAction(new EventHandler<ActionEvent>() {
	    	@Override
            public void handle(ActionEvent event) {
	    		writer.println("sEt#");
				writer.flush();
	    	}
	    });
	    
	    view.setOnAction(new EventHandler<ActionEvent>() {
	    	@Override
            public void handle(ActionEvent event) {
	    		writer.println("viEw#");
				writer.flush();
	    	}
	    });
	    
		// Create a scene and place it in the stage 
		Scene scene = new Scene(mainPane, 590, 280); 
		primaryStage.setTitle("Instant Messenger"); // Set the stage title 
		primaryStage.setScene(scene); // Place the scene in the stage 
		primaryStage.show(); // Display the stage
		try{
			setUpNetworking();
		}catch(Exception ex){
			System.err.println(ex);
		}
		tf.setOnAction(e -> {
			String message = new String();
			message = tf.getText();
			writer.println(message);
			writer.flush();
			tf.clear();
		});
	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		Socket sock = new Socket("127.0.0.1", 4242);
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		reader = new BufferedReader(streamReader);
		writer = new PrintWriter(sock.getOutputStream());
		System.out.println("networking established");
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
	}


	class IncomingReader implements Runnable {
		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					
						ta.appendText(message + "\n");
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
