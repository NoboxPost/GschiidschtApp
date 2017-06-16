package net.ictcampus.gschiidschtapp.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A User Object is a collection of different user-attributes, among others, in which teams he has member-status.
 * User is like a database-mirror for better programming-usage.
 */

public class User implements Serializable {

    String email;
    String name;
    ArrayList<String> teams;
    String uid;

    public User() {
    }

    public User(String email, String name, ArrayList<String> teams, String uid) {
        this.email = email;
        this.name = name;
        this.teams = teams;
        this.uid = uid;
    }

    @Override
    public String toString() {
        return name+" "+email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getTeams() {
        return teams;
    }

    public void setTeams(ArrayList<String> teams) {
        this.teams = teams;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
