package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Database;
import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.srv.Messages.Message;
import bgu.spl.net.srv.Messages.clientToServer.*;
import bgu.spl.net.srv.Messages.serverToClient.*;
import bgu.spl.net.srv.User;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message>{

//    private Server server;
    //using connection DS as the logged in users in DB
    private ConnectionsImpl connections;
    private ConnectionHandler connectionHandler;
    private boolean shouldTerminate = false;
    private Database database;
    private int idOfSender;
    public BidiMessagingProtocolImpl( ){      //was :: public BidiMessagingProtocolImpl(ConnectionHandler connectionHandler)
//        this.connectionHandler = connectionHandler;
        database = Database.getInstance();
        connections = ConnectionsImpl.getInstance();
//        idOfSender = database.getUserID(connectionHandler);
    }

    @Override
    public void start(int connectionId, Connections<Message> connections) {
        this.connections = (ConnectionsImpl) connections;
        this.idOfSender = connectionId;
        this.connectionHandler = ((ConnectionsImpl<Message>) connections).getConnectionIDS().get(connectionId);
        System.out.println("ID OF SENDER: "+ this.idOfSender);
        System.out.println("IDS OF HANDLER: "+ this.connectionHandler);

    }

    /**
     * each kind of message need a instanceof check
     * and
     * a function that determinants what it does
     * @param message
     */
    @Override
    public void process(Message message) {
        database.printDatabase();
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
            System.out.println("BLOCK: "+message);
            processBlock((BlockMessage) message);
        }

    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    private void processRegister(RegisterMessage message){
        //check if the username already registered - if yes return an error
        if(database.isRegistered(message.getUsername())){
            connections.send(database.getUserID(connectionHandler), new ERRORMessage((short)1));
        }
        else{
            database.register(message, connectionHandler);
            connections.send(database.getUserID(connectionHandler), new ACKMessage((short)1,null));
        }

    }

    private void processLogin(LoginMessage message){
        //check if already logged in
//        System.out.println("___________________________________________________________");
//        System.out.println("FIRST: "+!database.isRegistered(message.getUsername(), message.getPassword())
//        +"\nSEC: "+ database.isLogedIn(message.getUsername()));
//        System.out.println("THE TABLE : " + database.getRegisteredUsers());
//        System.out.println("___________________________________________________________");
        if(     !database.isRegistered(message.getUsername(), message.getPassword())
                || database.isLogedIn(message.getUsername())
                || message.getCaptcha() == (byte)0){
            connections.send(database.getUserID(connectionHandler), new ERRORMessage((short)2));
        }
        else{
            database.login(message, idOfSender);
            connections.send(idOfSender, new ACKMessage((short)2,null));
        }
    }


    private void processLogout(){
        if(!database.thereIsSomeOneHere() || !database.isLogedIn(database.getUserID(connectionHandler))){
            connections.send(idOfSender, new ERRORMessage((short)3));
            database.printDatabase();
        }
        else{ //need to send the server to connect, client leaves after getting an ACK message
            connections.send(idOfSender , new ACKMessage((short)3,null));
            shouldTerminate = true;             //////////////////////////////TODO: is good? /////////////////////
            database.logout(idOfSender);
        }
    }     //V
    private void processFollow(FollowMessage message){
        //if follow failed / !logged in ERROR message
        if(!database.isLogedIn(idOfSender)){
            connections.send(idOfSender, new ERRORMessage((short)4));
        }
        else{
            switch (message.getCommand()){
                //follow
                case (0):
                    if(!database.isFollowing(idOfSender,message.getUsername())){
                        database.follow(message,idOfSender);
                        connections.send(idOfSender, new ACKMessage((short)4, message.getUsername()));
                    }
                    else{
                        connections.send(idOfSender, new ERRORMessage((short)4));
                    }
                    break;
                //unfollow
                case (1):
                    if(database.isFollowing(idOfSender,message.getUsername())){
                        database.unfollow(idOfSender,message.getUsername());
                        connections.send(idOfSender, new ACKMessage((short)4,message.getUsername()));
                    }
                    else{
                        connections.send((idOfSender), new ERRORMessage((short)4));
                    }
                    break;
            }
        }

    }  //V
    private void processPost(PostMessage message){
//        System.out.println("GOT THE POST MESSAGE"); ///////////////////////////////////////////////
        //check if the sender of this message is logged in
        if(!database.isLogedIn(idOfSender)){
            connections.send(idOfSender, new ERRORMessage((short)5));
        }
        else{ // the sender is registered and logged in
            Vector<String> sendto = extractNames(message.getContent());
            if(!sendto.isEmpty()){ // there are users to inform - @users or followers
                                    //go through the vec and send the message to all users that need to get it
                // add message to DATABASE
                database.addMessage(message,database.getUserByIDIFLOGGEDIN(idOfSender));
                for(String usernames : sendto){
                    if(database.isLogedIn(usernames)) {
                        int idOfUser = database.getUserIDIFLOGGEDIN(usernames);
                        // send notification
                        connections.send(idOfUser, new NotificationMessage((byte) 1, database.getUserByIDIFLOGGEDIN(idOfSender).getUserName(), message.getContent()));
                    }
                    else{ // the receiver was not logged in
                        database.addMessageToLoggedOUT(message,database.getUserByNAME(usernames));
                    }
                    connections.send(idOfSender, new ACKMessage((short)5,null));    // send ACK
                }
            }

            else{ // there are no users to inform
                database.addMessage(message,database.getUserByIDIFLOGGEDIN(idOfSender));                                // add message to DATABASE
                connections.send(idOfSender, new ACKMessage((short)5,null));    // send ACK
            }

        }
    }    //V
    private void processPM(PMMessage message) {
        if (!database.isLogedIn(idOfSender) || !database.isFollowing(idOfSender,message.getUsername()) || database.isBlocked(idOfSender, message.getUsername())) {
            connections.send(idOfSender, new ERRORMessage((short) 6));
        } else { // the sender is logged in
            String filteredContent = filterContent(message.getContent());
            PMMessage filtered = new PMMessage(message.getUsername(),filteredContent, message.getDateAndTime());
            User recipientUser = database.getUserByNAME(message.getUsername());
                // add FILTERED message to DATABASE
                database.addMessage(filtered, database.getUserByIDIFLOGGEDIN(idOfSender));
                if(database.isLogedIn(recipientUser.getUserName())) { // receiver is logged in
                    // sending notification
                    System.out.println("FILTERED: "+filtered.getContent());
                    connections.send(database.getUserIDIFLOGGEDIN(message.getUsername()), new NotificationMessage((byte) 0, database.getUserByIDIFLOGGEDIN(idOfSender).getUserName(), filtered.getContent()));
                }
                else{ // the receiver was not logged in
                    System.out.println("\nENTERED ADD MESSGAE TO LOGOUT!!!!\n");
                    database.addMessageToLoggedOUT(filtered,database.getUserByNAME(message.getUsername()));
                }
                // sending ACK
                connections.send(idOfSender, new ACKMessage((short)6,null));
        }
    }  //V

    /**
     * A LOGSTAT message is used to recieve data on a logged in users
     * ---(the age of every user ,
     * number of posts every user posted,
     * number of every user???s followers,
     * number of users the user is following)--- NOT INCLUDED BLOCKED USERS.
     * In order to send a LOGSTAT message the user must be logged in, otherwise an ERROR message will
     * be returned to the client.
     * If user is not registered, an error message will be returned.
     *
     *
     * @param message
     */
    private void processLogstat(LogstatMessage message){
        if(!database.isLogedIn(idOfSender)){
            connections.send(idOfSender, new ERRORMessage((short) 7));
        }
        else{ // sender is logged in and registered
            Vector<User> toFilter = database.whoIsLoggedIN();
            //go through the vec and filter the blocked users
            //get the age, num of posts, num of followers, num of user i follow
            boolean someoneHereISnotREGISTERED = false;
            // check if someone is not registered as mentioned in
            // https://moodle2.bgu.ac.il/moodle/mod/forum/discuss.php?d=549972
            for(User toExtract : toFilter){
                if(!database.isRegistered(toExtract.getUserName())){
                    connections.send(idOfSender, new ERRORMessage((short) 7));
                    someoneHereISnotREGISTERED = true;
                }
            }
            if(!someoneHereISnotREGISTERED) {
                for (User toExtract : toFilter) {
                    if (!database.isBlocked(idOfSender, toExtract.getUserName())
                            && database.isLogedIn(toExtract.getUserName())) {
                        int ageOfUser = database.calculateAge(LocalDate.of(toExtract.getIntYEAR(), toExtract.getIntMONTH(), toExtract.getIntDAY()), LocalDate.now());
                        int numOfPosts = database.numOfPosts(toExtract);
                        int numOfFollowers = database.numOfFollowers(toExtract);
                        int numOfPeopleIFollow = database.numOfPeopleIFollow(toExtract);
                        String ageOfUserSTRING = String.valueOf(ageOfUser);
                        String numOfPostsSTRING = String.valueOf(numOfPosts);
                        String numOfFollowersSTRING = String.valueOf(numOfFollowers);
                        String numOfPeopleIFollowSTRING = String.valueOf(numOfPeopleIFollow);
                        //send ACK
                        connections.send(idOfSender, new ACKMessage((short) 7, ageOfUserSTRING + " " +
                                numOfPostsSTRING + " " + numOfFollowersSTRING + " " + numOfPeopleIFollowSTRING));
                    }
                }

            }
        }
    }  //VV

    /**
     * A STAT message is used to recieve data on a certain users (the age of every user , number of posts
     * every user posted, number of every user???s followers, number of users the user is following).
     * The ???List of usernames??? parameter are a sequence of bytes in UTF-8 terminated by a zero byte,
     * With the following format ??? usernam1|username2|username3|?????? for simplicity, you can assume
     * that ???username??? doesn???t contain the symbol ???|???.
     * In order to send a STAT message the user must be logged in, otherwise an ERROR message will be
     * returned to the client.
     * If user is not registered, an error message will be returned.
     * The returned ACK message will contain (for every single username) user???s age, number of posts a
     * user posted (not including PM???s), number of followers, number of users the user is following in the
     * optional section of the ACK message.
     * Example:
     * ACK-Opcode STAT-Opcode <Age><NumPosts> <NumFollowers> <NumFollowing>
     * @param message
     */
    private void processStat(StatMessage message){
        System.out.println("USERS: "+message.getUsernames().toString());        ////////////////////////////
        if(!database.isLogedIn(idOfSender)){
            connections.send(idOfSender, new ERRORMessage((short)8));
        }
        else { // the sender is registered and logged in
            Vector<User> toFilter = userForSTAT(message);
            boolean someoneHereISnotREGISTERED = false;
            // check if someone is not registered as mentioned in
            // https://moodle2.bgu.ac.il/moodle/mod/forum/discuss.php?d=549972
            if (!toFilter.isEmpty()) {
                for (User user : toFilter) {
                    if(user.getUserName().compareTo(" ") == 0){
                        connections.send(idOfSender, new ERRORMessage((short) 8));
                        someoneHereISnotREGISTERED = true;
                    }
                }
                if (!someoneHereISnotREGISTERED) {
                    for (User toExtract : toFilter) {
                        // conditions: the user from the list is - NOT blocked
                        //                                         if not registered - send an ERROR message
                        if (!database.isBlocked(idOfSender, toExtract.getUserName())) {
                            int ageOfUser = database.calculateAge(LocalDate.of(toExtract.getIntYEAR(), toExtract.getIntMONTH(), toExtract.getIntDAY()), LocalDate.now());
                            int numOfPosts = database.numOfPosts(toExtract);
                            int numOfFollowers = database.numOfFollowers(toExtract);
                            int numOfPeopleIFollow = database.numOfPeopleIFollow(toExtract);
                            String ageOfUserSTRING = String.valueOf(ageOfUser);
                            String numOfPostsSTRING = String.valueOf(numOfPosts);
                            String numOfFollowersSTRING = String.valueOf(numOfFollowers);
                            String numOfPeopleIFollowSTRING = String.valueOf(numOfPeopleIFollow);
                            //send ACK
                            connections.send(idOfSender, new ACKMessage((short) 8, ageOfUserSTRING + " " +
                                    numOfPostsSTRING + " " + numOfFollowersSTRING + " " + numOfPeopleIFollowSTRING));
                        }
                        else{
                            connections.send(idOfSender, new ACKMessage((short) 8,""));
                        }
                    }
                }
            }
            else{//no name were written
                connections.send(idOfSender, new ACKMessage((short) 8,""));
            }
        }
    }     //VV

    /**
     * This message will be sent from the server for any PM sent to the user,
     *  post sent by someone the user is following, or a post that
     * contained @<MyUsername> in the content of a message.
     * A user will recive any POST/PM notification sent after follow (for users the current user is following)
     * that he didn???t see. I.e. wasn???t logged in when the other user posted/sent the message. (Clue: for
     * each user, save timestamp of last message recieved from each of the other users / timestamp of the
     * follow command)
     * @param message
     */


    /**
     * Message send to block a specific user.
     * Blocked user can???t follow, PM, or show any information about the user who blocked him.
     * Once user blocking acknowledged, both users (the blocking and the blocked) stop following each
     * other.
     * If ???username??? doesn???t exist, and ERROR message will be sent back.
     * @param message
     */
    private void processBlock(BlockMessage message){
        if(!database.isRegistered(message.getUsername()) || !database.isLogedIn(idOfSender)
        || database.isBlocked(idOfSender,message.getUsername()) ){
            connections.send(idOfSender, new ERRORMessage((short)12));
        }
        else{ //username exists
            // UNFOLLOW each other
            User Blocker = database.getUserByIDIFLOGGEDIN(idOfSender);
            if(database.isFollowing(idOfSender, message.getUsername())){
                database.unfollow(idOfSender, message.getUsername());
            }
            if(database.isFollowing(message.getUsername(),Blocker.getUserName())) {
                database.unfollow(message.getUsername(), Blocker.getUserName());
            }
            // add to the DB of the blocker
            database.block(idOfSender, message.getUsername());
            // send ACK
            connections.send(idOfSender, new ACKMessage((short)12,null));
        }

    }

    private Vector<String> extractNames(String content){
        // extracting the users that mentioned in message and who follows me
        // should prevent double appirance of users
        // suppose to prevent blocking members to get the message

//        System.out.println("GOT TO THE FUNCTION");
//        System.out.println("CONTANT IS: "+ content);
        Vector<String> output1 = new Vector<>();
        // extract names from message
        int i = 0;
        String acc = "";
        boolean empty = content.length() == 0;
        System.out.println("empty? " + empty);
        while(i < content.length() & !empty){
            if(content.charAt(i) == '@'){
                while(i < content.length() && content.charAt(i) != ' ' && content.charAt(i) != ',' && content.charAt(i) != '!' && content.charAt(i) != '.'){
                    if(content.charAt(i) != '@')
                        acc += content.charAt(i);
                    i++;
                }
                if (!database.isBlocked(acc , idOfSender)) {
                    output1.add(acc);
                }
                acc = "";
            }
            //trail
            i++;
        }
        // extract who follows me
        ConcurrentHashMap<User, Vector<User>> following = database.getFollowing();
        Vector<String> output2 = new Vector<>();
        User sender = database.getUserByIDIFLOGGEDIN(idOfSender);
        for(Map.Entry<User, Vector<User>> pair : following.entrySet()){
//            if(user.getKey().getUserName().compareTo(sender.getUserName()) == 0) {
                for (User toFind: pair.getValue()){
                    if (toFind.equals(sender)){
                        output2.add(pair.getKey().getUserName());
                    }
//                }
            }
        }
        System.out.println("VECTOR OF FOLLOWERS: "+output2);        /////////////////////////////////////////////
        output1.addAll(output2);
        LinkedHashSet<String> convert = new LinkedHashSet<>(output1);
        output1.clear();
        output1.addAll(convert);
        System.out.println("FINISHED VECTOR OF USERS: "+output1);       ///////////////////////////////////////
        return output1;
    }
    public String filterContent(String unfiltered){
        Vector<String> vec = connections.getForbiddenWords();
        for (String badword : vec){
            unfiltered = unfiltered.replaceAll(badword , "<filtered>");
        }
        return unfiltered;
    }
    private Vector<User> userForSTAT(StatMessage message){
        Vector<User> output = new Vector<>();
        List<String> toProcess = message.getUsernames();
        for(String name : toProcess){
            if(database.isRegistered(name)) {
                output.add(database.getUserByNAME(name));
            }
            else{
                output.add(new User(" " , " " , "03.11.1993"));
                break;
            }
        }
        return output;
    } //VVV
}
