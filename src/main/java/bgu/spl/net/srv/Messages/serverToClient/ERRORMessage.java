package bgu.spl.net.srv.Messages.serverToClient;

import bgu.spl.net.srv.Messages.Message;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ERRORMessage extends Message {
    private int messageOpcode;

    public ERRORMessage(short messageOpcode) {
        super((short)11);
        this.messageOpcode = messageOpcode;
    }

    public int getMessageOpcode() {
        return messageOpcode;
    }

    @Override
    public String toString() {
        if(messageOpcode <= (short) 9){
            return (""+opcode+'0'+messageOpcode);
        }
        else
            return (""+opcode+messageOpcode);
    }

    @Override
    public byte[] encode() {
        if(messageOpcode <= (short) 9){
//            System.out.println(Arrays.toString((""+opcode+messageOpcode).getBytes(StandardCharsets.UTF_8)));     ////////
            System.out.println((""+opcode+'0'+messageOpcode));     ////////
            return (""+opcode+'0'+messageOpcode).getBytes(StandardCharsets.UTF_8);
        }
        else
            return (""+opcode+messageOpcode).getBytes(StandardCharsets.UTF_8);
    }
}
