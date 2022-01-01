package bgu.spl.net.srv.Messages.clientToServer;

import bgu.spl.net.srv.Messages.Message;

import java.util.List;

public class StatMessage extends Message {
    private List<String> usernames;

    public StatMessage(List<String> usernames) {
        super((short)8);
        this.usernames = usernames;
    }

    public List<String> getUsernames() {
        return usernames;
    }
}
