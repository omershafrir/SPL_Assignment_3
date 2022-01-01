package bgu.spl.net.srv.Messages.serverToClient;

import bgu.spl.net.srv.Messages.Message;

public class ERRORMessage extends Message {
    private int messageOpcode;

    public ERRORMessage(int messageOpcode) {
        super((short)11);
        this.messageOpcode = messageOpcode;
    }

    public int getMessageOpcode() {
        return messageOpcode;
    }
}
