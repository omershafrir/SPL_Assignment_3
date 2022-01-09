package bgu.spl.net.api;

import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.srv.Messages.Message;
import bgu.spl.net.srv.Messages.clientToServer.*;
import bgu.spl.net.srv.Messages.serverToClient.NotificationMessage;
import bgu.spl.net.srv.User;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class Database {
    // fields:
    private static Database instance = new Database();
    private static ConnectionsImpl connections;
    private ConcurrentHashMap<Integer, User> loggedInUsers;   ////////////holds the logged in members
    private ConcurrentHashMap<Integer, User> registeredUsers;
    private ConcurrentHashMap<User, Vector<User>> following;
    private ConcurrentHashMap<User, Vector<String>> blockerToBlocked;
    private ConcurrentHashMap<User, Vector<Message>> postAndPMdataBase;
    private ConcurrentHashMap<Integer,ConnectionHandler<Message>> connectionIDS;
    private ConcurrentHashMap<User, Vector<Message>> loggedOutMessageHolder;
    // constructor:
    private Database(){
        loggedInUsers = new ConcurrentHashMap<>();
        registeredUsers = new ConcurrentHashMap<>();
        following = new ConcurrentHashMap<>();
        postAndPMdataBase = new ConcurrentHashMap<>();
        connections = ConnectionsImpl.getInstance();
        connectionIDS = connections.getConnectionIDS();
        blockerToBlocked = new ConcurrentHashMap<>();
        loggedOutMessageHolder = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<User, Vector<User>> getFollowing() {
        return following;
    }

    public static Database getInstance(){
        return instance;
    }


    // generic:
    public boolean doesExist(int id){
        return connectionIDS.containsKey(id);
    }
    public Integer getUserID(String username){
        Integer id = -1;
        for(Map.Entry<Integer, User> user : registeredUsers.entrySet()){
            if(user.getValue().getUserName().compareTo(username) == 0) {
                id = user.getKey();
            }
        }
        return id;
    }

    public Integer getUserID(ConnectionHandler handler){
        Integer id = -1;
        for(Map.Entry<Integer, ConnectionHandler<Message>> CH : connectionIDS.entrySet()){
            if(CH.getValue().equals(handler)) {
                id = CH.getKey();
            }
        }
        return id;
    }
    public String getRegisteredUserName(int idOfUser){
        String name = "";
        name = registeredUsers.get(idOfUser).getUserName();
        return name;
    }
    public User getUserByNAME(String name){
        return getUserByID(getUserID(name));
    }
    public boolean isLogedIn(String userName){
        //go through the active users DB and search for the specific user
        for(User exists : loggedInUsers.values()){
            if (exists.getUserName().compareTo(userName) == 0)
                return true;
        }
        return false;
    }
    public boolean isLogedIn(int id){
        //go through the active users DB and search for the specific user
        for(Integer curr_id : loggedInUsers.keySet()){
            if (curr_id.equals(id))
                return true;
        }
        return false;
    }
    public boolean isRegistered(String username){
        //go through the active users DB and search for the specific user
        for(User exists : registeredUsers.values()){
            if (exists.getUserName().compareTo(username) == 0)
                return true;
        }
        return false;
    }
    public boolean isRegistered(String username,String password){
        //go through the active users DB and search for the specific user
        for(User exists : registeredUsers.values()){
            if (exists.getUserName().compareTo(username) == 0 && exists.getPassword().compareTo(password) == 0)
                return true;
        }
        return false;
    }
    public boolean isRegistered(int id){
        //go through the active users DB and search for the specific user
        for(Integer curr_id : registeredUsers.keySet()){
            if (curr_id.equals(id))
                return true;
        }
        return false;
    }
    public boolean thereIsSomeOneHere(){
        return !loggedInUsers.isEmpty();
    }
    public User getUserByID(int ID){
        if(!registeredUsers.containsKey(ID))
            return null;
        return registeredUsers.get(ID);
    }

    // Register:
    public int register(RegisterMessage message, ConnectionHandler<Message> handler){
        Integer id = -1;
        for(Map.Entry<Integer, ConnectionHandler<Message>> CH : connectionIDS.entrySet()){
            if(CH.getValue().equals(handler)) {
                id = CH.getKey();
            }
        }
        User toRegister = new User(message.getUsername(), message.getPassword(), message.getBirthday());
        registeredUsers.put(id,toRegister);
        return id;
    }

    public ConcurrentHashMap<Integer, User> getRegisteredUsers() {
        return registeredUsers;
    }

    // Login:
    public void login(LoginMessage message , int idOfSender){
        Integer id = -1;
        User toLogIn = null;
        for(Map.Entry<Integer, User> user : registeredUsers.entrySet()){
            if(user.getValue().getUserName().compareTo(message.getUsername()) == 0
                    && user.getValue().getPassword().compareTo(message.getPassword()) == 0) {

                id = user.getKey();
                toLogIn = user.getValue();
            }
        }
        if(id != -1) {

            registeredUsers.remove(id, toLogIn);
            registeredUsers.put(idOfSender, toLogIn);
            loggedInUsers.put(idOfSender, toLogIn);
            // send the user that logs in all the messages that have been waiting for him
            if(loggedOutMessageHolder.get(toLogIn) != null){
                //if he has messages, go through them and send each one
                //depending on the instance
                System.out.println("\nLOGOUT MESSAGE HOLDER: "+loggedOutMessageHolder+"\n"); /////////////////////////////////////////////
                for (Message m : loggedOutMessageHolder.get(toLogIn)){
                    if(m instanceof PMMessage)
                        connections.send(id,new NotificationMessage((byte)0,((PMMessage) m).getUsername(),((PMMessage) m).getContent()));
                    else{
                        //need to find who posted it
                        for(Map.Entry<User, Vector<Message>> user : postAndPMdataBase.entrySet()){
                            Vector<Message> toCheck = user.getValue();
                            if(toCheck.indexOf(m) != -1){
                                connections.send(id,new NotificationMessage((byte)1,user.getKey().getUserName(),((PostMessage) m).getContent()));
                                break;
                            }
                        }

                    }
                }
                loggedOutMessageHolder.get(toLogIn).clear();
                loggedOutMessageHolder.remove(toLogIn);
            }
        }

    }

    // Logout:
    public void logout(int idOfSender){
        Integer id = -1;
        User toLogOut = null;
        // remove ID -> CH
        connectionIDS.remove(idOfSender);
        // remove loggedIn: ID -> User
        for(Map.Entry<Integer, User> user : loggedInUsers.entrySet()){
            if(user.getKey().equals(idOfSender)) {
                id = user.getKey();
                toLogOut = user.getValue();
            }
        }
        loggedInUsers.remove(id,toLogOut);
    }


    // Follow / Unfollow:
    public void follow(FollowMessage message, Integer idOfSender){
        //sender wants to FOLLOW message.getname
        User sender = loggedInUsers.get(idOfSender);
        //if this is the first follow command
        if(following.get(sender) == null){
            Vector<User> followingList = new Vector<User>();
            following.put(sender, followingList);
        }
        Vector<User> vecOfUsers = following.get(sender);
        vecOfUsers.add(getUserByNAME(message.getUsername()));
    }

    public void unfollow(Integer idOfSender , String username){
        // sender wants to UNFOLLOW message.getname

        // need to use registered list because
        // the blocked wouldn't have to be logged in
        User sender = registeredUsers.get(idOfSender);
        User unfollowed = getUserByNAME(username);
        Vector<User> vecOfUsers = following.get(sender);
        // check if this is the last user im following
        // if so, check if this is the guy i want to unfollow
        if(vecOfUsers.size() == 1 && vecOfUsers.indexOf(username) != -1) {
            following.remove(sender);
        }
        else{
            vecOfUsers.remove(unfollowed);
        }
    }
    public boolean isFollowing(Integer idOfSender,String username){
        User sender = loggedInUsers.get(idOfSender);
        User followed = getUserByNAME(username);
        if(following.get(sender) == null){
            return false;
        }
        return following.get(sender).indexOf(followed) != -1;
    }

    // PM / Post:
    public void addMessage(Message message , int connectionId){
        if(postAndPMdataBase.get(getUserByID(connectionId)) == null){
            Vector<Message> toUpdate = new Vector<>();
            toUpdate.add(message);
            postAndPMdataBase.put(getUserByID(connectionId), toUpdate);
        }
        else{
            Vector<Message> toUpdate = postAndPMdataBase.get(getUserByID(connectionId));
            toUpdate.add(message);
        }

    }
    public void addMessageToLoggedOUT(Message message , int connectionId){
        if(loggedOutMessageHolder.get(getUserByID(connectionId)) == null){
            System.out.println("\nCREATED A NEW VECTOR!\n");            ///////////////////////////////////////////////////////
            Vector<Message> toUpdate = new Vector<>();
            toUpdate.add(message);
            loggedOutMessageHolder.put(getUserByID(connectionId), toUpdate);
        }
        else{
            System.out.println("\ndidnt created A NEW VECTOR!\n");            ///////////////////////////////////////////////////////
            Vector<Message> toUpdate = loggedOutMessageHolder.get(registeredUsers.get(connectionId));
            toUpdate.add(message);
        }

    }

    // Logstat:
    public Vector<User> whoIsLoggedIN(){
        Vector<User> output = new Vector<>();
        for(Map.Entry<Integer, User> user : loggedInUsers.entrySet()){
            output.add(user.getValue());
        }
        return output;
    }

    public int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;
        }
    }
    public int numOfPosts(User user){
        int output = 0;
        for(Map.Entry<User, Vector<Message>> userToMessage : postAndPMdataBase.entrySet()){
            if(userToMessage.getKey().equals(user)){
                Vector<Message> tocheck = userToMessage.getValue();
                for (Message m : tocheck){
                    if(m instanceof PostMessage)
                        output++;
                }
                //if you found the User and calculated everything toy can take a break =]
                break;
            }
        }
        return output;
    }
    public int numOfFollowers(User user){
        int output = 0;
        for(Map.Entry<User, Vector<User>> userToMessage : following.entrySet()){
            if(userToMessage.getKey().equals(user)){
                Vector<User> tocheck = userToMessage.getValue();
                output = tocheck.size();
                //if you found the User and calculated everything toy can take a break =]
                break;
            }
        }
        return output;
    }
    public int numOfPeopleIFollow(User user){
        int output = 0;
        for(Map.Entry<User, Vector<User>> userToMessage : following.entrySet()){
                // get the followers vec
                Vector<User> tocheck = userToMessage.getValue();
                for (User u : tocheck){
                    // for each USER see if it is me - that means that i follow someone
                    if(u.equals(user))
                        output++;
                }
        }
        return output;
    }

    // Blocked:
    public boolean isBlocked(int idOfBlocker, String Blocked){
        User Blocker = getUserByID(idOfBlocker);
        //if the blocker dont have a list of blocked members return false
        if(blockerToBlocked.get(Blocker) == null)
            return false;
        //if he has a list - check if the BLOCKED user is there
        return blockerToBlocked.get(Blocker).contains(Blocked);
//        return true;
    }
    public void block(int idOfBlocker, String Blocked){
        User Blocker = getUserByID(idOfBlocker);
        // The first block done for this user
        if(blockerToBlocked.get(Blocker) == null){
            Vector<String> blockedVec = new Vector<String>();
            blockedVec.add(Blocked);
            blockerToBlocked.put(Blocker,blockedVec);
        }
        else{  //the second and on
            Vector<String> blockedVec = blockerToBlocked.get(Blocker);
            blockedVec.add(Blocked);
        }
    }


    public void printDatabase(){
        System.out.println();

        System.out.println("connectionIDS MAP: ");
        System.out.println(connectionIDS.toString());

        System.out.println("loggedInUsers MAP: ");
        System.out.println(loggedInUsers.toString());

        System.out.println("registeredUsers MAP: ");
        System.out.println(registeredUsers.toString());

        System.out.println();
    }



}
