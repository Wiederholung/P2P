package server;

import client.ClientMessage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

class NewThread implements Runnable {
    Thread t;
    Socket client;
    ClientMessage msg;
    Data flag;
    Socket count[];
    Ser f;
    ServerSocket server;

    NewThread(Socket client, ClientMessage msg, Data flag, Socket count[], Ser f, ServerSocket server) {
        t = new Thread(this,"Client");
        this.server = server;
        this.client = client;
        this.msg = msg;
        this.f = f;
        this.flag = flag;
        this.count = count;
        t.start();
    }

    public void run() {
        String name = msg.senderID;
        try {
            while(!server.isClosed()) {
                ObjectInputStream obj = new ObjectInputStream(client.getInputStream());
                msg = (ClientMessage)obj.readObject();

                if(msg.senderID != null && msg.msgText != null) {
                    f.ta.append(msg.senderID+ " to " + msg.receiverID + " >> "+msg.msgText+"\n"); //显示从谁发给谁
                }
                name = msg.senderID;

                for(int i = 0; i < flag.count; i++) {
                    try{
                        ClientMessage m = (ClientMessage) new ObjectInputStream(count[i].getInputStream()).readObject();
                        if (m.senderID.equals(msg.receiverID)) {
                            ObjectOutputStream objw = new ObjectOutputStream(count[i].getOutputStream());
                            objw.writeObject(msg);
                        }
                    }catch(Exception e) {}
                }
            }

            if(server.isClosed()) {
                for(int i = 0; i < flag.count; i++) {
                    try{
                        count[i].close();
                    } catch(Exception e) {}
                }
            }
        } catch(Exception e) {
            f.ta.append(name + " is offline\n");
            try {
                msg.msgText = " is offline\n";
                for(int i = 0; i < flag.count; i++) {
                    try{
                        ObjectOutputStream objw = new ObjectOutputStream(count[i].getOutputStream());
                        objw.writeObject(msg);
                    }catch(Exception ex) {}
                }
                client.close();
            } catch(Exception ex) {}
        }
    }
}
