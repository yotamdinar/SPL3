package bgu.spl.net.srv;


import java.util.List;

public class User {

    private String username;
    private String password;
    private String birthday;
    private boolean loggedIn;
    List<String> followingList; //list of the users this user follows
    List<String> followersList; //list of the users that follows this user
    List<Post> postedPostsList; //list of all posted that published by this user
    List<String> blockedUsers;

    List<String> illegalWords;


    public User(String username, String password, String birthday){
        this.username = username;
        this.password = password;
        this.birthday = birthday;
        loggedIn = false;
    }

    public String getUsername() {
        return username;
    }


    /*public String getPassword() {
        return password;
    }*/
    /*public void setPassword(String password) {
        this.password = password;
    }*/
    public String getBirthday() {
        return birthday;
    }

    private void setBirthday(String birthday) {
        this.birthday = birthday;
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

    private void setFollowingList(List<String> followingList) {
        this.followingList = followingList;
    }

    public List<String> getFollowersList() {
        return followersList;
    }

    private void setFollowersList(List<String> followersList) {
        this.followersList = followersList;
    }

    public List<Post> getPostedPostsList() {
        return postedPostsList;
    }

    private void setPostedPostsList(List<Post> postedPostsList) {
        this.postedPostsList = postedPostsList;
    }

    public List<String> getBlockedUsers() {
        return blockedUsers;
    }

    private void setBlockedUsers(List<String> blockedUsers) {
        this.blockedUsers = blockedUsers;
    }

    public List<String> getIllegalWords() {
        return illegalWords;
    }

    public void setIllegalWords(List<String> illegalWords) {
        this.illegalWords = illegalWords;
    }

    public void login(){
        setLoggedIn(true);
    }

    public void logout(){
        setLoggedIn(false);

    }

    public int getAge(){
        return -1;
    }

    /*All PM messages should be saved to a data structure in the application, along with post messages.
Before showing and saving the message, the server should filter the message from words provided in
the server, and every filtered word should be replaced with ‘<filtered>’.
The data structure which includes the words need to be filtered, is hard coded in the server.
For example: if the server has a list of words [‘war’, ‘Trump’], and the message
Which some client had send was: ‘Trump is planning to declare a war on the republic of Lala-land’
Then the message that will be saved in the server is: ‘<filtered> is planning to declare a <filtered> on
the republic of Lala-land’*/
    /**
     *
     * @param message un filtered message
     * @return the message after it been filtered from illegal words
     */
    public String filterMessage(String message){
        return"-1";
    }
}
