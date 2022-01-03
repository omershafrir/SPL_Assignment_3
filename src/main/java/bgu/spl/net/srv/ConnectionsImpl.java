package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import jdk.internal.util.xml.impl.Pair;

import java.util.HashMap;
import java.util.Vector;
import java.util.function.Supplier;


public class ConnectionsImpl<T> implements Connections {
    private Server<T> server;
    private Vector<ConnectionHandler<T>> connectionsHandlerVector;  //////////// have to be updated, will indicate the loggedin status
    private static int connectionIdCounter = 0;
    private HashMap<Integer,ConnectionHandler<T>> connectionIDS;
    private HashMap<Integer, HashMap<String,String>> activeUsers;   ////////////added member

    public ConnectionsImpl(Server<T> server) {
        this.server = server;
        this.connectionsHandlerVector = new Vector<>();
        this.connectionIDS = new HashMap<>();
        this.activeUsers = new HashMap<>();
    }

    @Override
    public boolean send(int connectionId, Object msg) {
        if(connectionIDS.containsKey(connectionId) && msg!=null){  //input check
            ConnectionHandler<T> toReceive = connectionIDS.get(connectionId);
            toReceive.send((T)(msg));
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

    public void register(String userName, String password, ConnectionHandler<T> handler){
        if(isLogedIn(userName,password,handler)){
            //this user already exist
            //send a ERROR message
        }
        else{ // the user is not an active user, needs to be registered
            HashMap activeuser = new HashMap<String,String>();
            activeuser.put(userName,password);

            //TODO:put handler in the connectionIDS using "addHandler" function
            //TODO:
        }



//        HashMap activeuser = new HashMap<String,String>();
//        activeuser.put(userName,password);
//        boolean exist = false;
//        for(HashMap exists : activeUsers.values()){
//            if(activeuser.equals(exists)){
//                exist = true;
//                //this user already exist
//                //send a ERROR message
//            }
//        }
//        if(!exist){
//
//        }




    }

    public boolean isLogedIn(String userName, String password,ConnectionHandler connectionHandler){
        //check if has a handler
        if(connectionsHandlerVector.contains(connectionHandler)){
            //go through the active users DB and search for the specific user
            for(HashMap exists : activeUsers.values()){
                if (exists.containsKey(userName) && exists.containsValue(password))
                    return true;
            }
        }
        return false;
    }


}
