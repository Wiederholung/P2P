package client;

import java.awt.*;

class RunClient {
    public static void main(String a[]) {
        Client f = new Client("Client");
        f.setLayout(new FlowLayout());
        f.setSize(700,425);
        f.setResizable(true);
        f.setVisible(true);
    }
}