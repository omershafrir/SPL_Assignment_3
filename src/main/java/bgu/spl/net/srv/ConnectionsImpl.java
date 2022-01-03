package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.Messages.clientToServer.FollowMessage;
import bgu.spl.net.srv.Messages.clientToServer.LoginMessage;
import bgu.spl.net.srv.Messages.clientToServer.RegisterMessage;
import jdk.internal.util.xml.impl.Pair;

import java.util.*;
import java.util.function.Supplier;


public class ConnectionsImpl<T> implements Connections {
    private Server<T> server;
    private Vector<ConnectionHandler<T>> connectionsHandlerVector;  //////////// have to be updated, will indicate the loggedin status
    private static int connectionIdCounter = 0;
    private HashMap<Integer,ConnectionHandler<T>> connectionIDS;
    private HashMap<Integer, User> loggedInUsers;   ////////////holds the logged in members
    private HashMap<Integer, User> registeredUsers;
    private HashMap<User, LinkedList<User>> following;


    public ConnectionsImpl(Server<T> server) {
        this.server = server;
        this.connectionsHandlerVector = new Vector<>();
        this.connectionIDS = new HashMap<>();
        this.loggedInUsers = new HashMap<>();
        this.registeredUsers = new HashMap<>();
        this.following = new HashMap<>();
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
        for(Map.Entry<Integer, ConnectionHandler<T>> CH : connectionIDS.entrySet()){
            if(CH.getValue().equals(handler)) {
                id = CH.getKey();
            }
        }
        return id;
    }
    @Override
    public boolean send(int connectionId, Object msg) {
        if(connectionIDS.containsKey(connectionId) && msg!=null){  //input check
            //this client  needs to get the message
            ConnectionHandler<T> toReceive = connectionIDS.get(connectionId);
            //encode message
            toReceive.send((T)(msg));
            //put the encoded message in the CH socket
            //

            return true;
        }
        return false;
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

    public void register(RegisterMessage message, ConnectionHandler<T> handler){
        Integer id = -1;
        for(Map.Entry<Integer, ConnectionHandler<T>> CH : connectionIDS.entrySet()){
            if(CH.getValue().equals(handler)) {
                id = CH.getKey();
            }
        }
        User toRegister = new User(message.getUsername(), message.getPassword(), message.getBirthday());
        registeredUsers.put(id,toRegister);
    }

    public void login(LoginMessage message){
        Integer id = -1;
        User toLogIn = null;
        for(Map.Entry<Integer, User> user : registeredUsers.entrySet()){
            if(user.getValue().getUserName().equals(message.getUsername())) {
                id = user.getKey();
                toLogIn = user.getValue();
            }
        }
        loggedInUsers.put(id,toLogIn);

    }

    public boolean thereIsSomeOneHere(){
        return !loggedInUsers.isEmpty();
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


    public boolean isLogedIn(String userName){
            //go through the active users DB and search for the specific user
            for(User exists : loggedInUsers.values()){
                if (exists.getUserName().equals(userName))
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
    public boolean follow(FollowMessage message){
        //TODO
        return true;
    }
    public boolean unfollow(FollowMessage message){
        //TODO
        return true;
    }
    public boolean isFollowing(FollowMessage message, Integer idOfSender){
        User sender = registeredUsers.get(idOfSender);
        for(Map.Entry<User, LinkedList<User>> user : following.entrySet()){
            if(user.getKey().getUserName().equals(message.getUsername())) {
                for (User toFind: user.getValue()){
                    if (toFind.equals(sender)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
