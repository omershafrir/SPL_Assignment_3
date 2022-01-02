package bgu.spl.net.srv.Messages.serverToClient;

import bgu.spl.net.srv.Messages.Message;

import java.nio.charset.StandardCharsets;

public class ACKMessage extends Message {
    private short messageOpcode;
    private String optional;

    public ACKMessage(short messageOpcode, String optional) {
        super((short)10);
        this.messageOpcode = messageOpcode;
        if(optional != null)
            this.optional = optional;
        else
            this.optional = "";
    }

    public int getMessageOpcode() {
        return messageOpcode;
    }

    public String getOptional() {
        return optional;
    }

    @Override
    //not up to date
    public String toString() {
        if(optional != null)
            return ""+opcode+(char)0+messageOpcode+optional;
        else
            return ""+opcode+messageOpcode+optional;
    }

    @Override
    public byte[] encode() {
        if(messageOpcode <= (short) 9){
            return (""+opcode+(char)0+messageOpcode+optional).getBytes(StandardCharsets.UTF_8);
        }
        else
            return (""+opcode+messageOpcode+optional).getBytes(StandardCharsets.UTF_8);
    }
}
