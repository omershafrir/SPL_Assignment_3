package bgu.spl.net.api.bidi;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.srv.Messages.Message;
import bgu.spl.net.srv.Messages.clientToServer.*;
import bgu.spl.net.srv.Messages.serverToClient.ACKMessage;
import bgu.spl.net.srv.Messages.serverToClient.ERRORMessage;
import bgu.spl.net.srv.Messages.serverToClient.NotificationMessage;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<String> {

    private HashMap<Short, Class<? extends Message>> map = new HashMap();
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private int curr = 0;

    public MessageEncoderDecoderImpl() {
        map.put((short) 1, RegisterMessage.class);
        map.put((short) 2, LoginMessage.class);
        map.put((short) 3, LogoutMessage.class);
        map.put((short) 4, FollowMessage.class);
        map.put((short) 5, PostMessage.class);
        map.put((short) 6, PMMessage.class);
        map.put((short) 7, LogstatMessage.class);
        map.put((short) 8, StatMessage.class);
        map.put((short) 9, NotificationMessage.class);
        map.put((short) 10, ACKMessage.class);
        map.put((short) 11, ERRORMessage.class);
        map.put((short) 12, BlockMessage.class);
    }

    @Override
    public String decodeNextByte(byte nextByte) {
        if (nextByte == '\n') {
            return popString();
        }
        pushByte(nextByte);
        return null; //not a line yet
    }


    @Override
    public byte[] encode(String message) {
        return (message + "\n").getBytes(); //uses utf8 by default
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    private String popString() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        replaceZeros();
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }

    private short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    private byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        return bytesArr;
    }

    private void replaceZeros() {
        for (int i = 0; i < len; i++) {
            if (bytes[i] == 0)
                bytes[i] = 32;
        }
    }

//    private Class<? extends Message> getType(String msg) {
//        return map.get((short)msg.charAt(0));
//    }
//
//    public Message buildMessage(String msg) {
//        Message output = null;
//        Class<? extends Message> outputClass = getType(msg);
//        try {
//            output = outputClass.newInstance();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return output;
//    }
}