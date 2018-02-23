package com.example.oluwatise.quote.HelperClasses;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Oluwatise on 11/27/2017.
 */

public class QuoteObject {
    String msg;
    String name;
    String location;
    int numberOfLikes;
    long timeStamp;
    boolean isUserLiked = false;
    boolean isUserFlagged = false;
    ArrayList<String> likedUsers = new ArrayList<>();
    ArrayList<String> flaggedUsers = new ArrayList<>();

    //constructor with empty arguments (done intentionally)
    public QuoteObject() {
    }

    // setVariables is taking the place of a constructor... cuz firestore
    // cant convert an object to custom object if the custom object/class
    // (see GetUserHistory class) has
    // a constructor with parameters. so we'll explicitly call the setVariables method
    public void setVariables (String name, String location, String msg, long timeStamp) {
        this.msg = msg;
        this.name = name;
        this.location = location;
        this.timeStamp = timeStamp;
    }

                    /* Setters */
    public void setNumberOfLikes(int numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public void setUserLiked(boolean userLiked) {
        isUserLiked = userLiked;
    }

    public void setUserFlagged(boolean userFlagged) {
        isUserFlagged = userFlagged;
    }

    public void addLikedUser(String user) {
        this.likedUsers.add(user);
    }
    public void removeLikedUser(String user) {
        for (int i=0; i<=this.likedUsers.size(); i++) {
            if (likedUsers.get(i).equals(user)){
                this.likedUsers.remove(i);
                break;
            }
        }
    }

    public void addFlaggedUser(String user) {
        this.flaggedUsers.add(user);
    }

    /* Getters */
    public String getMsg() {
        return msg;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public int getNumberOfLikes() {
        return numberOfLikes;
    }
    public Long getTimeStamp() {return timeStamp;}

    public boolean isUserLiked() {
        return isUserLiked;
    }

    public boolean isUserFlagged() {
        return isUserFlagged;
    }

    public ArrayList<String> getLikedUsers() {
        return likedUsers;
    }

    public ArrayList<String> getFlaggedUsers() {
        return flaggedUsers;
    }
}
