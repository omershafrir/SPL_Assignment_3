package bgu.spl.net.srv.Messages.clientToServer;

import bgu.spl.net.srv.Messages.Message;

import java.nio.charset.StandardCharsets;

public class LogstatMessage extends Message {
    public LogstatMessage() {
        super((short)7);
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
