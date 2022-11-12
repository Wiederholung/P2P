package server;

import java.io.*;
import java.net.*;

public class MultiServer {
    public static final int PORT = 8989;


    public static void main(String[] args) throws IOException {
        try (ServerSocket s = new ServerSocket(PORT)) {
            System.out.println("Server Started");

            while (true) {
                Socket socket = s.accept();
                try {
                    new Thread(new ServeOne(socket)).start();
                } catch (IOException e) {
                    socket.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
