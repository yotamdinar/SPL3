package bgu.spl.net.srv;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.bidi.MessageEncoderDecoderImp;
import bgu.spl.net.api.bidi.messagingProtocolImp;

import java.net.Socket;

public class TestDataBase {

    DataBase dataBase;
    Connections connections;
    BlockingConnectionHandler handler = new BlockingConnectionHandler(new Socket(),new MessageEncoderDecoderImp(), new messagingProtocolImp());

    public TestDataBase(DataBase dataBase){
        this.dataBase =  dataBase;
        connections = dataBase.getConnections();
    }

    public void runTest(){
        User u1 = null;
        u1 = REGISTER("shlomi\0Pass\0Birthday\0", 1);
        if (u1 != null) {
            System.out.println("REGISTER good:\n");
        }
        System.out.println(dataBase);

        if (LOGIN(u1, "shlomi\0Pass\0"+'1'+"\0", 1 )){
            System.out.println("LOGIN good:\n");
        }
        System.out.println(dataBase);
    }

    private User REGISTER(String m, int clientChId){
        User u1 = dataBase.registerOP(m, clientChId);
        if (u1 != null && dataBase.getUsername_CHID_map().containsKey(u1.getUsername()))
            return u1;
         return null;
    }

    private boolean LOGIN(User u1, String m,int clientChId){
        dataBase.loginOP(m, clientChId);
        if (dataBase.getUsername_user_map().get(u1.getUsername()).isLoggedIn())
            return true;
        return false;
    }
}

