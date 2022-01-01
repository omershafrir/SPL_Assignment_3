package bgu.spl.net.srv.Messages.serverToClient;

import bgu.spl.net.srv.Messages.Message;

public class NotificationMessage extends Message {
    private byte notificationType;
    private String postingUser;
    private String content;

    public NotificationMessage(byte notificationType, String postingUser, String content) {
        super((short)9);
        this.notificationType = notificationType;
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
}
