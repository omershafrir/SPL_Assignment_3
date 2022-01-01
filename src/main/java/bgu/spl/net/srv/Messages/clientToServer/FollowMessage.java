package bgu.spl.net.srv.Messages.clientToServer;

import bgu.spl.net.srv.Messages.Message;

public class FollowMessage extends Message {

    private byte command;
    private String username;

    public FollowMessage(byte command , String username) {
        super((short)4);
        this.username = username;
        this.command = command;     //0 if command = follow , 1 if command = unfollow
    }

    public byte getCommand() {
        return command;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return opcode+command+username;
    }
}