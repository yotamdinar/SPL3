package bgu.spl.net.srv;

import java.util.LinkedList;
import java.util.List;

public class Post {

    private String publisher;
    private String postContent;

    private List<String> taggedUsernames;

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

    public List<String> getTaggedUsernames(){
        return taggedUsernames;
    }

    public List<String> parseTaggedUsernames(){
        System.out.println("parsing @@@@@");
        List<String> taggedUsernames = new LinkedList<>();
        String content = getPostContent();
        while (content.indexOf('@') >= 0){  //abc@yotam @shlomi ab@xavi
            System.out.println("content.1");
            content = content.substring(content.indexOf('@')+1);   //yotam @shlomi ab@xavi
            System.out.println("content.2");
            int firstSpaceafterTag = content.indexOf(' '); //=5
            if (firstSpaceafterTag <= 0 && content.length()>0) {
                System.out.println("added" + content.substring(0));
                taggedUsernames.add(content.substring(0)); //xavi)
            }
            else {
                System.out.println(content.substring(0, firstSpaceafterTag));
                String username = content.substring(0, firstSpaceafterTag); //yotam
                taggedUsernames.add(username);
            }
            if (firstSpaceafterTag < 0 || firstSpaceafterTag + 1 == content.length())
                break;
            content = content.substring(firstSpaceafterTag+1);
        }

        for (String s : taggedUsernames)
            System.out.println(s);
        return taggedUsernames;
    }
}
