package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Server extends Observable {
	private List<Client> clients = new ArrayList<Client>();
	
	public void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4242);
		while (true) {
			Socket clientSocket = serverSock.accept();
			ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
			Thread t = new Thread(new ClientHandler(clientSocket));
			t.start();
			this.addObserver(writer);
			System.out.println("got a connection");
		}
	}
	
	class ClientHandler implements Runnable {
		private Client client;
		private BufferedReader reader;
		private ClientObserver writer;
		
		public ClientHandler(Socket clientSocket) throws IOException {
			try {
				reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				writer = new ClientObserver(clientSocket.getOutputStream());
				client = new Client(reader, writer);
				clients.add(client);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			try {
				// Get name from client, welcome to group
				client.setName();
				setChanged();
				notifyObservers("---A new member, " + client.getName() + ", has joined the group!---");
				
				// Conversation
				String message;
				while(true) {
					message = reader.readLine();
					// Private chat
					if (message.startsWith("@")) {
						client.privateChat(message, clients);
					}
					// Set profile
					else if(message.contains("sEt#")){;
						client.setProfile(message);
					}
					// View profiles
					else if(message.contains("viEw#")){
						client.viewProfiles(message, clients);
					}
					// Leave chat
					else if (message.contains("quIt#")) {
						setChanged();
						notifyObservers("---" + client.getName() + " is leaving the group!---");
						client.quit();
					}
					// Public chat
					else {
						setChanged();
						notifyObservers(client.getName() + ": " + message);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
