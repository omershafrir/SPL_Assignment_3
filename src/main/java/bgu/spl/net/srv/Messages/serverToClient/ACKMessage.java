package bgu.spl.net.srv.Messages.serverToClient;

import bgu.spl.net.srv.Messages.Message;

public class ACKMessage extends Message {
    private int messageOpcode;
    private Object optional;

    public ACKMessage(int messageOpcode, Object optional) {
        super((short)10);
        this.messageOpcode = messageOpcode;
        this.optional = optional;
    }

    public int getMessageOpcode() {
        return messageOpcode;
    }

    public Object getOptional() {
        return optional;
    }
}
