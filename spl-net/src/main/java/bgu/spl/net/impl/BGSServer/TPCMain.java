package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.MessageEncoderDecoderImp;
import bgu.spl.net.api.bidi.messagingProtocolImp;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.Server;

import java.util.LinkedList;
import java.util.List;

public class TPCMain {

    public static void main(String[] args) {

        DataBase dataBase = DataBase.get_instance();
        for(int i = 1; i < args.length ; i++){
            dataBase.addIllegalWord(args[i]);
        }

        Server<String> server = Server.threadPerClient(
                Integer.parseInt(args[0]), () -> new messagingProtocolImp(), () -> new MessageEncoderDecoderImp());
        server.serve();
    }






















/*    public static void main(String[] args) {
        Server<String> server = Server.threadPerClient(
                7777, () -> new messagingProtocolImp(), () -> new MessageEncoderDecoderImp());
        server.serve();
    }*/
}
