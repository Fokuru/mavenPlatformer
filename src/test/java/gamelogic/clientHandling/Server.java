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
 private int CURRENT_CONNECTIONS = 0;
    public final int LISTENING_PORT = 9877;
    private  ArrayList<Information> playerData = new ArrayList<Information>();
    private  List<ConnectionHandler> connections = Collections.synchronizedList(new ArrayList<>());

    public int getConnections() {
        return CURRENT_CONNECTIONS;
    }
    
    public Server(){
          ServerSocket listener;  // Listens for incoming connections.
        Socket sock;      // For communication with the connecting program.

        // Accept and process connections forever, or until some error occurs. 

        try {
            listener = new ServerSocket(LISTENING_PORT);
            System.out.println("Listening on port " + LISTENING_PORT);
            while (true) {
                sock = listener.accept();
                // Accept next connection request and handle it.
                ConnectionHandler h = new ConnectionHandler(sock, CURRENT_CONNECTIONS);
                CURRENT_CONNECTIONS++;
                h.start();
                 
            }
        }
        catch (Exception e) {
            System.out.println("Sorry, the server has shut down.");
            System.out.println("Error:  " + e);
            return;
        }
    }
      public static void main(String[] args) {
        new Server();
      
    }  // end main()
    


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
            connections.add(this);
            String clientAddress = "User " + number;
            System.out.println("Handling connection with " + clientAddress);
            try {
                oos = new ObjectOutputStream(client.getOutputStream());
                ois = new ObjectInputStream(client.getInputStream());

                System.out.println("Streams established with " + clientAddress);
                
                while (true) {
                    if (ois == null) {
                        System.out.println("Input stream is null for " + clientAddress);
                        break;
                    }
                    
                    Information message = null;
                    try {
                        message = (Information) ois.readObject();
                    } catch (EOFException f){
                        System.out.println ("Ran into an EOF: " + f);
                    }

                    playerData.set(number, message);

                    System.out.println("Message Received from " + clientAddress);
                    
                    // Broadcast the message to all other clients
                    synchronized (connections) {
                        for (ConnectionHandler handler : connections) {
                            if (handler != this) {
                                try {
                                    handler.oos.writeObject(playerData);
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
