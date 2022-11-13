package server;

import client.ClientMessage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class NewThread implements Runnable {
    Thread thread;
    NewSocket client;
    ClientMessage msg;
    Data flag;
    Socket count[];
    ArrayList<NewSocket> clients;
    Server f;
    ServerSocket serverSocket;

    NewThread(NewSocket client, ClientMessage msg, Data flag, ArrayList<NewSocket> clients, Server f, ServerSocket server) {
        thread = new Thread(this,"Client");
        this.serverSocket = server;
        this.client = client;
        this.msg = msg;
        this.f = f;
        this.flag = flag;
        this.clients = clients;
        thread.start();
    }

    public void run() {
        String name = msg.senderID;
        try {
            while(!serverSocket.isClosed()) {
                ObjectInputStream obj = new ObjectInputStream(client.socket.getInputStream());
                msg = (ClientMessage)obj.readObject();

                String senderID = msg.senderID;
                String msgText = msg.msgText;
                String receiverID = msg.receiverID;


                if(msg.senderID != null && msg.msgText != null) {
                    f.ta.append(msg.senderID+ " to " + msg.receiverID + " >> "+msg.msgText+"\n"); //显示从谁发给谁
                }

//                for(int i = 0; i < flag.count; i++) {
//                    try{
//                        ClientMessage m = (ClientMessage) new ObjectInputStream(count[i].getInputStream()).readObject();
//                        if (m.senderID.equals(msg.receiverID)) {
//                            ObjectOutputStream objw = new ObjectOutputStream(count[i].getOutputStream());
//                            objw.writeObject(msg);
//                        }
//                    }catch(Exception e) {}
//                }
                for(int i = 0; i < flag.count; i++) {
                    if (receiverID.equals("All")){
                        try{
                            ObjectOutputStream objw = new ObjectOutputStream(clients.get(i).socket.getOutputStream());
                            objw.writeObject(msg);
                        }catch(Exception e) {}
                    } else if (senderID.equals(clients.get(i).ID) || receiverID.equals(clients.get(i).ID)){
                        try{
                            ObjectOutputStream objw = new ObjectOutputStream(clients.get(i).socket.getOutputStream());
                            objw.writeObject(msg);
                        }catch(Exception e) {}
                    }
                }
            }

            if(serverSocket.isClosed()) {
                for(int i = 0; i < flag.count; i++) {
                    try{
                        clients.get(i).socket.close();
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
                client.socket.close();
            } catch(Exception ex) {}
        }
    }
}
