package bgu.spl.net.api.bidi;

public class BidiMessagingProtocolImpl<T> implements BidiMessagingProtocol<T>{

    private boolean shouldTerminate = false;


    @Override
    public void start(int connectionId, Connections<T> connections) {

    }

    @Override
    public void process(T message) {
        //if opcode 3 shouldTerminate = true
        if(message instanceof Short){
//            if(message.equals()){
                shouldTerminate = true;
//            }
        }
    }

    //private message that classifies to types of messegase

    @Override
    public boolean shouldTerminate() {
        return false;
    }
}
