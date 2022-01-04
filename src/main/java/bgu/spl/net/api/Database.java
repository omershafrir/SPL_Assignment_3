package bgu.spl.net.api;

import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.srv.Messages.Message;
import bgu.spl.net.srv.Messages.clientToServer.FollowMessage;
import bgu.spl.net.srv.Messages.clientToServer.LoginMessage;
import bgu.spl.net.srv.Messages.clientToServer.PostMessage;
import bgu.spl.net.srv.Messages.clientToServer.RegisterMessage;
import bgu.spl.net.srv.User;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

public class Database {
    // fields:
    private static Database instance = new Database();
    private static ConnectionsImpl connections = ConnectionsImpl.getInstance();
    private HashMap<Integer, User> loggedInUsers;   ////////////holds the logged in members
    private HashMap<Integer, User> registeredUsers;
    private HashMap<User, LinkedList<User>> following;
    private HashMap<User, Vector<Message>> postAndPMdataBase;
    private HashMap<Integer,ConnectionHandler<Message>> connectionIDS;
    // constructor:
    private Database(){
        loggedInUsers = new HashMap<>();
        registeredUsers = new HashMap<>();
        following = new HashMap<>();
        postAndPMdataBase = new HashMap<>();
        connectionIDS = connections.getConnectionIDS();
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
            if(user.getValue().getUserName().equals(username)) {
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
        for(Map.Entry<Integer, User> userEntry : registeredUsers.entrySet()){
            if(userEntry.getValue().equals(idOfUser)) {
                name = userEntry.getValue().getUserName();
            }
        }
        return name;
    }
    public boolean isLogedIn(String userName){
        //go through the active users DB and search for the specific user
        for(User exists : loggedInUsers.values()){
            if (exists.getUserName().equals(userName))
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
    public boolean isRegistered(String userName){
        //go through the active users DB and search for the specific user
        for(User exists : registeredUsers.values()){
            if (exists.getUserName().equals(userName))
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
    public void register(RegisterMessage message, ConnectionHandler<Message> handler){
        Integer id = -1;
        for(Map.Entry<Integer, ConnectionHandler<Message>> CH : connectionIDS.entrySet()){
            if(CH.getValue().equals(handler)) {
                id = CH.getKey();
            }
        }
        User toRegister = new User(message.getUsername(), message.getPassword(), message.getBirthday());
        registeredUsers.put(id,toRegister);
    }
    // Login:
    public void login(LoginMessage message){
        Integer id = -1;
        User toLogIn = null;
        for(Map.Entry<Integer, User> user : loggedInUsers.entrySet()){
            if(user.getValue().getUserName().equals(message.getUsername())) {
                id = user.getKey();
                toLogIn = user.getValue();
            }
        }
        loggedInUsers.put(id,toLogIn);

    }



    public void logout(){
        Integer id = -1;
        User toLogIn = null;
        for(Map.Entry<Integer, User> user : loggedInUsers.entrySet()){
            id = user.getKey();
            toLogIn = user.getValue();
        }
        loggedInUsers.remove(id,toLogIn);
    }


    // Follow / Unfollow:
    public boolean follow(FollowMessage message, Integer idOfSender){
        //sender wants to FOLLOW message.getname
        User sender = registeredUsers.get(idOfSender);
        LinkedList<User> followingList = new LinkedList<>();
        for(Map.Entry<User, LinkedList<User>> user : following.entrySet()){
            if(user.getKey().getUserName().equals(message.getUsername())) {
                user.getValue().add(sender);
                followingList = user.getValue();
            }
        }
        return followingList.contains(sender);
    }
    public boolean unfollow(String username, Integer idOfSender){
        //sender wants to UNFOLLOW message.getname
        User sender = registeredUsers.get(idOfSender);
        LinkedList<User> followingList = new LinkedList<>();
        for(Map.Entry<User, LinkedList<User>> user : following.entrySet()){
            if(user.getKey().getUserName().equals(username)) {
                user.getValue().remove(sender);
                followingList = user.getValue();
            }
        }
        return !followingList.contains(sender);
    }


    public boolean isFollowing(String username, Integer idOfSender){
        User sender = registeredUsers.get(idOfSender);
        for(Map.Entry<User, LinkedList<User>> user : following.entrySet()){
            if(user.getKey().getUserName().equals(username)) {
                for (User toFind: user.getValue()){
                    if (toFind.equals(sender)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // PM / Post:
    public void addMessage(Message message , int connectionId){
        Vector<Message> toUpdate = postAndPMdataBase.get(loggedInUsers.get(connectionId));
        toUpdate.add(message);
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
        for(Map.Entry<User, LinkedList<User>> userToMessage : following.entrySet()){
            if(userToMessage.getKey().equals(user)){
                LinkedList<User> tocheck = userToMessage.getValue();
                for (User u : tocheck){
                    output++;
                }
                //if you found the User and calculated everything toy can take a break =]
                break;
            }
        }
        return output;
    }
    public int numOfPeopleIFollow(User user){
        int output = 0;
        for(Map.Entry<User, LinkedList<User>> userToMessage : following.entrySet()){
                // get the followers vec
                LinkedList<User> tocheck = userToMessage.getValue();
                for (User u : tocheck){
                    // for each USER see if it is me - that means that i follow someone
                    if(u.equals(user))
                        output++;
                }
        }
        return output;
    }

    // Blocked
    public boolean isBlocked(int idOfBlocker, String Blocked){
        //TODO : the blocked users SHOULD NOT be on the following list of the Blocker
        return true;
    }


}
