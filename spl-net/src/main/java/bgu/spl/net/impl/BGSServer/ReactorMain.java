package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.MessageEncoderDecoderImp;
import bgu.spl.net.api.bidi.messagingProtocolImp;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.Server;

import java.util.LinkedList;
import java.util.List;

public class ReactorMain {
    public static void main(String[] args) {

        DataBase dataBase = DataBase.get_instance();
        for(int i = 2; i < args.length ; i++){
            dataBase.addIllegalWord(args[i]);
        }


        Server<String> server = Server.reactor(Integer.parseInt(args[1]),
                Integer.parseInt(args[0]), () -> new messagingProtocolImp(), () -> new MessageEncoderDecoderImp());
        server.serve();
    }
}
