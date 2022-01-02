package bgu.spl.net.srv.Messages.serverToClient;

import bgu.spl.net.srv.Messages.Message;

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
        return opcode+notificationType+postingUser+(char)0+content+(char)0;
    }
}
