package client;

import java.io.Serializable;

public class ClientMessage implements Serializable {
    public volatile String senderID;
    public volatile String msgText;
}
