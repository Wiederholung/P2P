package Server;

import java.io.Serializable;

class Message implements Serializable {
    public volatile String senderID;
    public volatile String msgText;
}