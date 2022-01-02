package bgu.spl.net.srv.Messages.clientToServer;

import bgu.spl.net.srv.Messages.Message;

public class RegisterMessage extends Message {
    private String username;
    private String password;
    private String birthday;

    public RegisterMessage(String username, String password, String birthday) {
        super((short) 1);
        this.username = username;
        this.password = password;
        this.birthday = birthday;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getBirthday() {
        return birthday;
    }

    @Override
    public String toString() {
        return opcode+username+(char)0+password+(char)0+birthday+(char)0;
    }
}

