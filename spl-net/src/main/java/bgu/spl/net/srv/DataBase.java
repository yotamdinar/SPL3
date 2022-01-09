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
    private List<String> illegalWords;

    public DataBase(){
        username_CHID_map = new ConcurrentHashMap<>();
        this.connections = new ConnectionsImpl();
        username_pass_map = new ConcurrentHashMap<>();
        username_user_map = new ConcurrentHashMap<>();
        illegalWords = new LinkedList<>();
        CH_Next_ID = 1;
    }

    private static class DataBaseHolder{
        private static DataBase instance = new DataBase();
    }

    public static DataBase get_instance(){
        return DataBaseHolder.instance;
    }

    //getters



    public ConcurrentHashMap<String, Integer> getUsername_CHID_map() {
        return username_CHID_map;
    }

    public int getCH_Next_ID() {
        return CH_Next_ID;
    }

    public ConcurrentHashMap<String, String> getUsername_pass_map() {
        return username_pass_map;
    }

    public ConcurrentHashMap<String, User> getUsername_user_map() {
        return username_user_map;
    }

    public List<String> getIllegalWords() {
        return illegalWords;
    }

    public Connections getConnections(){
        return this.connections;
    }

    //methods

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

    public User registerOP(String m, int clientChId){ /*<Username>'\0'<Password>'\0'<Birthday>'\0'*/
        String[] elements = getElements(m, '\0', 3);
        User u = register(elements[0], elements[1],elements[2], clientChId);
        //response:
        String response;
        if (u != null) {
            sendResponse("1001", clientChId);    //return ack <10><01> => <opcode><regopcode>
        }
        else {
            sendResponse("1101", clientChId);   //return error
        }
        return u;
    }

    /**
     * registers new user to the server
     * @return null if user already registered, else returns the new User instance
     */
    private User register(String username, String pass, String birthday, int clientChId){
        if (this.username_user_map.containsKey(username))
            return null; //allready registered;
        User u = new User(username, pass, birthday);
        username_user_map.put(username, u);
        username_pass_map.put(username, pass);
        username_CHID_map.put(username, clientChId);
        return u;
    }

    public void loginOP(String m,int clientChId){/*<Username>'\0'<Password>'\0'CAPCHA\0*/
        String[] elements = getElements(m, '\0', 3);
        int success = login(elements[0], elements[1], Integer.parseInt(elements[2]));
        //response:
        if (success >= 0){
            sendResponse("1002", clientChId);

            for (Post post : username_user_map.get(elements[0]).getPendingPosts()){
                sendNotification('1', post.getPublisher(), post.getPostContent(), username_CHID_map.get(elements[0]));
            }
            username_user_map.get(elements[0]).cleanPendingPosts();    //cleaning
            for (PersonalMessage pm : username_user_map.get(elements[0]).getPendingMsgs()){
                sendNotification('0', pm.getPublisher(), pm.getMsgContent(), username_CHID_map.get(elements[0]));
            }
            username_user_map.get(elements[0]).cleanPendingMsgs();     //cleaning


        }
        else {
            sendResponse("1102", clientChId);
        }
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
        if ( username != null && RegisteredAndLoggedIn(username)) {
            this.username_user_map.get(username).logout();
            sendResponse("1003", clientChId);
        }
        else {
            sendResponse("1103", clientChId);
        }
    }

    public void follow_unFollowOP(String username,String msg, int clientChId){ /*<0/1 (Follow/Unfollow)> <UserNametoFollow>*/
        String[] elements = getElements(msg, '\0', 2);
        boolean success;
        if (elements[0].equals("0"))
            success = follow(username, elements[1]);
        else success = unfollow(username, elements[1]);
       if (success)
           sendResponse("1004 "+elements[1], clientChId);
       else sendResponse("1104 "+elements[1], clientChId);
    }

    private boolean follow(String username, String toFollow){

        if ( !username.equals(toFollow) && RegisteredAndLoggedIn(username) && !username_user_map.get(username).getFollowingList().contains(toFollow) &&
            !username_user_map.get(toFollow).blockedUsers.contains(username)) {
            username_user_map.get(username).addFollowing(toFollow);
            username_user_map.get(toFollow).addFollower(username);
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
            String filteredContent = filterMessage(content);
            Post post = new Post(publisher, filteredContent);

            List<String> reciepients = post.parseTaggedUsernames();
            for (String follower : username_user_map.get(publisher).followersList) {
                if (!reciepients.contains(follower))
                    reciepients.add(follower);
            }
            for (String reciepient : reciepients) {
                if ( username_user_map.get(reciepient) != null && !username_user_map.get(reciepient).blockedUsers.contains(publisher)) {
                    if (isLoggedIn(reciepient)) {
                        username_user_map.get(reciepient).receivePost(post);

                        int reciepient_ChId = username_CHID_map.get(reciepient);
                        sendNotification('1', publisher, post.getPostContent(), reciepient_ChId);//sending notification about the post to receiving client
                    }
                    else username_user_map.get(reciepient).addDelayPost(post);
                }
            }
            username_user_map.get(publisher).publishPost(post);
            return true;
        }
        return  false;
    }

    public void personalMessageOP(String senderUsername, String msg, int clientChId){
        String[] elements = getElements(msg, '\0', 2);
        int success = personalMessage(senderUsername, elements[0], elements[1]);
        if (success >=0)
            sendResponse("1006", clientChId);
        else if (success == -1 | success == -3)
            sendResponse("1106", clientChId);
        else sendResponse("1106 @"+elements[0]+" isn’t applicable for private messages", clientChId); //need to check if response correct ************************************************************
    }

    private int personalMessage(String senderUsername, String reciepient, String content){
        if (!RegisteredAndLoggedIn(senderUsername) || senderUsername.equals(reciepient))
            return -1;
        if (!isRegistered(reciepient)) /*If the reciepient user isn’t registered an ERROR message will be returned to the client.
@<username> isn’t applicable for private messages.*/
            return -2;
        if (!username_user_map.get(senderUsername).getFollowingList().contains(reciepient))
            return -3;
        if (username_user_map.get(reciepient).blockedUsers.contains(senderUsername))
            return -4;
        String filteredContent = filterMessage(content);
        if (isLoggedIn(reciepient)) {
            username_user_map.get(reciepient).receivePm(new PersonalMessage(senderUsername, filteredContent));
            int reciepient_ChId = username_CHID_map.get(reciepient);
            sendNotification('0', senderUsername, filteredContent, reciepient_ChId);//sending notification about the pm to receiving client
        }
        else username_user_map.get(reciepient).addDelaymsg(new PersonalMessage(senderUsername, filteredContent));
        return 1;
    }

    //07
    public void produceLOGSTAT(String requestingUsername, int clientChId){
        if (RegisteredAndLoggedIn(requestingUsername)){
            for (User user: username_user_map.values()){
                if (RegisteredAndLoggedIn(user.getUsername()) && !username_user_map.get(user.getUsername()).blockedUsers.contains(requestingUsername)){
                    sendResponse("1007"+user.produceStatRecord(), clientChId); //sending the ack
                }
            }
        }
        else sendResponse("1107", clientChId);
    }

    public void produceSTATOP(String requestingUsername, String msg, int clientId){
        String[] elements = getElements(msg, '\0', 1);
        produceSTAT(requestingUsername, elements[0], clientId);
    }

    //8
    public void produceSTAT(String requestingUsername, String usersList, int clientChId){
        usersList = usersList+'|';
        if (RegisteredAndLoggedIn(requestingUsername)){
            int numOfUsersInList = 0;
            for (int i = 0; i < usersList.length() ; i++) {
                if (usersList.charAt(i) == '|')
                    numOfUsersInList++;
            }
            String[] usernames = getElements(usersList, '|', numOfUsersInList);
            for (String s : usernames){
            }
            List<String> usernamesList = new LinkedList<>();
            for (int i = 0; i < usernames.length; i++)
                usernamesList.add(usernames[i]);
            for (String username : usernamesList) {
                User u = username_user_map.get(username);
                if (u != null && isLoggedIn(username) && !username_user_map.get(username).blockedUsers.contains(requestingUsername)) {
                    sendResponse("1008" + u.produceStatRecord(), clientChId);
                } else sendResponse("1108", clientChId);
            }
        }
        else sendResponse("1108", clientChId);
    }

    //09
    public void sendNotification(char NotificationType, String PostingUser, String content, int clientChId){
        String msg = "09"+NotificationType+PostingUser+'\0'+content+'\0';
        sendResponse(msg, clientChId);
    }

    //12
    public void block(String requestingUsername, String msg, int clientChId){
        String[] elements = getElements(msg, '\0', 1);
        String toBlock = elements[0];
        if (!requestingUsername.equals(toBlock) && RegisteredAndLoggedIn(requestingUsername) && isRegistered(toBlock)){
            username_user_map.get(requestingUsername).block(toBlock);
            username_user_map.get(toBlock).block(requestingUsername);
            sendResponse("1012", clientChId);
        }
        else sendResponse("1112", clientChId);
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
     * @param usersList user to produce their status
     * @return for each user in userList the following List: <Age><NumPosts> <NumFollowers> <NumFollowing>
     */
    public List<List<Integer>> produceSTAT(String requestingUsername, List<String> usersList){
        return null;
    }



    private boolean RegisteredAndLoggedIn(String username){
        return isRegistered(username) && isLoggedIn(username);
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
        String filtered = message;
        for (String wordInContent : filtered.toLowerCase().split(" ")){
            {
                if (illegalWords.contains(wordInContent)) {
                    filtered = filtered.replace(wordInContent, "<filtered>");
                }
            }
        }
        return filtered;
    }


    private void sendResponse(String responseMsg, int connectionId){
        connections.send(connectionId, responseMsg+';');
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

    public void addIllegalWord(String illegalWord){
        illegalWords.add(illegalWord);
    }

    public String toString(){
        String out = "database hash: " + this.hashCode()+'\n';
        out += '\t' + "username_CHID_map: " + username_CHID_map.values().size()+ "\n";
        for (String username : username_CHID_map.keySet()){
            out+= "\t\t" + username+" CHid "+ database.getUsername_CHID_map().get(username);
            out+= username_user_map.get(username).toString()+"\n\t";
        }
       return out;
    }

}
