package bgu.spl.net;

import bgu.spl.net.api.bidi.*;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.Server;
import bgu.spl.net.srv.TestDataBase;

public class Program {

    public static void main(String[] args){

        DataBase dataBase = DataBase.get_instance();

/*        try {
            Server<String> server = Server.threadPerClient(
                    777,()->new messagingProtocolImp(),()->new MessageEncoderDecoderImp());

            server.serve();
        }
        catch (Exception e){ System.out.println("exception");}

        //Socket socket = new Socket();
        BidiMessagingProtocol<String> protocol = new messagingProtocolImp();
        String msg = "01Username"+'\0'+"Password"+'\0'+"Birthday"+'\0';
        protocol.process(msg);*/

        TestDataBase Test = new TestDataBase(dataBase);
        Test.runTest();
    }
}
