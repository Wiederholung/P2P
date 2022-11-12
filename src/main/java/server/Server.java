package server;

import java.awt.*;
import java.io.IOException;

class Server {
    public static void main(String a[])throws IOException {
        Ser f = new Ser("Server");
        f.setLayout(new FlowLayout());
        f.setSize(700,450);
        f.setResizable(true);
        f.setVisible(true);
    }
}