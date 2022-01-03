package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.srv.Messages.Message;
import bgu.spl.net.srv.Messages.clientToServer.*;
import bgu.spl.net.srv.Messages.serverToClient.*;
import bgu.spl.net.srv.Server;
import bgu.spl.net.srv.User;


public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message>{

//    private Server server;
    //using connection DS as the logged in users in DB
    private ConnectionsImpl connections;
    private ConnectionHandler connectionHandler;
    private boolean shouldTerminate = false;

    public BidiMessagingProtocolImpl(ConnectionHandler connectionHandler){
        this.connectionHandler = connectionHandler;
    }

    @Override
    public void start(int connectionId, Connections<Message> connections) {
        this.connections = (ConnectionsImpl) connections;

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
            processLogin((LoginMessage)message);

        }
        else if (message instanceof LogoutMessage){
            processLogout();
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
        if(connections.isRegistered(message.getUsername())){
            connections.send(connections.getUserID(message.getUsername()), new ERRORMessage((short)1));
        }
        else{
            connections.register(message, connectionHandler);
            connections.send(connections.getUserID(message.getUsername()), new ACKMessage((short)1,null));
        }

    }

    private void processLogin(LoginMessage message){
        //check if already logged in
        if(!connections.isRegistered(message.getUsername()) || connections.isLogedIn(message.getUsername())
                || message.getCaptcha() == (char)0){
            connections.send(connections.getUserID(message.getUsername()), new ERRORMessage((short)2));
        }
        else{
            connections.login(message);
            connections.send(connections.getUserID(message.getUsername()), new ACKMessage((short)2,null));
        }
    }


    private void processLogout(){
        if(!connections.thereIsSomeOneHere()){
            connections.send(connections.getUserID(connectionHandler), new ERRORMessage((short)3));
        }
        else{ //need to send the server to connect, client leaves after getting an ACK message
            connections.send(connections.getUserID(connectionHandler), new ACKMessage((short)3,null));
        }
    }
    private void processFollow(FollowMessage message){
        //if follow failed / !logged in ERROR message
        if(!connections.isLogedIn(message.getUsername())){
            connections.send(connections.getUserID(message.getUsername()), new ERRORMessage((short)4));
        }
        else{
            switch (message.getCommand()){
                //follow
                case (0):
                    if(!connections.isFollowing(message,connections.getUserID(connectionHandler))){
                        connections.follow(message);
                    }
                    else{
                        connections.send(connections.getUserID(message.getUsername()), new ERRORMessage((short)4));
                    }
                    break;
                //unfollow
                case (1):
                    if(connections.isFollowing(message,connections.getUserID(connectionHandler))){
                        connections.unfollow(message);
                    }
                    else{
                        connections.send(connections.getUserID(message.getUsername()), new ERRORMessage((short)4));
                    }
                    break;
            }
        }

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
