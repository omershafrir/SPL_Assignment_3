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

    @Override
    public String toString() {
        return opcode+listString()+(char)0;
    }

    private String listString(){
        String output="";
        if(usernames!=null) {
            int size = usernames.size();
            for (int i = 0; i < size; i++) {
                if (i < size - 1)
                    output += usernames.get(i).toString() + '|';
                else
                    output += usernames.get(i).toString();
            }
        }
        return output;
    }
}
