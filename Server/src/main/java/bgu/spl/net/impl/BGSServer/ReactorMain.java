package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.MessageEncoderDecoderImp;
import bgu.spl.net.api.bidi.messagingProtocolImp;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args) {
        Server<String> server = Server.reactor(Integer.parseInt(args[1]),
                Integer.parseInt(args[0]), () -> new messagingProtocolImp(), () -> new MessageEncoderDecoderImp());
        server.serve();
    }
}
