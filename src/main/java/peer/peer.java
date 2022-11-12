package peer;

import java.io.*;
import java.net.*;

public class peer implements Runnable {
    private int PORT;
    private static volatile int[] ID_ls;
    private static volatile int count;
    private Socket client;
    private Socket server;
    private BufferedReader in;
    private PrintWriter out;


    public peer(int port) {
        PORT = port;
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            server = serverSocket.accept();
            System.out.println("Server Started");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

        public void run() {
    }


    // Command {BROADCAST_{content}} enables a client to send text to all the other clients connected to the server;
    public void broadcast(int PORT, String content) {
//        获取所有的client的socket

    }

}

