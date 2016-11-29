package assignment7;

import java.util.Observable;

public class ServerMain extends Observable {
	public static void main(String[] args) {
		try {
			new Server().setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
