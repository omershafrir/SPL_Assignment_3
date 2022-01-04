package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Database;
import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.srv.Messages.Message;
import bgu.spl.net.srv.Messages.clientToServer.*;
import bgu.spl.net.srv.Messages.serverToClient.*;
import bgu.spl.net.srv.User;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;


public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message>{

//    private Server server;
    //using connection DS as the logged in users in DB
    private ConnectionsImpl connections;
    private ConnectionHandler connectionHandler;
    private boolean shouldTerminate = false;
    private Database database;
    private int idOfSender;
    public BidiMessagingProtocolImpl(ConnectionHandler connectionHandler){
        this.connectionHandler = connectionHandler;
        database = Database.getInstance();
        connections = ConnectionsImpl.getInstance();
        idOfSender = database.getUserID(connectionHandler);
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
                processFollow((FollowMessage) message);
        }
        else if (message instanceof PostMessage){
                processPost((PostMessage) message);
        }
        else if (message instanceof PMMessage){
            processPM((PMMessage) message);
        }
        else if (message instanceof LogstatMessage){
                processLogstat((LogstatMessage) message);
        }
        else if (message instanceof StatMessage){
                processStat((StatMessage) message);
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
        if(database.isRegistered(message.getUsername())){
            connections.send(database.getUserID(message.getUsername()), new ERRORMessage((short)1));
        }
        else{
            database.register(message, connectionHandler);
            connections.send(database.getUserID(message.getUsername()), new ACKMessage((short)1,null));
        }

    }

    private void processLogin(LoginMessage message){
        //check if already logged in
        if(!database.isRegistered(message.getUsername()) || database.isLogedIn(message.getUsername())
                || message.getCaptcha() == (char)0){
            connections.send(database.getUserID(message.getUsername()), new ERRORMessage((short)2));
        }
        else{
            database.login(message);
            connections.send(database.getUserID(message.getUsername()), new ACKMessage((short)2,null));
        }
    }


    private void processLogout(){
        if(!database.thereIsSomeOneHere()){
            connections.send(idOfSender, new ERRORMessage((short)3));
        }
        else{ //need to send the server to connect, client leaves after getting an ACK message
            connections.send(idOfSender , new ACKMessage((short)3,null));
        }
    }
    private void processFollow(FollowMessage message){
        //if follow failed / !logged in ERROR message
        if(!database.isLogedIn(message.getUsername())){
            connections.send(database.getUserID(message.getUsername()), new ERRORMessage((short)4));
        }
        else{
            switch (message.getCommand()){
                //follow
                case (0):
                    if(!database.isFollowing(message.getUsername(),idOfSender)){
                        boolean success = database.follow(message,idOfSender);
                        if(!success){
                            connections.send(database.getUserID(message.getUsername()), new ERRORMessage((short)4));
                        }
                        else{ //success
                            connections.send(idOfSender, new ACKMessage((short)4,null));
                        }
                    }
                    else{
                        connections.send(database.getUserID(message.getUsername()), new ERRORMessage((short)4));
                    }
                    break;
                //unfollow
                case (1):
                    if(database.isFollowing(message.getUsername(),idOfSender)){
                        boolean success = database.unfollow(message.getUsername(),idOfSender);
                        if(!success){
                            connections.send(database.getUserID(message.getUsername()), new ERRORMessage((short)4));
                        }
                        else{ // success
                            connections.send(idOfSender, new ACKMessage((short)4,null));
                        }
                    }
                    else{
                        connections.send(database.getUserID(message.getUsername()), new ERRORMessage((short)4));
                    }
                    break;
            }
        }

    }
    private void processPost(PostMessage message){
        //check if the sender of this message is logged in
        if(!database.isLogedIn(database.getRegisteredUserName(idOfSender))
                || !database.isRegistered(idOfSender) ){
            connections.send(idOfSender, new ERRORMessage((short)5));
        }
        else{ // the sender is registered and logged in

            //TODO : create the vector
            //assuming I have a vector that holds the usernames that were mentioned in the message with
            // a @username, combined with the list of follower of ths SENDER - need to prevent doubles

            Vector<User> sendto = new Vector<>();
            //go through the vec and send the message to all users that need to get it
            for(User user : sendto){
                int idOfUser = database.getUserID(user.getUserName());
                boolean success = connections.send(idOfUser,message);
                if(!success){
                    connections.send(idOfSender, new ERRORMessage((short)5));
                }
                else{ //success
                    //add message to DATABASE
                    database.addMessage(message,idOfSender);
                    connections.send(idOfSender, new ACKMessage((short)5,null));
                }
            }

        }
    }
    private void processPM(PMMessage message) {
        if (!database.isLogedIn(idOfSender) || !database.isRegistered(message.getUsername())
                || !database.isFollowing(message.getUsername(), idOfSender)) {
            connections.send(idOfSender, new ERRORMessage((short) 6));
        } else { // the sender is logged in

            //TODO : filter the words
            PMMessage filteredMessage = new PMMessage("username", "content", "dateAndTime");
            int recipientID = database.getUserID(message.getUsername());
            boolean success = connections.send(recipientID, filteredMessage);
            if (!success) {
                connections.send(idOfSender, new ERRORMessage((short)6));
            } else { //success
                //add FILTERED message to DATABASE
                database.addMessage(filteredMessage, idOfSender);
                connections.send(idOfSender, new ACKMessage((short)6,null));
            }
        }
    }

    /**
     * A LOGSTAT message is used to recieve data on a logged in users
     * ---(the age of every user ,
     * number of posts every user posted,
     * number of every user’s followers,
     * number of users the user is following)--- NOT INCLUDED BLOCKED USERS.
     * In order to send a LOGSTAT message the user must be logged in, otherwise an ERROR message will
     * be returned to the client.
     * If user is not registered, an error message will be returned.
     *
     *
     * @param message
     */
    private void processLogstat(LogstatMessage message){
        if(!database.isLogedIn(idOfSender) || !database.isRegistered(idOfSender)){
            connections.send(idOfSender, new ERRORMessage((short) 7));
        }
        else{ // sender is logged in and registered
            Vector<User> toFilter = database.whoIsLoggedIN();
            //go through the vec and filter the blocked users
            //get the age, num of posts, num of followers, num of user i follow
            for(User toExtract : toFilter){
                if(!database.isBlocked(idOfSender,toExtract.getUserName())){
                    int ageOfUser = database.calculateAge(LocalDate.of(toExtract.getIntYEAR(),toExtract.getIntMONTH(),toExtract.getIntDAY()),LocalDate.now());
                    int numOfPosts = database.numOfPosts(toExtract);
                    int numOfFollowers = database.numOfFollowers(toExtract);
                    int numOfPeopleIFollow = database.numOfPeopleIFollow(toExtract);
                    String ageOfUserSTRING = String.valueOf(ageOfUser);
                    String numOfPostsSTRING = String.valueOf(numOfPosts);
                    String numOfFollowersSTRING = String.valueOf(numOfFollowers);
                    String numOfPeopleIFollowSTRING = String.valueOf(numOfPeopleIFollow);
                    //send ACK
                    connections.send(idOfSender, new ACKMessage((short)7,ageOfUserSTRING+" "+
                            numOfPostsSTRING+" "+ numOfFollowersSTRING+" "+numOfPeopleIFollowSTRING));
                }
            }


        }
    }

    /**
     * A STAT message is used to recieve data on a certain users (the age of every user , number of posts
     * every user posted, number of every user’s followers, number of users the user is following).
     * The ‘List of usernames’ parameter are a sequence of bytes in UTF-8 terminated by a zero byte,
     * With the following format ‘ usernam1|username2|username3|…’ for simplicity, you can assume
     * that ‘username’ doesn’t contain the symbol ‘|’.
     * In order to send a STAT message the user must be logged in, otherwise an ERROR message will be
     * returned to the client.
     * If user is not registered, an error message will be returned.
     * The returned ACK message will contain (for every single username) user’s age, number of posts a
     * user posted (not including PM’s), number of followers, number of users the user is following in the
     * optional section of the ACK message.
     * Example:
     * ACK-Opcode STAT-Opcode <Age><NumPosts> <NumFollowers> <NumFollowing>
     * @param message
     */
    private void processStat(StatMessage message){
        if(!database.isLogedIn(database.getRegisteredUserName(idOfSender))
                || !database.isRegistered(idOfSender) ){
            connections.send(idOfSender, new ERRORMessage((short)8));
        }
        else{ // the sender is registered and logged in
            //TODO : create the vector
            //assuming I have a vector that holds the usernames that were mentioned in the message with
            // in between the '|'
            Vector<User> toFilter = new Vector<>();
            //go through the vec and filter the blocked users
            //get the age, num of posts, num of followers, num of user i follow
            for(User toExtract : toFilter){
                if(!database.isBlocked(idOfSender,toExtract.getUserName())){
                    int ageOfUser = database.calculateAge(LocalDate.of(toExtract.getIntYEAR(),toExtract.getIntMONTH(),toExtract.getIntDAY()),LocalDate.now());
                    int numOfPosts = database.numOfPosts(toExtract);
                    int numOfFollowers = database.numOfFollowers(toExtract);
                    int numOfPeopleIFollow = database.numOfPeopleIFollow(toExtract);
                    String ageOfUserSTRING = String.valueOf(ageOfUser);
                    String numOfPostsSTRING = String.valueOf(numOfPosts);
                    String numOfFollowersSTRING = String.valueOf(numOfFollowers);
                    String numOfPeopleIFollowSTRING = String.valueOf(numOfPeopleIFollow);
                    //send ACK
                    connections.send(idOfSender, new ACKMessage((short)7,ageOfUserSTRING+" "+
                            numOfPostsSTRING+" "+ numOfFollowersSTRING+" "+numOfPeopleIFollowSTRING));
                }
            }
        }
    }

    /**
     * This message will be sent from the server for any PM sent to the user,
     *  post sent by someone the user is following, or a post that
     * contained @<MyUsername> in the content of a message.
     * A user will recive any POST/PM notification sent after follow (for users the current user is following)
     * that he didn’t see. I.e. wasn’t logged in when the other user posted/sent the message. (Clue: for
     * each user, save timestamp of last message recieved from each of the other users / timestamp of the
     * follow command)
     * @param message
     */


    /**
     * Message send to block a specific user.
     * Blocked user can’t follow, PM, or show any information about the user who blocked him.
     * Once user blocking acknowledged, both users (the blocking and the blocked) stop following each
     * other.
     * If ‘username’ doesn’t exist, and ERROR message will be sent back.
     * @param message
     */
    private void processBlock(BlockMessage message){
        if(!database.isRegistered(message.getUsername())){
            connections.send(idOfSender, new ERRORMessage((short)12));
        }
        else{ //username exists
            // UNFOLLOW each other
            User Blocker = database.getUserByID(idOfSender);
            database.unfollow(message.getUsername(),idOfSender);
            int BlockedID = database.getUserID(message.getUsername());
            database.unfollow(Blocker.getUserName(), BlockedID);
        }

    }


}
