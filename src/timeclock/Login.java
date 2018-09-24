/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timeclock;

/**
 * This class checks the user name and password on the admin pop up
 * @author jstil copied from http://www.zentut.com/java-swing/simple-login-dialog/
 * 
 * change line 18 inside the " " to set username and password
 */
public class Login {
 
    public static boolean authenticate(String username, String password) {
        // hardcoded username and password
        if (username.equals("bob") && password.equals("secret")) {
            return true;
        }
        return false;
    }
} 
