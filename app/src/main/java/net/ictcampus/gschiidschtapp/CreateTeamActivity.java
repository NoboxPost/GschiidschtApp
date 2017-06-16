package net.ictcampus.gschiidschtapp;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.ictcampus.gschiidschtapp.model.Team;

import java.util.ArrayList;

/**
 * CreateTeamActivity is the screen that a user sees, when he wants to establish a new team. (accessible via drawer menu)
 * Also it handles the database-entries:
 *      1. add a team to the database
 *      2. add the creator-user to the team
 */

public class CreateTeamActivity extends AppCompatActivity {

    private final String TAG = "CreateTeamActivity";
    private int easterEggCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);
        checkAndHandleEasterEgg();



        Button createTeamButton = (Button) findViewById(R.id.createTeamButton);
        createTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText teamNameField = (EditText) findViewById(R.id.createTeamName);
                if (TextUtils.isEmpty(teamNameField.getText())){
                    Toast.makeText(getApplicationContext(),R.string.createTeamNameEmpty,Toast.LENGTH_SHORT).show();
                }else {
                    createNewTeam(teamNameField.getText().toString());
                }

            }
        });

    }

    private void createNewTeam(String teamName) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        DatabaseReference teamsRef = FirebaseDatabase.getInstance().getReference(getString(R.string.db_teams));
        String teamID = teamsRef.push().getKey();
        Team team = new Team(teamID,teamName,uid);
        teamsRef.child(teamID).setValue(team);
        addTeamIdToUser(teamID);

    }

    private void addTeamIdToUser(final String teamID) {
        final DatabaseReference userRef= FirebaseDatabase.getInstance().getReference(getString(R.string.db_users)).child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
        final OnCompleteListener onCompleteListener =new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
            }
        };
        userRef.child(getString(R.string.db_user_teams)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    ArrayList<String> teamsList =(ArrayList<String>) dataSnapshot.getValue();
                    teamsList.add(teamID);
                    userRef.child(getString(R.string.db_user_teams)).setValue(teamsList).addOnCompleteListener(onCompleteListener);
                }else {
                    ArrayList<String> teamsList = new ArrayList<String>();
                    teamsList.add(teamID);
                    userRef.child(getString(R.string.db_user_teams)).setValue(teamsList).addOnCompleteListener(onCompleteListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
}
