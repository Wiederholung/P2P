package client;

import server.MultiServer;

import java.net.*;
import java.io.*;

class ClientThread implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private static int counter = 0;
    private int id = counter++;
    private static int thread_count = 0;
    public static int thread_count() { return thread_count; }


    public ClientThread(InetAddress addr) {
        System.out.println("Making client " + id);
        thread_count++;
        try {
            socket = new Socket(addr, MultiServer.PORT);
        } catch (IOException e) {
            System.err.println("Socket failed");
        }

        try {
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(
                            socket.getOutputStream())), true);
//            start();
        }
        catch (IOException e) {
            try { socket.close(); }
            catch (IOException e2) {
                System.err.println("Socket not closed");
            }
        }

    }


    public void run() {
        try {
            for (int i = 0; i < 5; i++) {
                out.println("Client " + id + ": " + i);
                String str = in.readLine();
                System.out.println(str);
            }
            out.println("END");
        }
        catch (IOException e) { System.err.println("IO Exception"); }
        finally {
            try { socket.close(); }
            catch (IOException e) {
                System.err.println("Socket not closed");
            }
            thread_count--;
        }
    }

}
