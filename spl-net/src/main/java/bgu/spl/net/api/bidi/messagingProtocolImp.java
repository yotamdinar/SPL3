package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.Xavi;

public class messagingProtocolImp implements BidiMessagingProtocol<String> {

    private boolean shouldTerminate = false;

    //new:
    private int id;
    private int connectionId;
    Xavi xavi;

    public messagingProtocolImp(Xavi xavi){
        this.xavi = xavi;
        this.id = -1;
        this.connectionId = -1;
    }

    @Override
    public void start(int connectionId, Connections<String> connections) {
        System.out.println("new client connected");
        this.connectionId = this.xavi.
    }

    @Override
    public void process(String message) {
        Integer opcode = Integer.parseInt(message.substring(0, 2));
        processOperation(opcode);
    }

    private void processOperation(int opcode){

    }

    private void Register(){

    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
