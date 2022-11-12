package client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class ClientWindowAdaptor extends WindowAdapter {
    Client f;
    public ClientWindowAdaptor(Client j)
    {
        f = j;
    }
    public void windowClosing(WindowEvent we) {
        f.setVisible(false);
        try{
            f.socket.close();
            f.dispose();
        } catch(Exception e) {}
        System.exit(0);
    }
}
