package bgu.spl.net.srv;

import bgu.spl.net.api.bidi.Connections;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Xavi {

    //networking related stuff
    ConcurrentHashMap<Integer, ConnectionHandler> clients;
    Connections connections;

    //application related stuff
    ConcurrentHashMap<String, String> username_pass_map;
    ConcurrentHashMap<String, User> username_user_map;


    public Xavi(Connections connections){
        clients = new ConcurrentHashMap<>();
        this.connections = connections;

        username_pass_map = new ConcurrentHashMap<>();
        username_user_map = new ConcurrentHashMap<>();
    }

    public int addConnection(ConnectionHandler connectionHandler){
        throw new NotImplementedException();
    }

    /**
     * registers new user to the server
     * @return false if user already registered, else returns true
     */
    public boolean register(String username, String pass, String birthday /*or replace string with birthFormat?*/){
        if (this.username_user_map.containsKey(username))
            return false; //allready registered;
        username_user_map.put(username, new User(username, pass, birthday));
        username_pass_map.put(username, pass);
        return true;
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

    public int logout(String username){
        if (true)
            return 2;
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
    public List<List<Integer>> produceLOGSTAT(){
        throw new NotImplementedException();
    }

    /**
     * @param usersList user to produce their status
     * @return for each user in userList the following List: <Age><NumPosts> <NumFollowers> <NumFollowing>
     */
    public List<List<Integer>> produceSTAT(List<String> usersList){
        throw new NotImplementedException();
    }

    public void Block(String username){
        throw new NotImplementedException();
    }


}
