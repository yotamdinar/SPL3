package bgu.spl.net.srv;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.bidi.ConnectionsImpl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public  class DataBase {

    private static DataBase database; //for singleton purposes
    //networking related stuff
    private ConcurrentHashMap<String, Integer> username_CHID_map;
    private ConnectionsImpl connections;
    private int CH_Next_ID;

    //application related stuff
    private ConcurrentHashMap<String, String> username_pass_map;
    private ConcurrentHashMap<String, User> username_user_map;
    //

    private DataBase(){
        username_CHID_map = new ConcurrentHashMap<>();
        this.connections = new ConnectionsImpl();
        username_pass_map = new ConcurrentHashMap<>();
        username_user_map = new ConcurrentHashMap<>();
        CH_Next_ID = 1;
    }

    public static DataBase get_instance(){
        if(database==null) {
            database=new DataBase();
        }
        return database;
    }

    public Connections getConnections(){
        return this.connections;
    }

    /**
     *
     * @param connectionHandler to register in database and connections
     * @return the CH_id for this CH
     */
    public int addConnection(ConnectionHandler connectionHandler){
        int newId = this.CH_Next_ID;
        CH_Next_ID++;
        connections.register(newId, connectionHandler); //register this CH with his id in Connections
        return newId;
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
        }
        return output;
    }

    private void sendResponse(String responseMsg, int connectionId){
        connections.send(connectionId, responseMsg);
    }

    public User registerOP(String m, int clientChId){ /*<Username>'\0'<Password>'\0'<Birthday>'\0'*/
        String[] elements = getElements(m, '\0', 4);
        User u = register(elements[0], elements[1],elements[2]);
        //response:
        String response;
        if (u != null)
            sendResponse("1001", clientChId);    //return ack <10><01> => <opcode><regopcode>
        else
            sendResponse("1101", clientChId);   //return error
        return u;
    }

    /**
     * registers new user to the server
     * @return null if user already registered, else returns the new User instance
     */
    private User register(String username, String pass, String birthday /*or replace string with birthFormat?*/){
        if (this.username_user_map.containsKey(username))
            return null; //allready registered;
        User u = new User(username, pass, birthday);
        username_user_map.put(username, u);
        username_pass_map.put(username, pass);
        return u;
    }

    public void loginOP(String m,int clientChId){/*<Username>'\0'<Password>'\0'*/
        String[] elements = getElements(m, '\0', 2);
        int success = login(elements[0], elements[1], 1);
        //response:
        if (success >= 0)
            sendResponse("1002", clientChId);
        else sendResponse("1102", clientChId);
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

    public void logout(String username, int clientChId){/*<>*/
        if ( RegisteredAndLoggedIn(username))
            sendResponse("1003", clientChId);
        else sendResponse("1103", clientChId);
    }

    public void follow_unFollowOP(String username,String msg, int clientChId){ /*<0/1 (Follow/Unfollow)> <UserNametoFollow>*/
        String[] elements = getElements(msg, '\0', 2);
        boolean success;
        if (elements[0] == "0")
            success = follow(username, elements[1]);
        else success = unfollow(username, elements[1]);
       if (success)
           sendResponse("1004", clientChId);
       else sendResponse("1104", clientChId);
       //need to correct the resonses here **********************************************************************************************************
       /*The ack for this command will contain the user name of the followed/unfollowed user.
The ACK message will have the following form:
ACK-Opcode FOLLOW-Opcode <username>*/
    }

    private boolean follow(String username, String toFollow){
        if ( RegisteredAndLoggedIn(username) && !username_user_map.get(username).getFollowingList().contains(toFollow)){
            username_user_map.get(username).getFollowingList().add(toFollow);
            return true;
        }
        return false;

    }
    private boolean unfollow(String username, String toUnFollow){
        if ( RegisteredAndLoggedIn(username) && username_user_map.get(username).getFollowingList().contains(toUnFollow)){
            username_user_map.get(username).getFollowingList().remove(toUnFollow);
            return true;
        }
        return false;
    }

    public void postOP(String username,String msg, int clientChId) { /*<content>*/
        String[] elements = getElements(msg, '\0', 1);
        boolean success = post(username, elements[0], clientChId);
        if (success)
            sendResponse("1005", clientChId);
        else sendResponse("1105", clientChId);
    }

    /**
     * A post message
     * will be sent to users who are listed with a “@username” inside the message (if username is
     * registered in the system) and to users following the user who posted the message.
     * @return true if post was posted
     */
    private boolean post(String publisher, String content, int clientChId){
        if (RegisteredAndLoggedIn(publisher)){
            ////********************************************************************************************************************************
            return true;
        }
        return  false;
    }

    public void personalMessageOP(String senderUsername, String msg, int clientChId){
        String[] elements = getElements(msg, '\0', 3);
        int success = personalMessage(senderUsername, elements[0], elements[1], elements[2]);
        if (success >=0)
            sendResponse("1006", clientChId);
        else if (success == -1 | success == -3)
            sendResponse("1106", clientChId);
        else sendResponse("1106@"+elements[0], clientChId); //need to check if response correct ************************************************************
    }

    private int personalMessage(String senderUsername, String reciepient, String content, String dateAndTime){
        if (!RegisteredAndLoggedIn(senderUsername))
            return -1;
        if (!isRegistered(reciepient)) /*If the reciepient user isn’t registered an ERROR message will be returned to the client.
@<username> isn’t applicable for private messages.*/
            return -2;
        if (!username_user_map.get(senderUsername).getFollowingList().contains(reciepient))
            return -3;
        //send the pm(not implemented) ***************************************************************************************************
        return 1;
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
    public List<List<Integer>> produceLOGSTAT(String requestingUsername, int clientChId){
        return null;
    }

    public void produceSTATOP(String requestingUsername, String usersList, int clientChId){
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

    private boolean RegisteredAndLoggedIn(String username){
        return isRegistered(username) && isLoggedIn(username);
    }


}
