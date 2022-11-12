package server;

import java.io.*;
import java.net.*;

public class ServeOne implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;


    public ServeOne(Socket s) throws IOException {
        socket = s;
        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(
                        socket.getOutputStream())), true);
//        start();
    }


    public void run() {
        try {
            while (true) {
                String str = in.readLine();
                if (str.equals("END")) break;
                System.out.println("Echoing: " + str);
                out.println(str);
            }
            System.out.println("closing...");
        }
        catch (IOException e) { System.err.println("IO Exception"); }
        finally {
            try { socket.close(); }
            catch (IOException e) {
                System.err.println("Socket not closed");
            }
        }
    }

}
