package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

class Client extends JFrame implements ActionListener,Runnable {
    Socket socket = null;
    JLabel l1, l2, l3, l4, l5, l6, jtf3, l7, l8;
    JTextField jtf1, jtf2, jtf4, jtf5, jtf6, jtf7;
    TextArea ta;
    ClientMessage msg = new ClientMessage();
    InetAddress host;
    int port = 5001;
    Thread t = null;
    JButton jb,jb2,jb3, jb4;

    Client(String s) {
        super(s);

        ClientWindowAdaptor a = new ClientWindowAdaptor(this);
        addWindowListener(a);

        l5 = new JLabel("Enter IP : ");
        add(l5);
        jtf4 = new JTextField(15);
        add(jtf4);
        jtf4.setText("127.0.0.1");

        add(new JLabel("                    "));

        l6 = new JLabel("Enter Port : ");
        add(l6);
        jtf5 = new JTextField(15);
        add(jtf5);
        jtf5.setText("5001");

        add(new JLabel("                    "));

        l1 = new JLabel("Your ID :  ");
        add(l1);
        jtf1 = new JTextField(15);
        add(jtf1);

        jb2 = new JButton("Connect");
        add(jb2);
        jb2.addActionListener(this);

        jb3 = new JButton("Disconnect");
        add(jb3);
        jb3.addActionListener(this);
        jb3.setEnabled(false);

        add(new JLabel("                       "));

        l2 = new JLabel("Message : ");
        add(l2);
        jtf2 = new JTextField(40);
        add(jtf2);
        jtf2.setEditable(false);

        jb = new JButton("Send");
        add(jb);
        jb.addActionListener(this);
        jb.setEnabled(false);

        l8 = new JLabel("Private Message to :");
        add(l8);
        jtf7 = new JTextField(5);
        add(jtf7);
//        jtf7.setEditable(false);

        l7 = new JLabel("Private Message :");
        add(l7);
        jtf6 = new JTextField(17);
        add(jtf6);
//        jtf6.setEditable(false);

        jb4 = new JButton("Send Privately");
        add(jb4);
        jb4.addActionListener(this);
        jb4.setEnabled(false);

        l3 = new JLabel("Status : ");
        add(l3);
        jtf3 = new JLabel("Not connected to the server...");
        add(jtf3);

//        add(new JLabel(" \n "));
//
//        l4 = new JLabel("Received Messages : ");
//        add(l4);
        ta = new TextArea("",12,80);
        add(ta);
        ta.setFont(Font.getFont("verdana"));
        ta.setBackground(Color.WHITE);
        ta.setEditable(false);

        jtf3.setText("Not connected to Server, click 'Connect'");
    }

    public void actionPerformed(ActionEvent ae) {
        try{
            String str = ae.getActionCommand();

            if(str.equals("Disconnect")) {
                try {
                    jb.setEnabled(false);
                    jtf2.setEditable(false);
                    jb2.setEnabled(true);
                    jb3.setEnabled(false);
                    jtf4.setEditable(true);
                    jtf5.setEditable(true);
                    jtf1.setEditable(true);
                    jtf7.setEnabled(false);
                    jtf6.setEnabled(false);
                    jb4.setEnabled(false);
                    socket.close();
                    socket = null;
                } catch(Exception e) {}
            }

            if(str.equals("Send")) {
                msg.senderID = jtf1.getText();

                msg.msgText = jtf2.getText();
                jtf2.setText("");

                if(!msg.senderID.equals("") && !msg.msgText.equals("")) {
                    sendData();
                } else {
                    jtf3.setText("Message was not sent, type a message");
                }
            }

            // TODO: 在这写发送p2p信息
            if (str.equals("Send Privately")){
                msg.senderID = jtf1.getText();

                msg.receiverID = jtf7.getText();
                jtf7.setText("");

                msg.msgText = jtf6.getText();
                jtf6.setText("");

                if(!msg.senderID.equals("") && !msg.receiverID.equals("") && !msg.msgText.equals("")) {
                    sendData();
                } else {
                    jtf3.setText("Message was not sent, check your inputs");
                }
            }

            if(str.equals("Connect")) {
                try{
                    host = InetAddress.getByName(jtf4.getText());
                    String p = jtf5.getText();

                    try{
                        if(socket!=null) {
                            socket.close();
                            socket = null;
                        }
                    } catch(Exception e) {}

                    if(!jtf1.getText().equals("")) {
                        socket = new Socket(host,Integer.parseInt(p));

                        // TODO: 发送上线成功
                        ObjectOutputStream obj = new ObjectOutputStream(socket.getOutputStream());
                        msg.senderID = jtf1.getText();
                        msg.msgText = " is now online at " + new Date().toString();
                        obj.writeObject(msg);

                        jtf2.setEditable(true);
                        jb.setEnabled(true);
                        jb2.setEnabled(false);
                        jb3.setEnabled(true);
                        jtf4.setEditable(false);
                        jtf5.setEditable(false);
                        jtf1.setEditable(false);
                        jtf7.setEnabled(true);
                        jtf6.setEnabled(true);
                        jb4.setEnabled(true);

                        jtf3.setText("Connection established with Server, start chatting");

                        t = new Thread(this,"Reading");
                        t.start();
                    }
                } catch(Exception e) {
                    jtf3.setText("Could not connect to Server, connect again");
                }
            }
        } catch(Exception e) {
            jtf3.setText("Action Error");
        }
    }

    // TODO: 发送完整消息对象
    public void sendData() {
        try {
            ObjectOutputStream obj = new ObjectOutputStream(socket.getOutputStream());
            if(!msg.senderID.equals("") && !msg.msgText.equals("")) {
                obj.writeObject(msg);
                jtf3.setText("Message was sent successfully");
            }
            msg.senderID = "";
            msg.msgText = "";
        } catch(Exception e) {
            jtf3.setText("Error occurred while sending message");}
    }


    public void run() {
        // TODO: 循环接收消息
        try{
            while(true) {
                // TODO: 接收完整消息对象
                ObjectInputStream obj = new ObjectInputStream(socket.getInputStream());
                ClientMessage msg = new ClientMessage();
                msg = (ClientMessage) obj.readObject();
                if(msg.senderID != null && msg.msgText != null) {
                    System.out.println("1"+msg.senderID);
                    System.out.println(jtf1.getText());
                    if(msg.senderID.equals(jtf1.getText()) && msg.msgText.equals(" is kicked")){
                        ta.append("You have been kicked offline");
                        socket.close();
                        socket = null;
                    }else{
                        ta.append(msg.senderID + " >> " + msg.msgText + "\n");
                    }
                }
            }
        } catch(Exception e) {
            jtf2.setEditable(false);
            jb2.setEnabled(true);
            jb.setEnabled(false);
            jb3.setEnabled(false);
            jtf4.setEditable(true);
            jtf5.setEditable(true);
            jtf1.setEditable(true);
            jtf7.setEnabled(false);
            jtf6.setEnabled(false);
            jb4.setEnabled(false);
            jtf3.setText("Connection Lost");
        }
    }
}
