package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.bidi.Connections;

import java.util.HashMap;
import java.util.Vector;
import java.util.function.Supplier;

public class ConnectionsImpl<T> implements Connections {
    private Server<T> server;
    private Vector<ConnectionHandler<T>> connectionsHandlerVector;
    private static int connectionIdCounter = 0;
    private HashMap<Integer,ConnectionHandler<T>> connectionIDS;

    public ConnectionsImpl(Server<T> server) {
        this.server = server;
        this.connectionsHandlerVector = new Vector<>();
        this.connectionIDS = new HashMap<>();
    }

    @Override
    public boolean send(int connectionId, Object msg) {
        if(connectionIDS.containsKey(connectionId) && msg!=null){  //input check
            //this client  needs to get the message
            ConnectionHandler<T> toReceive = connectionIDS.get(connectionId);
            //encode message

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


}
