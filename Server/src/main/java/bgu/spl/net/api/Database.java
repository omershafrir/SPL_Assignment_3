package bgu.spl.net.api;

import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.srv.Messages.Message;
import bgu.spl.net.srv.Messages.clientToServer.*;
import bgu.spl.net.srv.Messages.serverToClient.NotificationMessage;
import bgu.spl.net.srv.User;

import java.time.LocalDate;
import java.time.Period;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Database {
    // fields:
    private static Database instance = new Database();
    private static ConnectionsImpl connections;
    private ConcurrentHashMap<Integer, User> loggedInUsers;   ////////////holds the logged in members
    private ConcurrentLinkedQueue<User> registeredUsers;
    private ConcurrentHashMap<User, Vector<User>> following;
    private ConcurrentHashMap<User, Vector<String>> blockerToBlocked;
    private ConcurrentHashMap<User, Vector<Message>> postAndPMdataBase;
    private ConcurrentHashMap<Integer,ConnectionHandler<Message>> connectionIDS;
    private ConcurrentHashMap<User, Vector<Message>> loggedOutMessageHolder;
    // constructor:
    private Database(){
        loggedInUsers = new ConcurrentHashMap<>();
        registeredUsers = new ConcurrentLinkedQueue();
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
    public Integer getUserIDIFLOGGEDIN(String username){
        Integer id = -1;
        for(Map.Entry<Integer, User> userPair : loggedInUsers.entrySet()){
            if(userPair.getValue().getUserName().compareTo(username) == 0) {
                id = userPair.getKey();
                break;
            }
        }
        return id;
    }       //VV

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
//        name = registeredUsers.get(idOfUser).getUserName();
        return name;
    }
    public User getUserByNAME(String name){
        for(User u : registeredUsers){
            if(u.getUserName().compareTo(name) == 0){
                return u;
            }
        }
        return null;
    } //V
    public boolean isLogedIn(String userName){
        //go through the active users DB and search for the specific user
        for(User exists : loggedInUsers.values()){
            if (exists.getUserName().compareTo(userName) == 0)
                return true;
        }
        return false;
    }  //VV
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
        for(User exists : registeredUsers){
            if (exists.getUserName().compareTo(username) == 0)
                return true;
        }
        return false;
    }                           //v
    public boolean isRegistered(String username,String password){
        //go through the active users DB and search for the specific user
        for(User exists : registeredUsers){
            if (exists.getUserName().compareTo(username) == 0 && exists.getPassword().compareTo(password) == 0)
                return true;
        }
        return false;
    }           //v
    public boolean thereIsSomeOneHere(){
        return !loggedInUsers.isEmpty();
    } //V
    public User getUserByIDIFLOGGEDIN(int ID){
        if(!loggedInUsers.containsKey(ID))
            return null;
        return loggedInUsers.get(ID);
    } //V

    // Register:
    public int register(RegisterMessage message, ConnectionHandler<Message> handler){
//        for(Map.Entry<Integer, ConnectionHandler<Message>> CH : connectionIDS.entrySet()){
//            if(CH.getValue().equals(handler)) {
//                id = CH.getKey();
//            }
//        }
        User toRegister = new User(message.getUsername(), message.getPassword(), message.getBirthday());
        registeredUsers.add(toRegister);
        return 1;
    }           //V

    // Login:
    public void login(LoginMessage message , int idOfSender){
        User toLogIn = null;
        for(User user : registeredUsers){
            if(user.getUserName().compareTo(message.getUsername()) == 0
                    && user.getPassword().compareTo(message.getPassword()) == 0) {
                toLogIn = user;
            }
        }
            loggedInUsers.put(idOfSender, toLogIn);
            // send the user that logs in all the messages that have been waiting for him
            if(loggedOutMessageHolder.get(toLogIn) != null){
                //if he has messages, go through them and send each one
                //depending on the instance
                System.out.println("\nLOGOUT MESSAGE HOLDER: "+loggedOutMessageHolder+"\n"); /////////////////////////////////////////////
                Vector<Message> toExtract = loggedOutMessageHolder.get(toLogIn);
                synchronized (Database.getInstance()) {
                    for (Message m : loggedOutMessageHolder.get(toLogIn)) {
                        if (m instanceof PMMessage)
                            connections.send(idOfSender, new NotificationMessage((byte) 0, ((PMMessage) m).getUsername(), ((PMMessage) m).getContent()));
                        else {
                            //need to find who posted it
                            for (Map.Entry<User, Vector<Message>> user : postAndPMdataBase.entrySet()) {
                                Vector<Message> toCheck = user.getValue();
                                    if (toCheck.indexOf(m) != -1) {
                                        connections.send(idOfSender, new NotificationMessage((byte) 1, user.getKey().getUserName(), ((PostMessage) m).getContent()));
                                        break;
                                    }
                                }
                        }
                    }
                    loggedOutMessageHolder.get(toLogIn).clear();
                    loggedOutMessageHolder.remove(toLogIn);
                }
        }

    }         //V

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
    } //V


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
    } // V

    public void unfollow(Integer idOfSender , String username){
        // sender wants to UNFOLLOW message.getname

        // need to use registered list because
        // the blocked wouldn't have to be logged in
        User sender = loggedInUsers.get(idOfSender);
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
    } //V
    public void unfollow(String nameOfSender , String username){
        // sender wants to UNFOLLOW message.getname

        // need to use registered list because
        // the blocked wouldn't have to be logged in
        User sender = getUserByNAME(nameOfSender);
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
    } //V
    public boolean isFollowing(Integer idOfSender,String username){
        User sender = loggedInUsers.get(idOfSender);
        User followed = getUserByNAME(username);
        if(following.get(sender) == null){
            return false;
        }
        return following.get(sender).contains(followed);
    } //V
    public boolean isFollowing(String nameOfSender,String username){
        User sender = getUserByNAME(nameOfSender);
        User followed = getUserByNAME(username);
        if(following.get(sender) == null){
            return false;
        }
        return following.get(sender).contains(followed);
    } //V

    // PM / Post:
    public void addMessage(Message message , User user){
        if(postAndPMdataBase.get(user) == null){
            Vector<Message> toUpdate = new Vector<>();
            toUpdate.add(message);
            postAndPMdataBase.put(user, toUpdate);
        }
        else{
            Vector<Message> toUpdate = postAndPMdataBase.get(user);
            toUpdate.add(message);
        }

    } //V
    public void addMessageToLoggedOUT(Message message , User user){
        if(loggedOutMessageHolder.get(user) == null){
            System.out.println("\nCREATED A NEW VECTOR!\n");            ///////////////////////////////////////////////////////
            Vector<Message> toUpdate = new Vector<>();
            toUpdate.add(message);
            loggedOutMessageHolder.put(user, toUpdate);
        }
        else{
            System.out.println("\ndidnt created A NEW VECTOR!\n");            ///////////////////////////////////////////////////////
            Vector<Message> toUpdate = loggedOutMessageHolder.get(user);
            toUpdate.add(message);
        }

    } //V

    // Logstat:
    public Vector<User> whoIsLoggedIN(){
        Vector<User> output = new Vector<>();
        for(Map.Entry<Integer, User> user : loggedInUsers.entrySet()){
            output.add(user.getValue());
        }
        return output;
    }   //VV

    public int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;
        }
    } // V
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
    } //V
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
    } //V
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
    } //V

    // Blocked:
    public boolean isBlocked(int idOfBlocker, String Blocked){
        User Blocker = loggedInUsers.get(idOfBlocker);
        //if the blocker dont have a list of blocked members return false
        if(blockerToBlocked.get(Blocker) == null)
            return false;
        //if he has a list - check if the BLOCKED user is there
        return blockerToBlocked.get(Blocker).contains(Blocked);
//        return true;
    }    /////VVVV

    public boolean isBlocked(String nameOfBlocker, int Blocked){
        User BlockedUser = loggedInUsers.get(Blocked);
        for(Map.Entry<User, Vector<String>> blockerToblocked : blockerToBlocked.entrySet()){
            if(blockerToblocked.getKey().getUserName().compareTo(nameOfBlocker) == 0){
                if(blockerToblocked.getValue() != null && blockerToblocked.getValue().contains(BlockedUser.getUserName())){
                    return true;
                }
            }
        }
        return false;
    }    /////VVVV
    public void block(int idOfBlocker, String Blocked){
        User Blocker = getUserByIDIFLOGGEDIN(idOfBlocker);
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
    } //VV


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
