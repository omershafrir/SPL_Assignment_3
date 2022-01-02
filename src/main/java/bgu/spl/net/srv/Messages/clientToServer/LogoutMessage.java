package bgu.spl.net.srv.Messages.clientToServer;

import bgu.spl.net.srv.Messages.Message;

public class LogoutMessage extends Message {
    public LogoutMessage() {
        super((short)3);
    }

    @Override
    public String toString() {
        return ""+opcode;
    }
}
