package client;

import java.net.*;
import java.io.*;

public class MultiClient {
    static final int MAX_THREADS = 40;


    public static void main(String[] args) throws
            IOException,InterruptedException {
        InetAddress addr = InetAddress.getByName(null);
        Thread[] threads = new Thread[MAX_THREADS];
        for (int i = 0; i < MAX_THREADS; i++) {
            threads[i] = new Thread(new ClientThread(addr));
            threads[i].start();
            Thread.sleep(100);
        }

    }
}
