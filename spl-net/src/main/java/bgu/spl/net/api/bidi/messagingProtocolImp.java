package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.User;

public class messagingProtocolImp implements BidiMessagingProtocol<String> {

    private boolean shouldTerminate = false;


    //new:
    private int id;
    private int connectionId;
    public User client;
    private ConnectionsImpl Connections;
    private DataBase dataBase;
    //ConnectionHandler connectionHandler;

    public messagingProtocolImp( ConnectionsImpl connections/*, ConnectionHandler connectionHandler*/){

        this.dataBase = DataBase.get_instance();
        this.Connections = dataBase.getConnectionsImpl();
        this.id = -1;
        this.connectionId = -1;
        //this.connectionHandler = connectionHandler; //access to CH through the connections instead
    }

    @Override
    public void start(int connectionId, Connections<String> connections) {//////*********************indexing need to be change
        System.out.println("new client connected");
        //Connections.register(connectionId, connectionHandler );
        this.connectionId = connectionId;
    }

    @Override
    public void process(String message) {
        System.out.println("processing: " + message);
        Integer opcode = Integer.parseInt(message.substring(0, 2));
        System.out.println("opcode="+opcode+", ");
        String msgBody = message.substring(2);
        if (opcode == 1){ //register
            this.client = dataBase.registerOP(msgBody);
        }
        if (opcode == 2) //login
            dataBase.loginOP(msgBody);
        if (opcode == 3) //logout
            dataBase.logout(this.client.getUsername());
        if (opcode == 4) //follow/unfollow
            dataBase.follow_unFollowOP(msgBody);
        if (opcode == 5) //post
            dataBase.post(this.client.getUsername(), msgBody);
        if (opcode == 6) //PM(personal message)
            dataBase.personalMessageOP(this.client.getUsername(), msgBody);
        if (opcode == 7) //LOGSTAT
            dataBase.produceLOGSTAT(this.client.getUsername());
        if (opcode == 8) //STAT
            dataBase.produceSTATOP(this.client.getUsername(), msgBody);

    }



    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
