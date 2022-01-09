package bgu.spl.net.info.impl.echo;

import bgu.spl.net.api.MessagingProtocol;
import java.time.LocalDateTime;

public class EchoProtocol implements MessagingProtocol<String> {

    private boolean shouldTerminate = false;

    @Override
    public String process(String msg) {
        shouldTerminate = "bye".compareTo(msg) == 0;
        System.out.println("[" + LocalDateTime.now() + "]: " + "THIS IS ME TAKING OVER BITCHHHHHH           "+ msg);
        return createEcho(msg);
    }

    private String createEcho(String message) {
        String echoPart = message.substring(Math.max(message.length() - 2, 0), message.length());
        return message + "src/main " + echoPart + "src/main " + echoPart + "Server/src/main";
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
