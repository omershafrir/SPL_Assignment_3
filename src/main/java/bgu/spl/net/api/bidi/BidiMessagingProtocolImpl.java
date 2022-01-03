package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.srv.Messages.Message;
import bgu.spl.net.srv.Messages.clientToServer.*;
import bgu.spl.net.srv.Messages.serverToClient.*;
import bgu.spl.net.srv.Server;


public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message>{

    private Server<?> server;
    //using connection DS as the logedin users in DB
    private ConnectionsImpl connections;
    private ConnectionHandler connectionHandler;
    private boolean shouldTerminate = false;

    public BidiMessagingProtocolImpl(Server server,ConnectionsImpl connections, ConnectionHandler connectionHandler){
        this.server = server;
        this.connections = connections;
        this.connectionHandler = connectionHandler;
    }

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
        else if (message instanceof LoginMessage){


        }
        else if (message instanceof LogoutMessage){

        }
        else if (message instanceof FollowMessage){

        }
        else if (message instanceof PostMessage){

        }
        else if (message instanceof PMMessage){

        }
        else if (message instanceof LogstatMessage){

        }
        else if (message instanceof StatMessage){

        }
        else if (message instanceof NotificationMessage){

        }
        else if (message instanceof ACKMessage){

        }
        else if (message instanceof ERRORMessage){

        }
        else{ //(message instanceof BlockMessage)

        }

    }


    @Override
    public boolean shouldTerminate() {
        return false;
    }

    private void processRegister(RegisterMessage message){
        //check if the username already registered - if yes return an error
        if(this.connections.isLogedIn(message.getUsername(),message.getPassword(),this.connectionHandler)){

        }

    }

    private void processLogin(LoginMessage message){

    }


    private void processLogout(LogoutMessage message){

    }
    private void processFollow(FollowMessage message){

    }
    private void processPost(PostMessage message){

    }
    private void processPM(PMMessage message){

    }
    private void processLogstat(LogstatMessage message){

    }
    private void processStat(StatMessage message){

    }
    private void processNotification(NotificationMessage message){

    }

    private void processACK(ACKMessage message){

    }
    private void processERROR(ERRORMessage message){

    }
    private void processBlock(BlockMessage message){

    }


}
