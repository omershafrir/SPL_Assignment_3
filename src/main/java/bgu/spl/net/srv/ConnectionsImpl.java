package bgu.spl.net.srv;

import bgu.spl.net.api.bidi.Connections;

import java.util.Vector;

public class ConnectionsImpl<T> implements Connections {
    private Server<T> server;
    private Vector<ConnectionHandler<T>> connectionsHandlerVector;
    private static int connectionIdCounter = 0;

    public ConnectionsImpl(Server<T> server) {
        this.server = server;
        this.connectionsHandlerVector = new Vector<>();
    }

    @Override
    public boolean send(int connectionId, Object msg) {
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
    }

    public void deleteHandler(ConnectionHandler<T> handler){
        if(connectionsHandlerVector.contains(handler))
            connectionsHandlerVector.remove(handler);
    }
}
