package bgu.spl.net.srv;

public class Post {

    private String publisher;
    private String postContent;

    public Post(String post_publisher, String post_content){
        this.publisher = post_publisher;
        this.postContent = post_content;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPostContent() {
        return postContent;
    }
}
