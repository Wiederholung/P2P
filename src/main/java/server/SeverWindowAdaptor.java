package server;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class SeverWindowAdaptor extends WindowAdapter {
    Server f;
    public SeverWindowAdaptor(Server j) {
        f = j;
    }

    public void windowClosing(WindowEvent we) {
        f.setVisible(false);
        try{
            f.serverSocket.close();
        } catch(Exception e) {}
        f.dispose();
        System.exit(0);
    }
}
