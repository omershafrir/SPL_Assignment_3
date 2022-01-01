package bgu.spl.net.srv.Messages.clientToServer;

import bgu.spl.net.srv.Messages.Message;

public class PostMessage extends Message {
    private String content;

    public PostMessage(String content) {
        super((short)5);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return opcode+content+(char)0;
    }
}
