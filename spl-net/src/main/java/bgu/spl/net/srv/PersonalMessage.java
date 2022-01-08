package bgu.spl.net.srv;

public class PersonalMessage {

    private String publisher;
    private String msgContent;
    private String dateAndTime;

    public PersonalMessage(String msg_publisher, String msgContent, String dateAndTime){
        this.publisher = msg_publisher;
        this.msgContent = msgContent;
        this.dateAndTime = dateAndTime;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }
}
