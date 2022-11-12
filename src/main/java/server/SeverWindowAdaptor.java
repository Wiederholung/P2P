package Server;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class SeverWindowAdaptor extends WindowAdapter {
    Ser f;
    public SeverWindowAdaptor(Ser j)
    {
        f = j;
    }
    public void windowClosing(WindowEvent we) {
        f.setVisible(false);
        try{
            f.server.close();
        } catch(Exception e) {}
        f.dispose();
        System.exit(0);
    }
}
