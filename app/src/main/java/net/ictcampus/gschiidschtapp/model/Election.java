package net.ictcampus.gschiidschtapp.model;

import java.io.Serializable;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by vanonir on 09.06.2017.
 */

public class Election implements Serializable {

    private boolean running;
    private boolean elected;
    private User candidate;
    private long time;
    private int maxVotes;
    private Hashtable<User,Boolean> votes = new Hashtable<>();



    public Election() {

    }

    public Election(User candidate){
        this.candidate = candidate;
        running =true;
        time = System.nanoTime();

    }

    public boolean vote(User user,boolean vote){
        if (user.getUid() == candidate.getUid()){
            return false;
        }else {
            //TODO:implement insert in list.
            return true;
        }


    }

}
