package bgu.spl.net;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.api.bidi.MessageEncoderDecoderImp;
import bgu.spl.net.api.bidi.messagingProtocolImp;
import bgu.spl.net.srv.Server;
import bgu.spl.net.srv.Xavi;

public class Program {

    public static void main(String[] args){

        Connections connections = new ConnectionsImpl();
        Xavi xavi = new Xavi(connections);

        try {Server<String> server = Server.threadPerClient(777,xavi, connections->new messagingProtocolImp(xavi, connections,
                ))

        }

    }
}
