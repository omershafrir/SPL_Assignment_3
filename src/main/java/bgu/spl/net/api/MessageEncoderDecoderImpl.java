package bgu.spl.net.api;

import bgu.spl.net.srv.Messages.Message;
import bgu.spl.net.srv.Messages.clientToServer.*;
import bgu.spl.net.srv.Messages.serverToClient.ACKMessage;
import bgu.spl.net.srv.Messages.serverToClient.ERRORMessage;
import bgu.spl.net.srv.Messages.serverToClient.NotificationMessage;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len= 0 ;

    public MessageEncoderDecoderImpl() {
    }

    @Override
    public Message decodeNextByte(byte nextByte) {
        if (nextByte == ';') {
            return buildMessage();
        }
        pushByte(nextByte);
        return null; //not a line yet
    }

    @Override
    public byte[] encode(Message message) {
        byte[] without = message.encode(); //uses utf8 by default
        byte[] with = Arrays.copyOf(without , without.length+2);
        with[with.length-2] = ';';
        with[with.length-1] = '\n';
        return with;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    private short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    public Message buildMessage() {
        Message output = null;
        int opcode = Integer.parseInt(new String(bytes,0,2,StandardCharsets.UTF_8));
        switch (opcode){
            case 1:
                output = buildRegister();
                break;
            case 2:
                output = buildLogin();
                break;
            case 3:
                output = new LogoutMessage();
                break;
            case 4:
                output = buildFollow();
                break;
            case 5:
                output = buildPost();
                break;
            case 6:
                output =  buildPM();
                break;
            case 7:
                output = new LogstatMessage();
                break;
            case 8:
                output = buildStat();
                break;
            case 9:
                output = buildNotification();
                break;
            case 10:
                output = buildACK();
                break;
            case 11:
                output = buildERROR();
                break;
            case 12:
                output = buildBlock();
                break;
        }
        len=0;
        return output;
    }

    private Message buildRegister(){
        int indexStart = 2;
        int curr = 2;

        while(bytes[curr] != '\0'){
            curr++;
        }

        String username = new String(bytes, 2,curr - 2,StandardCharsets.UTF_8);
        indexStart = curr + 1;
        curr++;
        while(bytes[curr] != '\0'){
            curr++;
        }

        String password = new String(bytes, indexStart,curr - indexStart,StandardCharsets.UTF_8);
        indexStart = curr + 1;
        curr++;
        while(bytes[curr] != '\0'){
            curr++;
        }
        String birthday = new String(bytes, indexStart,curr - indexStart,StandardCharsets.UTF_8);
        return new RegisterMessage(username,password,birthday);
    }

    private Message buildLogin(){
        int indexStart = 2;
        int curr = 2;

        while(bytes[curr] != '\0'){
            curr++;
        }
        String username = new String(bytes, 2,curr - 2,StandardCharsets.UTF_8);
        indexStart = curr + 1;
        curr++;
        while(bytes[curr] != '\0'){
            curr++;
        }
        String password = new String(bytes, indexStart,curr - indexStart,StandardCharsets.UTF_8);
        indexStart = curr + 1;
        curr++;
        return new LoginMessage(username,password,  (byte)(bytes[curr]-48));
    }

    private Message buildFollow(){
        int indexStart = 3;
        int curr = 3;

        while(bytes[curr] != ';'){
            curr++;
        }
        String username = new String(bytes, indexStart,curr - indexStart,StandardCharsets.UTF_8);
        return new FollowMessage((byte) (bytes[2]-48), username);
    }

    private Message buildPost(){
        int indexStart = 2;
        int curr = 2;
        while(bytes[curr] != '\0'){
            curr++;
        }
        String content = new String(bytes, indexStart,curr - indexStart,StandardCharsets.UTF_8);
        return new PostMessage(content);
    }

    private Message buildPM(){
        int indexStart = 2;
        int curr = 2;

        while(bytes[curr] != '\0'){
            curr++;
        }
        String username = new String(bytes, 2,curr - 2,StandardCharsets.UTF_8);
        indexStart = curr + 1;
        curr++;
        while(bytes[curr] != '\0'){
            curr++;
        }
        String content = new String(bytes, indexStart,curr - indexStart,StandardCharsets.UTF_8);
        indexStart = curr + 1;
        curr++;
        while(bytes[curr] != '\0'){
            curr++;
        }
        String dateAndTime = new String(bytes, indexStart,curr - indexStart,StandardCharsets.UTF_8);
        return new PMMessage(username,content,dateAndTime);
    }

    private Message buildStat(){
        LinkedList<String> nameString = new LinkedList<>();
        int indexStart = 2;
        int curr = 2;
        while(bytes[curr] != '\0'){
            curr++;
            if(bytes[curr] == '|'){
                nameString.add(new String(bytes, indexStart,curr - indexStart,StandardCharsets.UTF_8));
                indexStart = curr + 1;
            }
        }
        return new StatMessage(nameString);

    }

    private Message buildNotification(){
        int indexStart = 3;
        int curr = 3;

        while(bytes[curr] != '\0'){
            curr++;
        }
        String username = new String(bytes, 3,curr - 3,StandardCharsets.UTF_8);
        indexStart = curr + 1;
        curr++;
        while(bytes[curr] != '\0'){
            curr++;
        }

        String content = new String(bytes, indexStart,curr - indexStart,StandardCharsets.UTF_8);
        return new NotificationMessage((byte) (bytes[2]-48),username,content);
    }

    private Message buildACK(){
        int indexStart = 4;
        int curr = 4;
        byte[] toConvert = new byte[2];
        toConvert[0] = bytes[2];
        toConvert[1] = bytes[3];
        while(bytes[curr] != ';'){
            curr++;
        }
        String optional = new String(bytes, indexStart,curr - indexStart,StandardCharsets.UTF_8);
        return new ACKMessage((byte)(bytesToShort(toConvert)-48), optional);
    }

    private Message buildERROR(){
        byte[] toConvert = new byte[2];
        toConvert[0] = bytes[2];
        toConvert[1] = bytes[3];
        return new ERRORMessage((byte)(bytesToShort(toConvert)-48));
    }

    private Message buildBlock(){
        int curr = 2;

        while(bytes[curr] != '\0'){
            curr++;
        }
        String username = new String(bytes, 2,curr - 2,StandardCharsets.UTF_8);
        return new BlockMessage(username);
    }
}