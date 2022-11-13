package server;

import java.awt.*;
import java.io.IOException;

class RunServer {
    public static void main(String a[])throws IOException {
        Server f = new Server("Server");
        f.setLayout(new FlowLayout());
        f.setSize(780,450);
        f.setResizable(true);
        f.setVisible(true);
    }
}