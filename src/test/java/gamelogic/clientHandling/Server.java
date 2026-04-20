package gamelogic.clientHandling;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
import gameengine.*;
import gamelogic.Main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import gameengine.GameBase;
import gameengine.graphics.MyWindow;
import gameengine.input.KeyboardInputManager;
import gameengine.loaders.LeveldataLoader;
import gamelogic.clientHandling.Information;
import gamelogic.clientHandling.Server;
import gamelogic.level.Level;
import gamelogic.level.LevelData;
import gamelogic.level.PlayerDieListener;
import gamelogic.level.PlayerWinListener;

public class Server {
    private static int CURRENT_CONNECTIONS = 0;
    public static final int LISTENING_PORT = 9877;
    private List<ConnectionHandler> connections = Collections.synchronizedList(new ArrayList<>());

    public int getConnections() {
        return CURRENT_CONNECTIONS;
    }
    

    public Server() {
        Main main = new Main();
		//Server start = new Server();

		ServerSocket listener;  // Listens for incoming connections.
        Socket connection;      // For communication with the connecting program.



		System.out.println("Running");
		new Thread(() -> {
			while (CURRENT_CONNECTIONS>0) {
				System.out.println("Starting game...");
				main.start("Platformer", Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
			}
		}).start();


		try {
            listener = new ServerSocket(LISTENING_PORT);
            System.out.println("Listening on port " + LISTENING_PORT);
            while (true) {
                  // Accept next connection request and handle it.
                connection = listener.accept();
                System.out.println("Connection received from " + connection.getInetAddress());
                ConnectionHandler handler = new ConnectionHandler(connection, CURRENT_CONNECTIONS);
                try {
					connections.add(handler);
					System.out.println("A new player exists! Player " + (CURRENT_CONNECTIONS));
					
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Error adding new player: " + e);
				}
                CURRENT_CONNECTIONS++;
                handler.start();

            }
        }
        catch (Exception e) {
            System.out.println("Sorry, the server has shut down.");
            System.out.println("Error:  " + e);
            return;
        }

    }


    /**
     *  Defines a thread that handles the connection with one
     *  client.
     */
    // pre: none
    // post: the thread is running, and is handling the connection with one client.
    public class ConnectionHandler extends Thread {
        Socket client;
        ObjectOutputStream oos;
        ObjectInputStream ois;
        int number;

        ConnectionHandler(Socket socket, int newNum) {
            client = socket;
            number = newNum;
        }
        
        public void run() {
            String clientAddress = "User " + number;
            try {
                oos = new ObjectOutputStream(client.getOutputStream());
                ois = new ObjectInputStream(client.getInputStream());
                
                while (true) {
                    Level message = (Level) ois.readObject();
                    System.out.println("Message Received from " + clientAddress);
                    
                    // Broadcast the message to all other clients
                    synchronized (connections) {
                        for (ConnectionHandler handler : connections) {
                            if (handler != this) {
                                try {
                                    handler.oos.writeObject(message);
                                    System.out.println("Hehe");
                                    handler.oos.flush();
                                    
                                } catch (IOException e) {
                                    System.out.println("Error sending to client: " + e);
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                System.out.println("Error on connection with: " + clientAddress + ": " + e);
            } finally {
                // Remove this handler from the list when connection closes
                synchronized (connections) {
                    connections.remove(this);
                }
                try {
                    client.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }


}
