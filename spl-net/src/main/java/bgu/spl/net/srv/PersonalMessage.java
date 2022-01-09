package bgu.spl.net.srv;

public class PersonalMessage {

    private String publisher;
    private String msgContent;

    public PersonalMessage(String msg_publisher, String msgContent){
        this.publisher = msg_publisher;
        this.msgContent = msgContent;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getMsgContent() {
        return msgContent;
    }

}
