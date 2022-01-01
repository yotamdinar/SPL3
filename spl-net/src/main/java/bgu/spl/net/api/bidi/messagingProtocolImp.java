package bgu.spl.net.api.bidi;

public class messagingProtocolImp implements BidiMessagingProtocol<String> {

    private boolean shouldTerminate = false;


    @Override
    public void start(int connectionId, Connections<String> connections) {

    }

    @Override
    public void process(String message) {

    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
