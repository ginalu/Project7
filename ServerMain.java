/* ServerMain.java
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
