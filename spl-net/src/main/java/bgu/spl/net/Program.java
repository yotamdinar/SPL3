package bgu.spl.net;

import bgu.spl.net.api.bidi.*;
import bgu.spl.net.srv.DataBase;

public class Program {

    public static void main(String[] args){

        DataBase dataBase = DataBase.get_instance();

//        try {Server<String> server = Server.threadPerClient(777,xavi, connections->new messagingProtocolImp(xavi, connections,
//                ))
//
//        }

        //Socket socket = new Socket();
        //ConnectionHandler<String> handler  = new BlockingConnectionHandler<>(socket,new MessageEncoderDecoderImp(), new messagingProtocolImp(xavi, connections));
        BidiMessagingProtocol<String> protocol = new messagingProtocolImp(dataBase.getConnectionsImpl());
        String msg = "01Username"+'\0'+"Password"+'\0'+"Birthday"+'\0';
        protocol.process(msg);
    }
}
