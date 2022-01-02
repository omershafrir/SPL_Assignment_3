package bgu.spl.net.srv.Messages.clientToServer;

import bgu.spl.net.srv.Messages.Message;

public class LoginMessage extends Message {
    private String username;
    private String password;
    private byte captcha;

    public LoginMessage(String username, String password, byte captcha) {
        super((short)2);
        this.username = username;
        this.password = password;
        this.captcha = captcha;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public byte getCaptcha() {
        return captcha;
    }

    @Override
    public String toString() {
        return opcode+username+(char)0+password+(char)0+captcha;
    }
}
