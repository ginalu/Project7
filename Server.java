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
		private String plainName;
		private int Age = -1;
		private Long Number;
		private Socket clientSocket;
		private List<ClientObserver> observers = new ArrayList<ClientObserver>();
		List<ClientObserver> self = new ArrayList<ClientObserver>();
		
		public ClientHandler(Socket clientSocket) throws IOException {
			this.clientSocket = clientSocket;
			clients.add(this);
			try {
				reader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
				writer = new ClientObserver(this.clientSocket.getOutputStream());
				observers.add(this.writer);
				self.add(this.writer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			try {
				// Get name from client, welcome to group
				while (true) {
					notifyMyObservers(self, "Enter your name: ");
					plainName = reader.readLine().trim();
					if (plainName.indexOf('@') == -1) {
						System.out.println("breaking from @");
						break;
					} else {
						setChanged();
						notifyMyObservers(self, "The name should not contain '@' character.");
					}
				}
				clientName = "@" + plainName;
				notifyMyObservers(observers, "Welcome to the group chat, " + plainName + "!");
				notifyMyObservers(observers, "Type the @ symbol before someone's name to private message them, or type /quit to leave.");
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
					// Set profile
					else if(message.contains("sEt#")){;
						notifyMyObservers(self, "Enter your age: ");
						message = reader.readLine();
				        while((message.matches("-?\\d+(\\.\\d+)?") == false) 
				                || Integer.parseInt(message) <= 0){
				        	notifyMyObservers(self, message + " is not valid age. Try again: " + "\n");
				            message = reader.readLine();
				        }
				        notifyMyObservers(self, "Storing " + message + "\n");
			            
				        Age = Integer.parseInt(message);
						notifyMyObservers(self, "Enter your phone number: ");
						message = reader.readLine();
				        while((message.matches("-?\\d+(\\.\\d+)?") == false) 
				                || message.length() != 10){
				        	notifyMyObservers(self, message + " is not valid number. Try again: " + "\n");
				            message = reader.readLine();
				        }
				        Number = Long.parseLong(message);
				        notifyMyObservers(self, "Storing " + message + "\n");
				        notifyMyObservers(self, "Finished Profile!" + "\n");
					}
					// View profiles
					else if(message.contains("viEw#")){
						notifyMyObservers(self, "Who would you like to view? Type @ symbol before their name. ");
						message = reader.readLine();
						boolean found = false;
						while(found == false){
							for (ClientHandler client : clients) {
								if (client.clientName.equals(message)) {
									found = true;
									if(client.Age == -1){
										notifyMyObservers(observers, "This user has not set up their profile"+ "\n");
									}
									else{
										notifyMyObservers(observers, client.clientName + "\n");
										notifyMyObservers(observers, "Age: " + client.Age + "\n");
										notifyMyObservers(observers, "Phone Number: " + client.Number + "\n");
									}
								}
	
							}
							if(found == false){
								notifyMyObservers(self, "Invalid User. Try Again: ");
								message = reader.readLine();
							}
						}
						
					}
					// Leave chat
					else if (message.contains("quIt#")) {
						setChanged();
						notifyObservers("---" + plainName + " is leaving the group!---");
						clients.remove(this);
						notifyMyObservers(self, "/quit");
						return;
					}
					// Public chat
					else {
						setChanged();
						notifyObservers(plainName + ": " + message);
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
