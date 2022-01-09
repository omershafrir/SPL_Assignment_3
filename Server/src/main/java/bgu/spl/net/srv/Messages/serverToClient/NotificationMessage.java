package bgu.spl.net.srv.Messages.serverToClient;

import bgu.spl.net.srv.Messages.Message;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class NotificationMessage extends Message {
    private byte notificationType;
    private String postingUser;
    private String content;

    public NotificationMessage(byte notificationType, String postingUser, String content) {
        super((short)9);
        this.notificationType = notificationType;       // 0 if type = PM , 1 if type = public message
        this.postingUser = postingUser;
        this.content = content;
    }

    public byte getNotificationType() {
        return notificationType;
    }

    public String getPostingUser() {
        return postingUser;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return ""+0+opcode+notificationType+postingUser+'\0'+content+'\0';
    }

    @Override
    public byte[] encode() {
        System.out.println("NOTIFICATION TOSTRING: "+toString());
        System.out.println("NOTIFICATION BYTE[] IS: "+
                Arrays.toString((""+0+opcode+notificationType+postingUser+'\0'+content+'\0').getBytes(StandardCharsets.UTF_8)));        ///////////////////
        return (""+0+opcode+notificationType+postingUser+'\0'+content+'\0').getBytes(StandardCharsets.UTF_8);
    }
}
