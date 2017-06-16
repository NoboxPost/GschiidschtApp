package net.ictcampus.gschiidschtapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * MainActivity is the first screen. It is only displayed, when the user is not signed in.
 * MainActivity provides the registration (the link to RegisterActivity) and the login (full sign-in-form, NOT a link).
 */

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private int easterEggCounter = 0;
    EditText loginEmail;
    EditText loginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkAndHandleEasterEgg();
        initEditTextes();
        handleLinkToRegisterActivity();
        handleLoginButtonClick();


    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfLoggedInAndIntendTeamActivity();

        //TODO: reset header after logout

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
    }

    private void checkIfLoggedInAndIntendTeamActivity() {
        if (checkIfLoggedInToFirebase()) {
            Intent teamActivityIntent = new Intent(getApplicationContext(), TeamActivity.class);
            startActivity(teamActivityIntent);
        }
    }

    private void handleLoginButtonClick() {
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(loginEmail.getText())) {
                    Toast.makeText(getApplicationContext(), R.string.loginEmptyEmail, Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(loginPassword.getText())) {
                    Toast.makeText(getApplicationContext(), R.string.loginEmptyPassword, Toast.LENGTH_LONG).show();
                    return;
                }
                loginUser();
            }
        });
    }

    private void initEditTextes() {
        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);
    }

    private void loginUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(loginEmail.getText().toString(), loginPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    checkIfLoggedInAndIntendTeamActivity();
                    Log.d(TAG,"Successfully logged in");
                }else {
                    Log.d(TAG,"Successfully logged in");
                    Toast.makeText(getApplicationContext(),getString(R.string.loginFailed)+" \r\n"+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void handleLinkToRegisterActivity() {
        Button registerLinkButton = (Button) findViewById(R.id.registerButton);
        registerLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerActivityIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerActivityIntent);
            }
        });

    }

    //changes only the title in the toolbar
    private void checkAndHandleEasterEgg() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_main));
        final SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preference_file_key), getApplication().MODE_PRIVATE);

        boolean easterEggState = sharedPreferences.getBoolean(getString(R.string.easterEggStatus), false);
        final Button btn = (Button) findViewById(R.id.sneekyButton);
        if (easterEggState) {
            btn.setText(R.string.app_sneekyname);
        } else {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    easterEggCounter++;
                    if (easterEggCounter == 10) {
                        Toast.makeText(getApplicationContext(), getString(R.string.found_easterEgg), Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(getString(R.string.easterEggStatus), true).commit();
                        btn.setText(R.string.app_sneekyname);
                        btn.setOnClickListener(null);
                    }
                }
            });

        }
    }


    private boolean checkIfLoggedInToFirebase() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            return true;
        } else {
            return false;
        }
    }


}
