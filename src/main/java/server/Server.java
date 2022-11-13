package server;

import client.ClientMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class Server extends JFrame implements ActionListener,Runnable{
    Thread thread;
    JButton startSever, stopSever;
    JTextField tfPort;
    JLabel hint;
    TextArea ta;

    ServerSocket serverSocket;
    ClientMessage msg = new ClientMessage();
    Data flag = new Data();

    Socket count[] = new Socket[5];
    int cj = 5;
    int cl = 0;

    ArrayList<NewSocket> clients;

    int MaxClientAllowed = 100;

    Server(String windowName) {
        super(windowName);

        flag.signal = 0;
        flag.count = 0;
        clients = new ArrayList<NewSocket>(MaxClientAllowed);

        JLabel l3 = new JLabel("Enter Port No. : ");
        add(l3);

        tfPort = new JTextField(7);
        tfPort.setText("5001");
        add(tfPort);

        JLabel l1 = new JLabel("Start the Server");
        add(l1);

        startSever = new JButton("Start");
        startSever.addActionListener(this);
        add(startSever);

        JLabel l2 = new JLabel("Stop the Server");
        add(l2);

        stopSever = new JButton("Stop");
        stopSever.addActionListener(this);
        add(stopSever);
        stopSever.setEnabled(false); //默认不能点击

        JLabel l4 = new JLabel("Status : ");
        add(l4);

//        add(new JLabel("    "));

        hint = new JLabel("Server is not running...");
        add(hint);

        ta = new TextArea("",18,90);
        ta.setEditable(false);
        ta.setBackground(Color.WHITE);
        ta.setFont(Font.getFont("verdana"));
        add(ta);

        SeverWindowAdaptor a = new SeverWindowAdaptor(this);
        addWindowListener(a);
    }

    public void actionPerformed(ActionEvent ae) {
        try{
            String str = ae.getActionCommand();

            if(str.equals("Start")) {
                String str2 = tfPort.getText(); //获取输入的服务器号
                if(!str2.equals("")) {
                    try {
                        serverSocket = new ServerSocket(Integer.parseInt(str2)); //创建服务器
                        hint.setText("Server is running....");
                        tfPort.setEnabled(false);

                        startSever.setEnabled(false);
                        stopSever.setEnabled(true);

                        flag.count = 0;
                        clients = new ArrayList<>(MaxClientAllowed);

                        // TODO: 创建5个socket
                        count = new Socket[5];
                        cj = 5;
                        cl = 0;

                        thread = new Thread(this,"Running");
                        thread.start();
                    } catch(Exception e) {
                        hint.setText("Either the port no. is invalid or is in use");
                    }
                } else {
                    hint.setText("Enter port no.");
                }
            }
            if(str.equals("Stop")) {
                try{
                    serverSocket.close();
                } catch(Exception ee) {
                    hint.setText("Error closing server");
                }
                hint.setText("Server is closed");
                tfPort.setEnabled(true);

                startSever.setEnabled(true);
                stopSever.setEnabled(false);

                serverSocket = null;
                thread = null;

                for(int i = 0; i < MaxClientAllowed; i++) {
                    try{
//                        count[i].close();
                        clients.get(i).socket.close();
                    } catch(Exception e) {
                        return;
                    }
                }
            }
        } catch(Exception ex) {}
    }

    public void run() {
        while(true) {
            if(serverSocket.isClosed()) {
                return;
            }
            try{
                Socket client = serverSocket.accept();
                // TODO: 循环接收消息对象
                ObjectInputStream obj = new ObjectInputStream(client.getInputStream());
                msg = (ClientMessage) obj.readObject();

                ta.append(msg.senderID+" >>>> "+msg.msgText+"\n");

                NewSocket client2 = new NewSocket();
                client2.ID = msg.senderID;
                client2.socket = client;

                clients.add(client2);

                // TODO: 将正在连接的socket加入到count数组
                if(cl < cj) {
                    count[cl] = client;
                } else {
                    Socket temp[] = new Socket[cj];
                    for(int i = 0; i < cj; i++) {
                        temp[i] = count[i];
                    }

                    count = new Socket[cj+5];
                    for(int i = 0; i < cj; i++) {
                        count[i] = temp[i];
                    }
                    count[cj] = client;
                    cj = cj + 5;
                }
                cl++;
                flag.count = cl;

                // 将所用客户发送的 msg 对象保存至 msg_all 数组
//                ObjectInputStream[] objr_all = new ObjectInputStream[flag.count];
//                ClientMessage[] msg_all = new ClientMessage[flag.count];
//                for(int i = 0; i < flag.count; i++) {
//                    objr_all[i] = new ObjectInputStream(count[i].getInputStream());
//                    msg_all[i] = (ClientMessage) objr_all[i].readObject();
//                }

                // TODO: 将消息对象发送给对应的socket
//                for(int i = 0; i < flag.count; i++) {
////                    if (msg_all[i].senderID.equals(msg.receiverID)) {
//                    if (true) {
//                        try {
//                            ClientMessage m = (ClientMessage) new ObjectInputStream(count[i].getInputStream()).readObject();
//                            if (m.senderID.equals(msg.receiverID)) {
//                                System.out.println("send from " + m.senderID);
//                                System.out.println("send  " + m.msgText);
//                                ObjectOutputStream objw = new ObjectOutputStream(count[i].getOutputStream());
//                                objw.writeObject(msg);
//                            }
//                        } catch (Exception e) {
//
//                        }
//                    }
//                }

                for(int i = 0; i < flag.count; i++) {
                    try{
                        ObjectOutputStream objw = new ObjectOutputStream(clients.get(i).socket.getOutputStream());
                        objw.writeObject(msg);
                    }catch(Exception e) {}
                }

                new NewThread(client2, msg, flag, clients, this, serverSocket);

            } catch(Exception e) {
                hint.setText("Server is stopped");
                tfPort.setEnabled(true);
                try{
                    serverSocket.close();
                } catch(Exception ey){
                    hint.setText("Error closing server");
                }
            }
        }
    }
}
