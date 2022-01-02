package bgu.spl.net.srv.Messages;

import java.util.Vector;

public abstract class Message {
    public final short opcode;

    public Message(short opcode) {
        this.opcode = opcode;
    }
    public abstract String toString();

    public abstract byte[] encode();

    protected short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    protected byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        return bytesArr;
    }

}
