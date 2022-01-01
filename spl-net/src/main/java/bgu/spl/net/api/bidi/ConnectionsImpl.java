package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.ConnectionHandler;

import java.util.HashMap;
import java.util.List;

public class ConnectionsImpl<T> implements Connections<T>{

    HashMap<Integer, ConnectionHandler<T>> map;


    @Override
    public boolean send(int connectionId, T msg) {
        map.get(0).send(msg);
        return false;
    }

    @Override
    public void broadcast(T msg) {

    }

    @Override
    public void disconnect(int connectionId) {

    }

    public int register(Integer connectionId, ConnectionHandler<T> connectionHandler){
        this.map.put(connectionId, connectionHandler);
        return 3333333;
    }
}
