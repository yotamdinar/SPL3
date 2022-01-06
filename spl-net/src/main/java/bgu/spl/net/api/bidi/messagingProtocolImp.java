package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.User;

public class messagingProtocolImp implements BidiMessagingProtocol<String> {

    private boolean shouldTerminate = false;


    //new:
    private int connectionId;
    public User user;
    //private ConnectionsImpl Connections;
    private Connections<String> connections;
    private DataBase dataBase;
    //ConnectionHandler connectionHandler;

    public messagingProtocolImp( /*ConnectionsImpl connections*/ /*, ConnectionHandler connectionHandler*/){

        this.dataBase = DataBase.get_instance();
        this.connections = dataBase.getConnections();
        this.connectionId = -1;     //connection id wasn't initiated yet
    }

    @Override
    public void start(int connectionId, Connections<String> connections) {
        this.connectionId = connectionId;
        System.out.println("new client connected and this protocol now has his CH_Id");
    }

    @Override
    public void process(String message) { //02'\0'<Username>'\0'<Password>'\0'
        System.out.println("processing: " + message);
        Integer opcode = Integer.parseInt(message.substring(0, 2));
        System.out.println("opcode="+opcode+", ");
        String msgBody = "";
        if (message.length() > 2)
             msgBody = message.substring(2); //'\0'<Username>'\0'<Password>'\0'
        if (opcode == 1){ //register
            this.user = dataBase.registerOP(msgBody, connectionId); //also initiating the client field
            //the database responsible register the user and return ack/error response to the client
        }
        if (opcode == 2) //login
            dataBase.loginOP(msgBody, connectionId);
        if (opcode == 3) //logout
            dataBase.logout(this.user.getUsername(),connectionId);
        if (opcode == 4) //follow/unfollow
            dataBase.follow_unFollowOP(this.user.getUsername(),msgBody,connectionId);
        if (opcode == 5) //post
            dataBase.postOP(this.user.getUsername(), msgBody,connectionId);
        if (opcode == 6) //PM(personal message)
            dataBase.personalMessageOP(this.user.getUsername(), msgBody,connectionId);
        if (opcode == 7) //LOGSTAT
            dataBase.produceLOGSTAT(this.user.getUsername(),connectionId);
        if (opcode == 8) //STAT
            dataBase.produceSTAT(this.user.getUsername(), msgBody,connectionId);

    }


    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
