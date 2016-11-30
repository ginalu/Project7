/* Client.java
 * EE422C Project 7 submission by
 * Gina Lu
 * gbl286
 * 16480
 * Jessica Slaughter
 * jts3329
 * 16470
 * Slip days used: <1>
 * Fall 2016
 */

package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Observable;

/**
 * This class represents a client of the server.
 * @author jessicaslaughter
 *
 */
public class Client extends Observable {
	private BufferedReader reader;
	private ClientObserver writer;
	private String clientName;
	private String plainName;
	private int age = -1;
	private Long number;
	
	public Client(BufferedReader reader, ClientObserver writer) {
		this.reader = reader;
		this.writer = writer;
		this.addObserver(writer);
	}
	
	/**
	 * Set the name of the client
	 */
	public void setName() {
		try {
			while (true) {
				setChanged();
				notifyObservers("Enter your name: ");
				plainName = reader.readLine().trim();
				if (plainName.indexOf('@') == -1) {
					System.out.println("breaking from @");
					break;
				} else {
					setChanged();
					notifyObservers("The name should not contain '@' character.");
				}
			}
			clientName = "@" + plainName;
			setChanged();
			notifyObservers("Welcome to the group chat, " + plainName + "!");
			setChanged();
			notifyObservers("Type the @ symbol before someone's name to private message them.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the name of the client
	 * @return
	 */
	public String getName() {
		return plainName;
	}
	
	/**
	 * Send a private message
	 * @param message is the message to send
	 * @param clients are the clients to search through for the intended recipient
	 */
	public void privateChat(String message, List<Client> clients) {
		String[] privateMessage = message.split("\\s", 2);
		if (privateMessage.length > 1 && privateMessage[1] != null) {
			privateMessage[1] = privateMessage[1].trim();
			if (!privateMessage[1].isEmpty()) {
				for (Client client : clients) {
					if (client != this
							&& client.clientName.equals(privateMessage[0])) {
						this.addObserver(client.writer);
						setChanged();
						notifyObservers("<" + plainName + "> " + privateMessage[1]);
						this.deleteObserver(client.writer);
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Set the client's profile
	 * @param message
	 */
	public void setProfile(String message) {
		try {
			setChanged();
			notifyObservers("Enter your age: ");
			message = reader.readLine();
			while((message.matches("-?\\d+(\\.\\d+)?") == false) 
					|| Integer.parseInt(message) <= 0){
				setChanged();
				notifyObservers(message + " is not valid age. Try again: " + "\n");
				message = reader.readLine();
			}
			setChanged();
			notifyObservers("Storing " + message + "\n");

			age = Integer.parseInt(message);
			setChanged();
			notifyObservers("Enter your phone number: ");
			message = reader.readLine();
			while((message.matches("-?\\d+(\\.\\d+)?") == false) 
					|| message.length() != 10){
				setChanged();
				notifyObservers(message + " is not valid number. Try again: " + "\n");
				message = reader.readLine();
			}
			number = Long.parseLong(message);
			setChanged();
			notifyObservers("Storing " + message + "\n");
			setChanged();
			notifyObservers("Finished Profile!" + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * View a client/s profile
	 * @param message is the message with the client's name
	 * @param clients are the clients to search through
	 */
	public void viewProfiles(String message, List<Client> clients) {
		try {
			setChanged();
			notifyObservers("Who would you like to view? Type @ symbol before their name. ");
			message = reader.readLine();
			boolean found = false;
			while(found == false){
				for (Client client : clients) {
					if (client.clientName.equals(message)) {
						found = true;
						if(client.age == -1){
							setChanged();
							notifyObservers("This user has not set up their profile"+ "\n");
						}
						else{
							setChanged();
							notifyObservers(client.clientName + "\n");
							setChanged();
							notifyObservers("Age: " + client.age + "\n");
							setChanged();
							notifyObservers("Phone Number: " + client.number + "\n");
						}
					}

				}
				if(found == false){
					setChanged();
					notifyObservers("Invalid User. Try Again: ");
					message = reader.readLine();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Leave the group chat
	 */
	public void quit() {
		setChanged();
		notifyObservers("/quit");
	}
}
