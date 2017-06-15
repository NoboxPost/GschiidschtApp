package net.ictcampus.gschiidschtapp.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vanonir on 09.06.2017.
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
