package Project7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ChatServer {
	
	private ArrayList<PrintWriter> clientOutputStreams; 
	
	public static void main(String[] args){
		try{
			new ChatServer().setUpNetworking();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void setUpNetworking() throws Exception {
		clientOutputStreams = new ArrayList<PrintWriter>();
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4242); 
		while (true) { 
			Socket clientSocket = serverSock.accept();
			PrintWriter writer = new PrintWriter(clientSocket.getOutputStream()); 
			clientOutputStreams.add(writer); 
			Thread t = new Thread(new ClientHandler(clientSocket)); 
			t.start(); 
			System.out.println("got a connection");
		}
	}
	
	class ClientHandler implements Runnable { 
		private BufferedReader reader;
		public ClientHandler(Socket clientSocket) throws IOException {
			Socket sock = clientSocket; 
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		} 
		public void run() { 
			String message;
			while ((message = reader.readLine()) != null) {
				notifyClients(message);
			}
		}
	}
	private void notifyClients(String message) {
		for (PrintWriter writer : clientOutputStreams) {
			writer.println(message); 
			writer.flush();
		}
	}
}
