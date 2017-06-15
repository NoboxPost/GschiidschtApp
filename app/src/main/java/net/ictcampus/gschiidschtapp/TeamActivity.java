package net.ictcampus.gschiidschtapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.ictcampus.gschiidschtapp.model.Team;
import net.ictcampus.gschiidschtapp.model.User;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TeamActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "TeamActivity";
    private ArrayList<Team> teams = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    private Team selectedTeam;
    private DatabaseReference usersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //end intent if no user is logged in
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
        }
        //addAllTeamsWithUserAsNavLinks();


//        Toast.makeText(getApplicationContext(),FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),Toast.LENGTH_LONG).show();

    }

    private void addAllTeamsWithUserAsNavLinks() {
        //Add All Teams with currentUser as member;
        final ArrayList<String> teamIds = new ArrayList<>();


        DatabaseReference userTeamsRef = FirebaseDatabase.getInstance().getReference(getString(R.string.db_users)).child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child(getString(R.string.db_user_teams));
        userTeamsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "Received user teamIds" + dataSnapshot.toString());
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        teamIds.add(child.getValue().toString());
                    }
                    getAllTeamsIntoTeamsArray(teamIds);
                } else {
                    Log.d(TAG, "Didnt get any user teamIds");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getAllTeamsIntoTeamsArray(final ArrayList<String> teamIds) {
        if (!teamIds.isEmpty()) {
            DatabaseReference teamsRef = FirebaseDatabase.getInstance().getReference(getString(R.string.db_teams));
            teamsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        Log.d(TAG,"recieved some Teams");
                        teams.clear();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            if (teamIds.contains(child.getKey().toString())){
                                teams.add((Team)child.getValue(Team.class));
                            }
                        }
                        addTeamsToNav();
                    }else {
                        Log.d(TAG,"didnt receive Teams");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void addTeamsToNav() {
        if (!teams.isEmpty()){
            //get submenu to add all teams;
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            SubMenu subMenu = navigationView.getMenu().getItem(0).getSubMenu();
            subMenu.clear();
            subMenu.add(R.id.teamGroup,R.id.createTeam,Menu.NONE,R.string.createTeam).setCheckable(false).setIcon(R.drawable.ic_menu_add_team);
            for (int i  = 0 ; i< teams.size();i++){
                subMenu.add(R.id.teamGroup,i,Menu.NONE,teams.get(i).getTeamName()).setCheckable(true);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        usersRef = FirebaseDatabase.getInstance().getReference(getString(R.string.db_users));
        loadAndDisplayUsernameAndUserEmailInNavMenu();
        addAllTeamsWithUserAsNavLinks();

    }

    private void loadAndDisplayUsernameAndUserEmailInNavMenu() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(getString(R.string.db_users)).child(mAuth.getCurrentUser().getUid().toString());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //log
                    Log.d(TAG, dataSnapshot.toString());
                    //accessing the username TextView
                    if (dataSnapshot.exists()) {
                        //setting Text by getting username out of DatabaseSnapshot
                        if (dataSnapshot.child(getString(R.string.db_user_name)).exists()) {
                            TextView username = (TextView) findViewById(R.id.teamUsername);
                            username.setText(dataSnapshot.child(getString(R.string.db_user_name)).getValue().toString());
                        }
                        //same for Email
                        if (dataSnapshot.child(getString(R.string.db_user_email)).exists()) {
                            TextView userEmail = (TextView) findViewById(R.id.teamUserEmail);
                            userEmail.setText(dataSnapshot.child(getString(R.string.db_user_email)).getValue().toString());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.team, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.createTeam) {
            Intent createTeamIntent = new Intent(getApplicationContext(), CreateTeamActivity.class);
            startActivity(createTeamIntent);
        } else if (item.getGroupId() == R.id.teamGroup) {
            selectedTeam = teams.get(item.getItemId());
            loadTeamFromDatabase();
        }
        if (id == R.id.nav_settings) {
            //TODO: INTENT To SEttings Acitivity;
        } else if (id == R.id.nav_logout) {

            //logout in Firebase
            FirebaseAuth.getInstance().signOut();

            //reset dr Dümmscht header
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preference_file_key), getApplication().MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.easterEggStatus), false).apply();

            //end intent
            finishAffinity();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void refreshTeamContent() {
        if (selectedTeam!=null) {
            TextView teamName = (TextView) findViewById(R.id.teamTeamname);
            teamName.setText(selectedTeam.getTeamName());
            TextView teamElected = (TextView) findViewById(R.id.teamCurrentElected);
            if (selectedTeam.getCurrentElected() != null) {
                teamElected.setText(selectedTeam.getCurrentElected());
            } else {
                teamElected.setText(R.string.teamCurrentElected);
            }




        }
    }

    private void attachNewElectedListenerOnSelectedTeam() {
        DatabaseReference selectedTeamDBRef = selectedTeam.getTeamDBRef(getString(R.string.db_teams)).child("currentElected");
        selectedTeamDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,"refreshedTeam "+selectedTeam.getTeamName()+" got new elected: "+dataSnapshot.toString());
                loadTeamFromDatabase();
                refreshTeamContent();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void attachChildChangeListenerForSelectedTeam(){
        selectedTeam.getTeamDBRef(getString(R.string.db_teams)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("FB_DATA_INCOMING","Received Data from DB");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadTeamFromDatabase(){
        users.clear();
        selectedTeam.getTeamDBRef(getString(R.string.db_teams)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                synchronized (selectedTeam) {
                    selectedTeam = dataSnapshot.getValue(Team.class);
                }
                Log.d(TAG,"loades selected Team from Database");
                refreshTeamContent();
                attachNewElectedListenerOnSelectedTeam();
                attachChildChangeListenerForSelectedTeam();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        FirebaseAuth.getInstance().signOut();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        FirebaseAuth.getInstance().signOut();
    }

    private void receiveUser(String UserUid){
        usersRef.child(UserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}


