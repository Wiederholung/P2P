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

class Ser extends JFrame implements ActionListener,Runnable{
    Thread t;
    JButton startSever, stopSever, showList, kick, showStats;
    JTextField jtf2, jtf3, jtf4;
    JLabel jlabel;
    TextArea ta;

    ServerSocket server;
    ClientMessage msg = new ClientMessage();
    Data flag = new Data();

    Socket count[] = new Socket[5];
    int cj = 5;
    int cl = 0;

    Ser(String s) {
        super(s);

        flag.signal = 0;
        flag.count = 0;

        JLabel l3 = new JLabel("Enter Port No. : ");
        add(l3);

        jtf2 = new JTextField(7);
        jtf2.setText("5001");
        add(jtf2);

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

        jlabel = new JLabel("Server is not running...");
        add(jlabel);



        showList = new JButton("Show List");
        showList.addActionListener(this);
        add(showList);
        showList.setEnabled(false); //默认不能点击

        JLabel l5 = new JLabel("Enter Kick ID : ");
        add(l5);
        jtf3 = new JTextField(10);
        add(jtf3);

        kick = new JButton("Kick");
        kick.addActionListener(this);
        add(kick);
        kick.setEnabled(false); //默认不能点击

        JLabel l6 = new JLabel("Enter Stats ID : ");
        add(l6);
        jtf4 = new JTextField(10);
        add(jtf4);

        showStats = new JButton("Show Stats");
        showStats.addActionListener(this);
        add(showStats);
        showStats.setEnabled(false); //默认不能点击




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
                String str2 = jtf2.getText(); //获取输入的服务器号
                if(!str2.equals("")) {
                    try {
                        server = new ServerSocket(Integer.parseInt(str2)); //创建服务器
                        jlabel.setText("Server is running....");
                        jtf2.setEnabled(false);

                        startSever.setEnabled(false);
                        stopSever.setEnabled(true);
                        showList.setEnabled(true);
                        kick.setEnabled(true);
                        showStats.setEnabled(true);

                        flag.count = 0;

                        count = new Socket[5];
                        cj = 5;
                        cl = 0;

                        t = new Thread(this,"Running");
                        t.start();
                    } catch(Exception e) {
                        jlabel.setText("Either the port no. is invalid or is in use");
                    }
                } else {
                    jlabel.setText("Enter port no.");
                }
            }
            if(str.equals("Stop")) {
                try{
                    server.close();
                } catch(Exception ee) {
                    jlabel.setText("Error closing server");
                }
                jlabel.setText("Server is closed");
                jtf2.setEnabled(true);

                startSever.setEnabled(true);
                stopSever.setEnabled(false);
                showList.setEnabled(false);
                kick.setEnabled(false);
                showStats.setEnabled(false);

                server = null;
                t = null;

                for(int i = 0; i < flag.count; i++) {
                    try{
                        count[i].close();
                    } catch(Exception e) {}
                }
            }
        } catch(Exception ex) {}
    }

    public void run() {
        while(true) {
            if(server.isClosed()) {
                return;
            }
            try{
                Socket client = server.accept();

                ObjectInputStream obj = new ObjectInputStream(client.getInputStream());
                msg = (ClientMessage) obj.readObject();

                ta.append(msg.senderID+" >> "+msg.msgText+"\n");

                if( cl < cj) {
                    count[cl] = client;
                    cl++;
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
                    cl++;
                }
                flag.count = cl;

                for(int i = 0; i < flag.count; i++) {
                    try{
                        ObjectOutputStream objw = new ObjectOutputStream(count[i].getOutputStream());
                        objw.writeObject(msg);
                    }catch(Exception e) {}
                }

                new NewThread(client, msg, flag, count, this, server);
            } catch(Exception e) {
                jlabel.setText("Server is stopped");
                jtf2.setEnabled(true);
                try{
                    server.close();
                } catch(Exception ey){
                    jlabel.setText("Error closing server");
                }
            }
        }
    }
}
