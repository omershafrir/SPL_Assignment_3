package bgu.spl.net.srv.Messages.clientToServer;

import bgu.spl.net.srv.Messages.Message;

public class PMMessage extends Message {
    private String username;
    private String content;
    private String dateAndTime;

    public PMMessage(String username, String content, String dateAndTime) {
        super((short)6);
        this.username = username;
        this.content = content;
        this.dateAndTime = dateAndTime;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    @Override
    public String toString() {
        return opcode+username+(char)0+content+(char)0+dateAndTime+(char)0;
    }
}
