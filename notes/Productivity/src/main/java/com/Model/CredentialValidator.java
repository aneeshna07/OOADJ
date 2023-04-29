package com.Model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.sql.SQLException;

public class CredentialValidator {
    public static String validateUsername(String username) throws SQLException {
        if(username.equals("credentials") || username.equals("admin") || username.equals("template"))
            return "Username unavailable!";
        if (ConnectSQL.searchUsername(username))
            return "User already exists!";

        if(username.length() < 5 || username.length() > 10)
            return "Username must be from 5 to 10 characters!";

        Pattern pattern = Pattern.compile("[^a-zA-Z0-9_]");
        Matcher matcher = pattern.matcher(username);
        if(matcher.find())
            return "Username must contain only alphanumeric and underscores!";
        else
            return "";
    }
    public static String validatePassword(String password){
        if (password.length() < 6 || password.length() > 15) {
            return "Password must be from 6 to 15 characters!";
        }
        boolean hasUpperCase = false;
        boolean hasSpecial = false;
        boolean hasDigit = false;

        String specialChar = "!@#$%^&*()-+";
        for (Character c : password.toCharArray()) {
            if(Character.isLowerCase(c)) {}
            else if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }else if (specialChar.contains(Character.toString(c))) {
                hasSpecial = true;
            } else{
                return "Invalid character in password found!";
            }
        }
        if(hasUpperCase && hasSpecial && hasDigit)
            return "";
        else
            return "Password must contain at least one - uppercase, lowercase and special character!";
    }
    public static String encryptPassword(String password) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }
    public static String verifyCredentials(String username, String password) throws Exception {
        if (ConnectSQL.searchUsername(username)) {
            String storedPassword = ConnectSQL.getPassword(username);
            if (storedPassword.equals(encryptPassword(password)))
                return "";
            else
                return "Incorrect password!";
        } else
            return "User does not exist!";
    }

}
