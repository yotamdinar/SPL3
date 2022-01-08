package bgu.spl.net.srv;


import java.util.LinkedList;
import java.util.List;

public class User {

    private String username;
    private String password;
    private String birthday;    //DD-MM-YYYY
    private boolean loggedIn;
    List<String> followingList; //list of the users this user follows
    List<String> followersList; //list of the users that follows this user
    List<Post> postedPostsList; //list of all posted that published by this user
    List<Post> postsReceived; //list of all posted that this user recived
    List<String> blockedUsers;

    List<String> illegalWords;


    public User(String username, String password, String birthday){
        this.username = username;
        this.password = password;
        this.birthday = birthday;
        loggedIn = false;
        followingList = new LinkedList<>();
        followersList = new LinkedList<>();
        postedPostsList  = new LinkedList<>();
        postsReceived = new LinkedList<>();
        blockedUsers = new LinkedList<>();
        illegalWords = new LinkedList<>();
        illegalWords.add("macabi");

    }

    public String getUsername() {
        return username;
    }


    /*public String getPassword() {
        return password;
    }*/

    public String getBirthday() {
        return birthday;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    private void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public List<String> getFollowingList() {
        return followingList;
    }

    public List<String> getFollowersList() {
        return followersList;
    }

    public List<Post> getPostedPostsList() {
        return postedPostsList;
    }

    public List<String> getBlockedUsers() {
        return blockedUsers;
    }

    public List<String> getIllegalWords() {
        return illegalWords;
    }

    public List<Post> getPostsReceived() {
        return postsReceived;
    }

    public void login(){
        setLoggedIn(true);
    }

    public void logout(){
        setLoggedIn(false);

    }

    /**
     * add the post to list of post accepted by this user
     */
    public void receivePost(Post post){
        this.postsReceived.add(post);
    }

    /**
     * adds a post published by this user to the published post list
     */
    public void publishPost(Post post){
        this.postedPostsList.add(post);
    }

    public int getAge(){//DD-MM-YYYY
        return 2022 - Integer.parseInt(birthday.substring(6));
    }

    public String produceStatRecord(){
        return ""+getAge()+" "+postedPostsList.size()+" "+followersList.size()+" "+followingList.size();
    }

    public void block(String toBlockUsername) {
        if (followingList.contains(toBlockUsername))
            followingList.remove(toBlockUsername);
        if (followersList.contains(toBlockUsername))
            followersList.remove(toBlockUsername);
        blockedUsers.add(toBlockUsername);
    }

    public String toString() {
        return "" + username + " pass: " + password + " birth: " + birthday + " loggedin: " + loggedIn + " follwing: " + followingList.size() +
                " followers: " + followersList.size() + " postedPostsList: " + postedPostsList.size() + " postsReceived: " + postedPostsList.size() +
                " blockedUsers: " + blockedUsers.size();

    }
}
