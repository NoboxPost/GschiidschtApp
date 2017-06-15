package net.ictcampus.gschiidschtapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.ictcampus.gschiidschtapp.model.User;

/**
 * Created by vanonir on 09.06.2017.
 */

public class LocalUserHandler {


    private boolean loggedIn =false;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference refUser = FirebaseDatabase.getInstance().getReference("User");


    public LocalUserHandler() {
        loggedIn = checkIfLoggedInToFirebase();
    }


    public boolean checkIfLoggedInToFirebase() {

        if (mAuth.getCurrentUser() !=null){
            return true;
        }else {
            return false;
        }
    }

    public User getCurrentFBAuthUser(String email, String pwd){
        if (!checkIfLoggedInToFirebase()){
            return null;
        }
        User user = new User();
        user.setEmail(email);
        user.setUid(mAuth.getCurrentUser().getUid());

        return user;
    }

}
