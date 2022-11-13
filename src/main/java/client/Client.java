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

class Client extends JFrame implements ActionListener, Runnable {
    Socket socket = null;
    JLabel lID, lMsg, lStats, lIP, lPort, hint, lPMsg, lPMsgTo;
    JTextField tfID, tfMsg, tfIP, tfPort, tfPMsg, tfPMsgTo, tfKick, tfStats;
    TextArea ta;
    ClientMessage msg = new ClientMessage();
    InetAddress host;
    int port = 5001;
    Thread t = null;
    JButton send, connect, disconnect, sendPrivately, showList, kick, showStats;

    Client(String windowName) {
        super(windowName);

        ClientWindowAdaptor clientWindowAdaptor = new ClientWindowAdaptor(this);
        addWindowListener(clientWindowAdaptor);

        lIP = new JLabel("Enter IP : ");
        add(lIP);
        tfIP = new JTextField(15);
        add(tfIP);
        tfIP.setText("127.0.0.1");

        add(new JLabel("                    ")); // add an empty part to adjust the layout

        lPort = new JLabel("Enter Port : ");
        add(lPort);
        tfPort = new JTextField(15);
        add(tfPort);
        tfPort.setText("5001");

        add(new JLabel("                    "));

        lID = new JLabel("Your ID :  ");
        add(lID);
        tfID = new JTextField(15);
        add(tfID);

        connect = new JButton("Connect");
        add(connect);
        connect.addActionListener(this);

        disconnect = new JButton("Disconnect");
        add(disconnect);
        disconnect.addActionListener(this);
        disconnect.setEnabled(false);

        add(new JLabel("                       "));

        lMsg = new JLabel("Message : ");
        add(lMsg);
        tfMsg = new JTextField(40);
        add(tfMsg);
        tfMsg.setEditable(false);

        send = new JButton("Send");
        add(send);
        send.addActionListener(this);
        send.setEnabled(false);

        lPMsgTo = new JLabel("Private Message to :");
        add(lPMsgTo);
        tfPMsgTo = new JTextField(5);
        add(tfPMsgTo);
//        jtf7.setEditable(false);

        lPMsg = new JLabel("Private Message :");
        add(lPMsg);
        tfPMsg = new JTextField(17);
        add(tfPMsg);
//        jtf6.setEditable(false);

        sendPrivately = new JButton("Send Privately");
        add(sendPrivately);
        sendPrivately.addActionListener(this);
        sendPrivately.setEnabled(false);


        showList = new JButton("Show List");
        showList.addActionListener(this);
        add(showList);
        showList.setEnabled(false); //默认不能点击

        JLabel l5 = new JLabel("Enter Kick ID : ");
        add(l5);
        tfKick = new JTextField(10);
        add(tfKick);

        kick = new JButton("Kick");
        kick.addActionListener(this);
        add(kick);
        kick.setEnabled(false); //默认不能点击

        JLabel l6 = new JLabel("Enter Stats ID : ");
        add(l6);
        tfStats = new JTextField(10);
        add(tfStats);

        showStats = new JButton("Show Stats");
        showStats.addActionListener(this);
        add(showStats);
        showStats.setEnabled(false); //默认不能点击

        lStats = new JLabel("Status : ");
        add(lStats);
        hint = new JLabel("Not connected to the server...");
        add(hint);

        ta = new TextArea("",12,80);
        add(ta);
        ta.setFont(Font.getFont("verdana"));
        ta.setBackground(Color.WHITE);
        ta.setEditable(false);

        hint.setText("Not connected to Server, click 'Connect'");
    }

