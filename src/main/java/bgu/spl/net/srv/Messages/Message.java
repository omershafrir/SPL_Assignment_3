package bgu.spl.net.srv.Messages;

import java.util.Vector;

public abstract class Message {
    public final short opcode;

    public Message(short opcode) {
        this.opcode = opcode;
    }
//    public abstract Vector<byte> toArray();


}
