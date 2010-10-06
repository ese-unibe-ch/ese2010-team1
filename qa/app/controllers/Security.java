package controllers;
 
import models.*;
 
public class Security extends Secure.Security {
	
    static boolean authenticate(String username, String password) {
        if(User.get(username) == null)
        	new User(username);
        return true;
    }
    
    static void onDisconnected() {
        Application.index();
    }
}