package net.ictcampus.gschiidschtapp;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * RegisterActivity (accessible via MainActivity) is the screen where a new user can register to create himself a new user.
 * It is a connection to Firebase Authentication.
 */

public class RegisterActivity extends AppCompatActivity {
    private final String TAG = "RegisterActivity";

    private int easterEggCounter = 0;
    private EditText nameField;
    private EditText emailField;
    private EditText emailRepeatField;
    private EditText passwordField;
    private EditText passwordRepeatField;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        checkAndHandleEasterEgg();
        declareEditTexts();

        handleRegisterButtonClick();


    }

    private void handleRegisterButtonClick() {
        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInputValuesAndExecuteRegistration();
            }
        });
    }


    private void declareEditTexts() {
        nameField  = (EditText) findViewById(R.id.registerName);
        emailField  = (EditText) findViewById(R.id.registerEmail);
        emailRepeatField = (EditText) findViewById(R.id.registerEmailRepeat);
        passwordField = (EditText) findViewById(R.id.registerPassword);
        passwordRepeatField = (EditText) findViewById(R.id.registerPasswordRepeat);
    }


    private void checkInputValuesAndExecuteRegistration() {

        if (TextUtils.isEmpty(nameField.getText())){
            Toast.makeText(getApplicationContext(),R.string.missing_regsistration_name,Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(emailField.getText())){
            Toast.makeText(getApplicationContext(),R.string.missing_regsistration_email,Toast.LENGTH_LONG).show();
            return;
        }
        if (!emailField.getText().toString().equals(emailRepeatField.getText().toString())){
            Toast.makeText(getApplicationContext(),R.string.notequal_regsistration_email,Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(passwordField.getText())){
            Toast.makeText(getApplicationContext(),R.string.missing_registration_password,Toast.LENGTH_LONG).show();
            return;
        }
        if (!passwordRepeatField.getText().toString().equals(passwordField.getText().toString())){
            Toast.makeText(getApplicationContext(),R.string.notequal_regsistration_password,Toast.LENGTH_LONG).show();
            return;
        }
        registerNewUser(emailField.getText().toString(),passwordField.getText().toString(),nameField.getText().toString());

    }

    private void registerNewUser(final String email, final String password, final String name) {

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d(TAG,"Create User with Email and Password successful");
                    setUserDisplayName(name);
                    String uid = mAuth.getCurrentUser().getUid();
                    createDatabaseUser(uid, email,name);
                    finish();
                }else {
                    Log.d(TAG,R.string.registration_Failed+task.getException().getMessage());
                    //TODO: Display TOAST to User if emailadress already in use.

                    Toast.makeText(getApplicationContext(),R.string.registration_Failed+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createDatabaseUser(String uid, String email, String name) {
        DatabaseReference usersRef =  FirebaseDatabase.getInstance().getReference(getString(R.string.db_users));
        usersRef.child(uid).child(getString(R.string.db_user_uid)).setValue(uid);
        usersRef.child(uid).child(getString(R.string.db_user_email)).setValue(email);
        usersRef.child(uid).child(getString(R.string.db_user_name)).setValue(name);

    }

    private void setUserDisplayName(String name) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();

        user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "User DisplayName set");
                }else {
                    Log.d(TAG,"Failed to set DisplayName"+task.getException().getMessage());
                }
            }
        });

    }

    //changes only the title in the toolbar
    private void checkAndHandleEasterEgg() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_main));
        final SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preference_file_key), getApplication().MODE_PRIVATE);

        boolean easterEggState = sharedPreferences.getBoolean("easterEggState", false);
        final Button btn = (Button) findViewById(R.id.sneekyButton);
        if (easterEggState) {
            btn.setText(R.string.app_sneekyname);
        }else{
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    easterEggCounter++;
                    if (easterEggCounter == 10) {
                        Toast.makeText(getApplicationContext(), getString(R.string.found_easterEgg), Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("easterEggState",true).commit();
                        btn.setText(R.string.app_sneekyname);
                        btn.setOnClickListener(null);
                    }
                }
            });

        }
    }
}
