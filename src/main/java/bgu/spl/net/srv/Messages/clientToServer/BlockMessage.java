package bgu.spl.net.srv.Messages.clientToServer;

import bgu.spl.net.srv.Messages.Message;

import java.nio.charset.StandardCharsets;

public class BlockMessage extends Message {
    private String username;

    public BlockMessage(String username) {
        super((short) 12);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return opcode+username+'\0';
    }

    @Override
    public byte[] encode() {
        return (opcode+username+'\0').getBytes(StandardCharsets.UTF_8);
    }
}
