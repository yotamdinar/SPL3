package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.MessageEncoderDecoderImp;
import bgu.spl.net.api.bidi.messagingProtocolImp;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.Server;

public class TPCMain {

    DataBase dataBase = DataBase.get_instance();
    public static void main(String[] args) {
        /*Server<String> server = Server.threadPerClient(
                Integer.parseInt(args[0]), () -> new messagingProtocolImp(), () -> new MessageEncoderDecoderImp());
        server.serve();*/
    }
}
