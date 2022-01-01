package bgu.spl.net.srv.Messages.clientToServer;

import bgu.spl.net.srv.Messages.Message;

public class LogstatMessage extends Message {
    public LogstatMessage() {
        super((short)7);
    }
}