    public void actionPerformed(ActionEvent ae) {
        try{
            String str = ae.getActionCommand();

            if(str.equals("Disconnect")) {
                try {
                    send.setEnabled(false);
                    tfMsg.setEditable(false);
                    connect.setEnabled(true);
                    disconnect.setEnabled(false);
                    tfIP.setEditable(true);
                    tfPort.setEditable(true);
                    tfID.setEditable(true);
                    tfPMsgTo.setEnabled(false);
                    tfPMsg.setEnabled(false);
                    sendPrivately.setEnabled(false);

                    showList.setEnabled(false);
                    kick.setEnabled(false);
                    showStats.setEnabled(false);
                    tfKick.setEnabled(false);
                    tfStats.setEnabled(false);

                    socket.close();
                    socket = null;
                } catch(Exception e) {}
            }

            if(str.equals("Send")) {
                msg.senderID = tfID.getText();

                msg.receiverID = "All";

                msg.msgText = tfMsg.getText();
                tfMsg.setText("");

                if(!msg.senderID.equals("") && !msg.msgText.equals("")) {
                    sendData();
                } else {
                    hint.setText("Message was not sent, type a message");
                }
            }

            // TODO: 在这写发送p2p信息
            if (str.equals("Send Privately")){
                msg.senderID = tfID.getText();

                msg.receiverID = tfPMsgTo.getText();
                tfPMsgTo.setText("");

                msg.msgText = tfPMsg.getText();
                tfPMsg.setText("");

                if(!msg.senderID.equals("") && !msg.receiverID.equals("") && !msg.msgText.equals("")) {
                    sendData();
                } else {
                    hint.setText("Message was not sent, check your inputs");
                }
            }

            if(str.equals("Connect")) {
                try{
                    host = InetAddress.getByName(tfIP.getText());
                    String p = tfPort.getText();

                    try{
                        if(socket!=null) {
                            socket.close();
                            socket = null;
                        }
                    } catch(Exception e) {}

                    if(!tfID.getText().equals("")) {
                        socket = new Socket(host,Integer.parseInt(p));

                        // TODO: 发送上线成功
                        ObjectOutputStream obj = new ObjectOutputStream(socket.getOutputStream());
                        msg.senderID = tfID.getText();
                        msg.msgText = " is now online at " + new Date().toString();
                        obj.writeObject(msg);

                        tfMsg.setEditable(true);
                        send.setEnabled(true);
                        connect.setEnabled(false);
                        disconnect.setEnabled(true);
                        tfIP.setEditable(false);
                        tfPort.setEditable(false);
                        tfID.setEditable(false);
                        tfPMsgTo.setEnabled(true);
                        tfPMsg.setEnabled(true);
                        sendPrivately.setEnabled(true);

                        showList.setEnabled(true);
                        kick.setEnabled(true);
                        showStats.setEnabled(true);
                        tfKick.setEnabled(true);
                        tfStats.setEnabled(true);

                        hint.setText("Connection established with Server, start chatting");

                        t = new Thread(this,"Reading");
                        t.start();
                    }
                } catch(Exception e) {
                    hint.setText("Could not connect to Server, connect again");
                }
            }

            if(str.equals("Kick")) {
                msg.senderID = tfID.getText();
                msg.receiverID = tfKick.getText();
                hint.setText("");

                ta.append(msg.receiverID + " is kicked\n");
                msg.msgText = " is kicked";

                sendData();
            }

        } catch(Exception e) {
            hint.setText("Action Error");
        }
    }

    // TODO: 发送完整消息对象
    public void sendData() {
        try {
            ObjectOutputStream obj = new ObjectOutputStream(socket.getOutputStream());
            if(!msg.senderID.equals("") && !msg.msgText.equals("")) {
                obj.writeObject(msg);
                hint.setText("Message was sent successfully");
            }
            msg.senderID = "";
            msg.msgText = "";
        } catch(Exception e) {
            hint.setText("Error occurred while sending message");}
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
//                    System.out.println("1"+msg.senderID);
//                    System.out.println(tfID.getText());
//                    if(msg.receiverID.equals(tfID.getText()) && msg.msgText.equals(" is kicked")){
//                        ta.append("You have been kicked offline");
//                        socket.close();
//                        socket = null;
//                    }else{
                        ta.append(msg.senderID + " >> " + msg.msgText + "\n");
//                    }
                }
            }
        } catch(Exception e) {
            tfMsg.setEditable(false);
            connect.setEnabled(true);
            send.setEnabled(false);
            disconnect.setEnabled(false);
            tfIP.setEditable(true);
            tfPort.setEditable(true);
            tfID.setEditable(true);
            tfPMsgTo.setEnabled(false);
            tfPMsg.setEnabled(false);
            sendPrivately.setEnabled(false);

            showList.setEnabled(false);
            kick.setEnabled(false);
            showStats.setEnabled(false);
            tfKick.setEnabled(false);
            tfStats.setEnabled(false);

            hint.setText("Connection Lost");
        }
    }
}
