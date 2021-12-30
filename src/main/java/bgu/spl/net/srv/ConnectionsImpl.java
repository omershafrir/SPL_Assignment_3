package bgu.spl.net.srv;

import bgu.spl.net.api.bidi.Connections;

import java.util.HashMap;
import java.util.Vector;

public class ConnectionsImpl<T> implements Connections {
    private Server<T> server;
    private Vector<ConnectionHandler<T>> connectionsHandlerVector;
    private static int connectionIdCounter = 0;
    private HashMap<ConnectionHandler<T> , Integer> connectionIDS;

    public ConnectionsImpl(Server<T> server) {
        this.server = server;
        this.connectionsHandlerVector = new Vector<>();
        this.connectionIDS = new HashMap<>();
    }

    @Override
    public boolean send(int connectionId, Object msg) {
        if(connectionIDS.containsKey(connectionId) && msg!=null){  //input check
            connectionIDS.get(connectionId).send((T)msg);

            for (Integer currConnectionId : connectionIDS.keySet()){
                if(currConnectionId.equals(connectionId)) {
                    connectionIDS.ge
                    handler.send((T) msg);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void broadcast(Object msg) {

    }

    @Override
    public void disconnect(int connectionId) {

    }

    public void addHandler(ConnectionHandler<T> handler){
        connectionsHandlerVector.add(handler);
        connectionIDS.put(handler , connectionIdCounter++);
    }

    public void deleteHandler(ConnectionHandler<T> handler){
        if(connectionsHandlerVector.contains(handler)) {
            connectionsHandlerVector.remove(handler);
            connectionIDS.remove(handler);
        }
    }
}
