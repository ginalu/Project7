package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import javafx.application.Platform;

public class Server extends Observable {
	public static int numClients = 0;
	public static List<String> clients = new ArrayList<String>();
	
	public void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4242);
		while (true) {
			Socket clientSocket = serverSock.accept();
			numClients++;
			// Display the client number 
			System.out.println("Starting thread for client " + numClients +
					" at " + new Date());
			ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
			Thread t = new Thread(new ClientHandler(clientSocket));
			t.start();
			this.addObserver(writer);
			System.out.println("got a connection");
			System.out.print("current clients: ");
			for (String client : clients) {
				System.out.print(client + " ");
			}
			System.out.println();
		}
	}
	
	class ClientHandler implements Runnable {
		private BufferedReader reader;

		public ClientHandler(Socket clientSocket) {
			Socket sock = clientSocket;
			try {
				reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					if (message.contains("client ID: ")) {
						String[] messageArray = message.split(" ");
						clients.add(messageArray[2]);
					}
					else {
						System.out.println("server read "+message);
						setChanged();
						notifyObservers(message);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
