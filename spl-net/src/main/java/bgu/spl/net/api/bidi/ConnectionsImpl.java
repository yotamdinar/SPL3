package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.ConnectionHandler;

import java.util.HashMap;
import java.util.List;

public class ConnectionsImpl<T> implements Connections<T>{

    HashMap<Integer, ConnectionHandler<T>> CHId_CH_map;


    @Override
    public boolean send(int connectionId, T msg) {
        CHId_CH_map.get(0).send(msg);
        return false;
    }

    @Override
    public void broadcast(T msg) {

    }

    @Override
    public void disconnect(int connectionId) {
    }

    public HashMap<Integer, ConnectionHandler<T>> getCHId_CH_map() {
        return CHId_CH_map;
    }

    public void register(Integer connectionId, ConnectionHandler<T> connectionHandler){
        this.CHId_CH_map.put(connectionId, connectionHandler);
    }


}
