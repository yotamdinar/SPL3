package bgu.spl.net.srv;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.bidi.ConnectionsImpl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public  class DataBase {

    //networking related stuff
    private ConcurrentHashMap<Integer, ConnectionHandler> clients;
    private ConnectionsImpl connections;
    private static DataBase database;
    //application related stuff
    private ConcurrentHashMap<String, String> username_pass_map;
    private ConcurrentHashMap<String, User> username_user_map;


    private DataBase(){
        clients = new ConcurrentHashMap<>();
        this.connections = new ConnectionsImpl();

        username_pass_map = new ConcurrentHashMap<>();
        username_user_map = new ConcurrentHashMap<>();
    }

    public static DataBase get_instance()
    {
        if(database==null)
        {
            database=new DataBase();
            return database;
        }
        else
        {
            return database;
        }
    }

    public ConnectionsImpl getConnectionsImpl(){
        return this.connections;
    }
    public int addConnection(ConnectionHandler connectionHandler){
        return -1;
    }



    /**
     * @param msg <Username>'/0'<Password>'/0'</Birthday>'/0'
     * @param delimiter
     * @param numberOfElements
     * @return
     */
    private String[] getElements(String msg, char delimiter, int numberOfElements) {
        String m =  msg;
        String[] output = new String[numberOfElements];
        for (int i = 0; i < numberOfElements && m.indexOf(delimiter) != -1; i++) {
            output[i] = m.substring(0, m.indexOf(delimiter));   //the next element
            m = m.substring(m.indexOf(delimiter)+1);   //after catting rthe element
            System.out.println(i+", "+m);
        }
        return output;
    }

    public User registerOP(String m){ /*<Username>'\0'<Password>'\0'<Birthday>'\0'*/
        String[] elements = getElements(m, '\0', 4);
        String username = elements[0];
        //System.out.println("username="+ username+", ");
        String pass = elements[1];
        //System.out.println("pass="+pass+", ");
        String birthday = elements[2];
        //System.out.println("birthday=" + birthday);

        return register(username, pass,birthday);
    }

    public void loginOP(String m){/*<Username>'\0'<Password>'\0'*/
        String[] elements = getElements(m, '\0', 2);
        login(elements[0], elements[1], 1);
    }

    /**
     * registers new user to the server
     * @return false if user already registered, else returns true******************************************************fix return comment
     */
    private User register(String username, String pass, String birthday /*or replace string with birthFormat?*/){
        if (this.username_user_map.containsKey(username))
            return null; //allready registered;
        User u = new User(username, pass, birthday);
        username_user_map.put(username, u);
        username_pass_map.put(username, pass);
        return u;
    }

    /**
     * logs in  a user to the System
     * @return -1 if username doesn't exist, -2 if pass doesn't match given username,
     * -3 if  user already logged-in, -10 if YOU ARE A ROBOT,  else returns 1
     */
    public int login(String username, String pass, int captcha/*int or bit depend on decoding impl*/){
        if (captcha != 1)
            return -10;
        if (!isRegistered(username)) //user doesn't exist in the server
            return -1;
        if (!this.username_pass_map.get(username).equals(pass)) //pass doesn't match given username
            return -2;
        if (isLoggedIn(username)) // user already logged-in
            return -3;
        username_user_map.get(username).login();
        return 1;
    }

    public int logout(String username){/*<>*/
        if (true)
            return 2;
        return 1;
    }

    public void follow_unFollowOP(String msg){ /*<0/1 (Follow/Unfollow)> <UserName>*/
        String[] elements = getElements(msg, '\0', 2);
        if (elements[0] == "0")
            follow(elements[1]);
        else unfollow(elements[1]);
    }

    private void follow(String username){

    }
    private void unfollow(String username){

    }

    public void post(String publisher, String content){
        //post a post
    }

    public void personalMessageOP(String senderUsername, String msg){
        String[] elements = getElements(msg, '\0', 3);
        personalMessage(senderUsername, elements[0], elements[1], elements[2]);
    }

    private void personalMessage(String senderUsername, String sendToUsername, String content, String dateAndTime){

    }

    /**
     * @param username , must be a registered user
     * @return true if this user logged in
     */
    private boolean isLoggedIn(String username){
        return this.username_user_map.get(username).isLoggedIn();
    }

    /**
     * @return true if this user is registered to the server
     */
    private boolean isRegistered(String username){
        return this.username_pass_map.containsKey(username);
    }

    /**
     *
     * @return for each logged in user the following List: <Age><NumPosts> <NumFollowers> <NumFollowing>
     */
    public List<List<Integer>> produceLOGSTAT(String requestingUsername){
        return null;
    }

    public void produceSTATOP(String requestingUsername, String usersList){
        int numOfUsersInList = 1 ;
        for(int i = 0 ; i < usersList.length()-1;i++)
            if (usersList.charAt(i) == '|')
                numOfUsersInList ++;
        String[] usernames = getElements(usersList, '|', numOfUsersInList);
        List<String> usernamesList = new LinkedList<>();
        for (int i = 0; i < usernames.length;i++)
            usernamesList.add(usernames[i]);
        produceSTAT(requestingUsername, usernamesList);
    }

    /**
     * @param usersList user to produce their status
     * @return for each user in userList the following List: <Age><NumPosts> <NumFollowers> <NumFollowing>
     */
    public List<List<Integer>> produceSTAT(String requestingUsername, List<String> usersList){
        return null;
    }

    public void Block(String username){
    }


}
