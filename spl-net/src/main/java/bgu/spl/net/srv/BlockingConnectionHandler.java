package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.bidi.ConnectionsImpl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;

    //new:
    private int CH_id = 0;
    private DataBase dataBase;
    private Connections connections;

    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocol<T> protocol) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        dataBase = DataBase.get_instance();
        connections = dataBase.getConnections();
        CH_id = dataBase.addConnection(this); //register this Ch in db and connections and get and set his Ch_id
        protocol.start(this.CH_id, connections);   //d"n.. set the Cg_id in protocol
    }

    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());

            System.out.println("CH number:: " + this.CH_id +" starting run loop");
            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    System.out.println("(CH.RUN) after decode: "+nextMessage);
                    protocol.process(nextMessage);
                   /* if (response != null) {
                        out.write(encdec.encode(response));
                        out.flush();
                    }*/
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    @Override
    public void send(T msg) {//**********************************not sure if that correct*******************************
        try{
            System.out.println("(CH.send)sending the following string msg: "+msg);
            out.write(encdec.encode(msg));
            out.flush();
        }
        catch(Exception e){};
    }
}
