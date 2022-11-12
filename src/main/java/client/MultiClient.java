package client;

import java.net.*;
import java.io.*;

public class MultiClient {
    static final int MAX_THREADS = 40;


    public static void main(String[] args) throws
            IOException,InterruptedException {
        InetAddress addr = InetAddress.getByName(null);
        while (true) {
            if (ClientThread.thread_count() < MAX_THREADS)
                new Thread(new ClientThread(addr)).start();
            Thread.sleep(100);
        }
    }
}
