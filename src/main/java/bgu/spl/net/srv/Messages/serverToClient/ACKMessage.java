package bgu.spl.net.srv.Messages.serverToClient;

import bgu.spl.net.srv.Messages.Message;

public class ACKMessage extends Message {
    private short messageOpcode;
    private Object optional;

    public ACKMessage(short messageOpcode, Object optional) {
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

    @Override
    public String toString() {
        if(optional != null)
            return opcode+messageOpcode+optional.toString();
        else
            return ""+opcode+messageOpcode;
    }
}
