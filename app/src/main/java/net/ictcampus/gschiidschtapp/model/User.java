package net.ictcampus.gschiidschtapp.model;

import java.io.Serializable;

/**
 * Created by vanonir on 09.06.2017.
 */

public class User implements Serializable {

    private String email;
    private String displayName;
    private String uid;

    public User() {
    }


    public boolean checkIfHasAllProperties(){
        if (email !=null&&displayName!=null&&uid!=null){
            return true;
        }else {
            return false;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
