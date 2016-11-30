package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import javafx.application.Platform;

public class Server extends Observable {
	private List<ClientHandler> clients = new ArrayList<ClientHandler>();
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
		private BufferedReader reader;
		private ClientObserver writer;
		private String clientName;
		private Socket clientSocket;
		private List<ClientObserver> observers = new ArrayList<ClientObserver>();
		
		public ClientHandler(Socket clientSocket) throws IOException {
			this.clientSocket = clientSocket;
			clients.add(this);
			try {
				reader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
				writer = new ClientObserver(this.clientSocket.getOutputStream());
				observers.add(writer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			try {
				// Get name from client, welcome to group
				String plainName;
				while (true) {
					notifyMyObservers(observers, "Enter your name: ");
					plainName = reader.readLine().trim();
					if (plainName.indexOf('@') == -1) {
						System.out.println("breaking from @");
						break;
					} else {
						setChanged();
						notifyMyObservers(observers, "The name should not contain '@' character.");
					}
				}
				clientName = "@" + plainName;
				notifyMyObservers(observers, "Welcome to the group chat, " + plainName + "!");
				notifyMyObservers(observers, "Type the @ symbol before someone's name to chat with them, or type /quit to leave.");
				setChanged();
				notifyObservers("---A new member, " + plainName + ", has joined the group!---");
				
				// Conversation
				String message;
				while(true) {
					message = reader.readLine();
					// Private chat
					if (message.startsWith("@")) {
						String[] privateMessage = message.split("\\s", 2);
						if (privateMessage.length > 1 && privateMessage[1] != null) {
							privateMessage[1] = privateMessage[1].trim();
							if (!privateMessage[1].isEmpty()) {
								for (ClientHandler client : clients) {
									if (client != this
											&& client.clientName.equals(privateMessage[0])) {
										observers.add(client.writer);
										notifyMyObservers(observers, "<" + plainName + "> " + privateMessage[1]);
										observers.remove(client);
										break;
									}
								}
							}
						}
					}
					// Public chat
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
	
	/**
	 * This method updates all provided observers with a given message.
	 * @param observers is the list of observers to update
	 * @param message is the message to update with
	 */
	public void notifyMyObservers(List<ClientObserver> observers, String message) {
		for (ClientObserver observer : observers) {
			observer.update(this, message);
		}
	}
}