package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.Messages.Message;
import bgu.spl.net.srv.Messages.clientToServer.RegisterMessage;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message>{

    private boolean shouldTerminate = false;


    @Override
    public void start(int connectionId, Connections<Message> connections) {

    }


    /**
     * each kind of message need a instanceof check
     * and
     * a function that determinants what it does
     * @param message
     */
    @Override
    public void process(Message message) {
        //if opcode 3 shouldTerminate = true
        if (message instanceof RegisterMessage){
            processRegister((RegisterMessage)message);
        }

    }

    //private message that classifies to types of messegase

    @Override
    public boolean shouldTerminate() {
        return false;
    }

    private void processRegister(RegisterMessage message){

    }
}
