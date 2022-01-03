package bgu.spl.net.srv.Messages.serverToClient;

import bgu.spl.net.srv.Messages.Message;
import sun.security.util.ArrayUtil;
import java.util.Arrays;



import java.nio.charset.StandardCharsets;
import java.util.Vector;

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
//        return (""+opcode+'\0'+messageOpcode+optional);
        if(messageOpcode <= (short) 9){
            return (""+opcode+'0'+messageOpcode+optional);
        }
        else
            return (""+opcode+messageOpcode+optional);
    }

    @Override
    public byte[] encode() {
//        byte[] a = shortToBytes(opcode);
//        byte[] b = shortToBytes(messageOpcode);
//        byte[] c = optional.getBytes(StandardCharsets.UTF_8);
//        Vector<byte[]> vec = new Vector<>(); vec.add(a); vec.add(b); vec.add(c);
//        byte[] output = mergeArrays(vec);
//        System.out.println("OUTPUT: "+Arrays.toString(output));
//        return output;
//        return (""+opcode+'\0'+messageOpcode+optional).getBytes(StandardCharsets.UTF_8);
        if(messageOpcode <= 9){
            return (""+opcode+'0'+messageOpcode+optional).getBytes(StandardCharsets.UTF_8);
        }
        else {
            return ("" + opcode + messageOpcode + optional).getBytes(StandardCharsets.UTF_8);
        }
    }
}
