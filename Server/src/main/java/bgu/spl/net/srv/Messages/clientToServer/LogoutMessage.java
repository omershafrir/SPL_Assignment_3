package bgu.spl.net.srv.Messages.clientToServer;

import bgu.spl.net.srv.Messages.Message;

import java.nio.charset.StandardCharsets;

public class LogoutMessage extends Message {
    public LogoutMessage() {
        super((short)3);
    }

    @Override
    public String toString() {
        return ""+opcode;
    }

    @Override
    public byte[] encode() {
        return (""+0+opcode).getBytes(StandardCharsets.UTF_8);
    }
}
