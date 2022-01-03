package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.Xavi;

public class messagingProtocolImp implements BidiMessagingProtocol<String> {

    private boolean shouldTerminate = false;


    //new:
    private int id;
    private int connectionId;
    private ConnectionsImpl Connections;
    Xavi xavi;
    //ConnectionHandler connectionHandler;

    public messagingProtocolImp(Xavi xavi, ConnectionsImpl connections, ConnectionHandler connectionHandler){
        this.Connections = connections;
        this.xavi = xavi;
        this.id = -1;
        this.connectionId = -1;
        //this.connectionHandler = connectionHandler; //access to CH through the connections instead
    }

    @Override
    public void start(int connectionId, Connections<String> connections) {//////*********************indexing need to be change
        System.out.println("new client connected");
        Connections.register(connectionId, connectionHandler );
        this.connectionId = connectionId;
    }

    @Override
    public void process(String message) {
        Integer opcode = Integer.parseInt(message.substring(0, 2));
        processOperation(opcode);
    }

    private void processOperation(int opcode){

    }

    private void Register(){

    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
