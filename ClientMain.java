package assignment7;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

//import javax.swing.*;

public class ClientMain { 
	private BufferedReader reader; 
	private PrintWriter writer; 
	private static ArrayList<String> names;		//keep a list on existing participants?
	//private JTextArea incoming; 
	//private JTextField outgoing;
	
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
	
	public static void run(){
		
	}
	
	public static void main(String[] args){
		try{
			run();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	class IncomingReader implements Runnable {
		public void run() { 
			String message;
			try{
				while ((message = reader.readLine()) != null) {
					incoming.append(message + "\n");
				}
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
}