package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.Messages.Message;
import bgu.spl.net.srv.Messages.clientToServer.*;
import jdk.internal.util.xml.impl.Pair;

import java.util.*;
import java.util.function.Supplier;


public class ConnectionsImpl<T> implements Connections {
    private Server<T> server;
    private Vector<ConnectionHandler<T>> connectionsHandlerVector;  //////////// have to be updated, will indicate the loggedin status
    private static ConnectionsImpl instance = new ConnectionsImpl();
    private static int connectionIdCounter = 0;
    private volatile HashMap<Integer,ConnectionHandler<T>> connectionIDS;


    public HashMap<Integer, ConnectionHandler<T>> getConnectionIDS() {
        return connectionIDS;
    }

    private ConnectionsImpl() {
        this.server = null;
        this.connectionsHandlerVector = new Vector<>();
        this.connectionIDS = new HashMap<>();
    }
    public void setServer(Server<T> server){
        this.server = server;
        //TODO::: call setServer
    }
    public static ConnectionsImpl getInstance(){
        return instance;
    }

    @Override
    public boolean send(int connectionId, Object msg) {
        ConnectionHandler<T> toSend =  connectionIDS.get(connectionId);
        toSend.send((T)msg);
        return true;
    }

    @Override
    public void broadcast(Object msg) {
        for(ConnectionHandler<T> user: connectionsHandlerVector){
            user.send((T)msg);
        }

    }

    @Override
    public void disconnect(int connectionId) {
        if(connectionIDS.containsKey(connectionId)){
            ConnectionHandler<T> toDisconnect = connectionIDS.get(connectionId);
            if(connectionsHandlerVector.contains(toDisconnect)) {
                connectionsHandlerVector.remove(toDisconnect);
                connectionIDS.remove(toDisconnect);
            }
        }
    }

    public void addHandler(ConnectionHandler<T> handler){
        connectionsHandlerVector.add(handler);
        connectionIDS.put(connectionIdCounter++, handler);
    }

//    public Integer getUserID(String username){
//        Integer id = -1;
//        for(Map.Entry<Integer, User> user : registeredUsers.entrySet()){
//            if(user.getValue().getUserName().equals(username)) {
//                id = user.getKey();
//            }
//        }
//        return id;
//    }
//    public Integer getUserID(ConnectionHandler handler){
//        Integer id = -1;
//        for(Map.Entry<Integer, ConnectionHandler<T>> CH : connectionIDS.entrySet()){
//            if(CH.getValue().equals(handler)) {
//                id = CH.getKey();
//            }
//        }
//        return id;
//    }
//    public String getRegisteredUserName(int idOfUser){
//        String name = "";
//        for(Map.Entry<Integer, User> userEntry : registeredUsers.entrySet()){
//            if(userEntry.getValue().equals(idOfUser)) {
//                name = userEntry.getValue().getUserName();
//            }
//        }
//        return name;
//    }
//    public void register(RegisterMessage message, ConnectionHandler<T> handler){
//        Integer id = -1;
//        for(Map.Entry<Integer, ConnectionHandler<T>> CH : connectionIDS.entrySet()){
//            if(CH.getValue().equals(handler)) {
//                id = CH.getKey();
//            }
//        }
//        User toRegister = new User(message.getUsername(), message.getPassword(), message.getBirthday());
//        registeredUsers.put(id,toRegister);
//    }
//
//    public void login(LoginMessage message){
//        Integer id = -1;
//        User toLogIn = null;
//        for(Map.Entry<Integer, User> user : registeredUsers.entrySet()){
//            if(user.getValue().getUserName().equals(message.getUsername())) {
//                id = user.getKey();
//                toLogIn = user.getValue();
//            }
//        }
//        loggedInUsers.put(id,toLogIn);
//
//    }
//
//    public boolean thereIsSomeOneHere(){
//        return !loggedInUsers.isEmpty();
//    }
//
//    public void logout(){
//        Integer id = -1;
//        User toLogIn = null;
//        for(Map.Entry<Integer, User> user : loggedInUsers.entrySet()){
//                id = user.getKey();
//                toLogIn = user.getValue();
//        }
//        loggedInUsers.remove(id,toLogIn);
//    }
//
//
//    public boolean isLogedIn(String userName){
//            //go through the active users DB and search for the specific user
//            for(User exists : loggedInUsers.values()){
//                if (exists.getUserName().equals(userName))
//                    return true;
//            }
//        return false;
//    }
//
//    public boolean isRegistered(String userName){
//        //go through the active users DB and search for the specific user
//        for(User exists : registeredUsers.values()){
//            if (exists.getUserName().equals(userName))
//                return true;
//        }
//        return false;
//    }
//    public boolean isRegistered(int id){
//        //go through the active users DB and search for the specific user
//        for(Integer curr_id : registeredUsers.keySet()){
//            if (curr_id.equals(id))
//                return true;
//        }
//        return false;
//    }
//    public boolean follow(FollowMessage message,Integer idOfSender){
//        //sender wants to FOLLOW message.getname
//        User sender = registeredUsers.get(idOfSender);
//        LinkedList<User> followingList = new LinkedList<>();
//        for(Map.Entry<User, LinkedList<User>> user : following.entrySet()){
//            if(user.getKey().getUserName().equals(message.getUsername())) {
//                user.getValue().add(sender);
//                followingList = user.getValue();
//            }
//        }
//        return followingList.contains(sender);
//    }
//    public boolean unfollow(FollowMessage message, Integer idOfSender){
//        //sender wants to UNFOLLOW message.getname
//        User sender = registeredUsers.get(idOfSender);
//        LinkedList<User> followingList = new LinkedList<>();
//        for(Map.Entry<User, LinkedList<User>> user : following.entrySet()){
//            if(user.getKey().getUserName().equals(message.getUsername())) {
//                user.getValue().remove(sender);
//                followingList = user.getValue();
//            }
//        }
//        return !followingList.contains(sender);
//    }
//    public boolean isFollowing(FollowMessage message, Integer idOfSender){
//        User sender = registeredUsers.get(idOfSender);
//        for(Map.Entry<User, LinkedList<User>> user : following.entrySet()){
//            if(user.getKey().getUserName().equals(message.getUsername())) {
//                for (User toFind: user.getValue()){
//                    if (toFind.equals(sender)){
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }

}
