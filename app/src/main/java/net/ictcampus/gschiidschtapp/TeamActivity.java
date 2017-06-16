package net.ictcampus.gschiidschtapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SubMenu;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.ictcampus.gschiidschtapp.model.Team;
import net.ictcampus.gschiidschtapp.model.User;

import java.util.ArrayList;

/**
 * TeamActivity is where the magic happens. It is the main screen and includes:
 *      - team-overview:
 *          - team-name
 *          - user-name that got elected (Gschiidscht)
 *          - list of users in that team
 *          - graphical overviews of the past votes
 *
 *      - drawer-menu:
 *          - different links (create team, information)
 *          - sign off & shut down
 */


//TODO: Swap parts of this huge class to small independent parts

public class TeamActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "TeamActivity";
    private ArrayList<Team> teams = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    private Team selectedTeam;
    private DatabaseReference usersRef;
    private ArrayAdapter<User> userArrayAdapter;

    protected HorizontalBarChart overallChart;
    protected LineChart lastSixMonthsChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //drawer menu
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
        //addAllTeamsContainingUserAsNavLinks();
        userArrayAdapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1,users);

        //userlist
        ListView userListView = (ListView) findViewById(R.id.teamMemberList);
        userListView.setAdapter(userArrayAdapter);
        attachUserListViewClickHandler();

    }

    private void addAllTeamsContainingUserAsNavLinks() {
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
                    if (dataSnapshot.exists()) {
                        Log.d(TAG, "recieved some Teams");
                        teams.clear();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            if (teamIds.contains(child.getKey().toString())) {
                                teams.add((Team) child.getValue(Team.class));
                            }
                        }
                        addTeamsToNav();
                    } else {
                        Log.d(TAG, "didnt receive Teams");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    //adds the different teams a user is in to the drawer menu
    private void addTeamsToNav() {
        if (!teams.isEmpty()) {
            //get submenu to add all teams;
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            SubMenu subMenu = navigationView.getMenu().getItem(0).getSubMenu();
            subMenu.clear();
            subMenu.add(R.id.teamGroup, R.id.createTeam, Menu.NONE, R.string.createTeam).setCheckable(false).setIcon(R.drawable.ic_menu_add_team);
            for (int i = 0; i < teams.size(); i++) {
                subMenu.add(R.id.teamGroup, i, Menu.NONE, teams.get(i).getTeamName()).setCheckable(true);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        usersRef = FirebaseDatabase.getInstance().getReference(getString(R.string.db_users));
        userArrayAdapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1,users);
        ListView userListView = (ListView) findViewById(R.id.teamMemberList);
        userListView.setAdapter(userArrayAdapter);
        attachUserListViewClickHandler();
        loadAndDisplayUsernameAndUserEmailInNavMenu();
        addAllTeamsContainingUserAsNavLinks();

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
        // Handle navigation view (drawer menu) item clicks here.
        int id = item.getItemId();

        if (id == R.id.createTeam) {
            Intent createTeamIntent = new Intent(getApplicationContext(), CreateTeamActivity.class);
            startActivity(createTeamIntent);
        } else if (item.getGroupId() == R.id.teamGroup) {
            selectedTeam = teams.get(item.getItemId());
            loadTeamFromDatabase();
        } else if (id == R.id.nav_settings) {
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
        } else if (id == R.id.nav_info) {
            Intent infoIntent = new Intent(this, InfoActivity.class);
            startActivity(infoIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //content-load-in
    private void refreshTeamContent() {
        if (selectedTeam != null) {
            TextView teamName = (TextView) findViewById(R.id.teamTeamname);
            teamName.setText(selectedTeam.getTeamName());
            final TextView teamElected = (TextView) findViewById(R.id.teamCurrentElected);
            if (selectedTeam.getCurrentElected() != null) {
                usersRef.child(selectedTeam.getCurrentElected()).child(getString(R.string.db_user_name)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG,"Username for Elected from DB :"+dataSnapshot.toString());
                        if (dataSnapshot.exists()){
                            teamElected.setText(dataSnapshot.getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                teamElected.setText(R.string.teamCurrentElected);
            }

            createOverallGraph();
            createLastSixMonthsGraph();

        }
    }

    //creation of a bar-chart; not yet database connect, just a demo version
    private void createOverallGraph() {

        overallChart = (HorizontalBarChart) findViewById(R.id.overall_graph);
        overallChart.getLegend().setEnabled(false);
        overallChart.setScaleEnabled(false);

        Description description = overallChart.getDescription();
        //TODO: move this string to res values
        description.setText("overall number of times a user got elected");
        description.setTextSize(12);
        description.setTextColor(R.color.colorPrimaryDark);


        XAxis xAxis = overallChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setDrawLabels(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


        YAxis leftYAxis = overallChart.getAxisLeft();
        leftYAxis.setEnabled(true);
        YAxis rightYAxis = overallChart.getAxisRight();
        rightYAxis.setEnabled(false);


        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0f, 23));
        barEntries.add(new BarEntry(1f, 22));
        barEntries.add(new BarEntry(2f, 37));
        barEntries.add(new BarEntry(3f, 15));
        barEntries.add(new BarEntry(4f, 16));
        barEntries.add(new BarEntry(5f, 27));
        barEntries.add(new BarEntry(6f, 63));
        BarDataSet barDataSet = new BarDataSet(barEntries, "times chosen");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        final String[] users = new String[]{"Rino", "Basil", "Robin", "Silas", "Jonas", "Kirstin", "Yanick", "Test"};
        xAxis.setValueFormatter(new IndexAxisValueFormatter(users));

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet);


        BarData overallData = new BarData(dataSets);
        overallChart.setData(overallData);
        overallChart.setFitBars(true);


    }

    //creation of a line-chart; not yet database connect, just a demo version
    private void createLastSixMonthsGraph() {
        lastSixMonthsChart = (LineChart) findViewById(R.id.last_six_months_graph);
        lastSixMonthsChart.setScaleEnabled(false);

        Description description = lastSixMonthsChart.getDescription();
        //TODO: move this string to res values
        description.setText("changes in last six months' votes");
        description.setTextSize(12);
        description.setTextColor(R.color.colorPrimaryDark);

        XAxis xAxis = lastSixMonthsChart.getXAxis();
        xAxis.setEnabled(false);

        YAxis leftYAxis = lastSixMonthsChart.getAxisLeft();
        leftYAxis.setDrawAxisLine(false);
        leftYAxis.setDrawGridLines(true);

        YAxis rightYAxis = lastSixMonthsChart.getAxisRight();
        rightYAxis.setEnabled(false);

        ArrayList<Entry> yAxisBasil = new ArrayList<>();
        ArrayList<Entry> yAxisRino = new ArrayList<>();
        ArrayList<Entry> yAxisRobin = new ArrayList<>();

        yAxisBasil.add(new Entry(1f, 12f));
        yAxisBasil.add(new Entry(2f, 5f));
        yAxisBasil.add(new Entry(3f, 3f));
        yAxisBasil.add(new Entry(4f, 4f));

        yAxisRino.add(new Entry(1f, 1f));
        yAxisRino.add(new Entry(2f, 12f));
        yAxisRino.add(new Entry(3f, 39f));
        yAxisRino.add(new Entry(4f, 4f));

        yAxisRobin.add(new Entry(1f, 5f));
        yAxisRobin.add(new Entry(2f, 13f));
        yAxisRobin.add(new Entry(3f, 0f));
        yAxisRobin.add(new Entry(4f, 11f));

        LineDataSet lineDataSet1 = new LineDataSet(yAxisBasil, "Basil");
        lineDataSet1.setDrawCircles(false);
        lineDataSet1.setDrawValues(false);
        lineDataSet1.setColor(Color.BLUE);

        LineDataSet lineDataSet2 = new LineDataSet(yAxisRino, "Rino");
        lineDataSet2.setDrawCircles(false);
        lineDataSet2.setDrawValues(false);
        lineDataSet2.setColor(Color.RED);

        LineDataSet lineDataSet3 = new LineDataSet(yAxisRobin, "Robin");
        lineDataSet3.setDrawCircles(false);
        lineDataSet3.setDrawValues(false);
        lineDataSet3.setColor(Color.GREEN);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);
        dataSets.add(lineDataSet2);
        dataSets.add(lineDataSet3);


        LineData lastSixMonthsData = new LineData(dataSets);
        lastSixMonthsChart.setData(lastSixMonthsData);
        lastSixMonthsChart.invalidate();

    }


    private void attachNewElectedListenerOnSelectedTeam() {
        DatabaseReference selectedTeamDBRef = selectedTeam.getTeamDBRef(getString(R.string.db_teams)).child("currentElected");
        selectedTeamDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "refreshedTeam " + selectedTeam.getTeamName() + " got new elected: " + dataSnapshot.toString());
                if (selectedTeam.getCurrentElected()!=null&&(!dataSnapshot.getValue().toString().equals(selectedTeam.getCurrentElected()))){
                    loadTeamFromDatabase();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void attachChildChangeListenerForSelectedTeam() {
        selectedTeam.getTeamDBRef(getString(R.string.db_teams)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("FB_DATA_INCOMING", "Received Data from DB");
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

    private void loadTeamFromDatabase() {
        users.clear();
        userArrayAdapter.clear();
        selectedTeam.getTeamDBRef(getString(R.string.db_teams)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                synchronized (selectedTeam) {
                    selectedTeam = dataSnapshot.getValue(Team.class);
                }
                Log.d(TAG, "loades selected Team from Database");
                refreshTeamContent();
                attachNewElectedListenerOnSelectedTeam();
                attachChildChangeListenerForSelectedTeam();
                reveiceAllTeamMembersFromDBIntoUsersArray();
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

    private void receiveUser(final String userUid) {
        usersRef.child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "User received form DB: " + dataSnapshot.child("name").getValue().toString());
                    try {
                        users.add(dataSnapshot.getValue(User.class));
                        userArrayAdapter.notifyDataSetChanged();
                        ListView userListView = (ListView) findViewById(R.id.teamMemberList);
                        justifyListViewHightBAsedOnChildren(userListView);
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
                } else {
                    Log.d(TAG, "User not found in DB");
                }
                usersRef.child(userUid).removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void attachUserListViewClickHandler() {

        ListView userListView = (ListView) findViewById(R.id.teamMemberList);
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User userClicked = userArrayAdapter.getItem(position);
                selectedTeam.getTeamDBRef(getString(R.string.db_teams)).child(getString(R.string.db_team_currentElected)).setValue(userClicked.getUid());
            }
        });


    }

    private void justifyListViewHightBAsedOnChildren(ListView userListView) {
        ListAdapter adapter = userListView.getAdapter();
        ViewGroup vg = userListView;
        int totalHeight = 0;
        for (int i = 0; i<adapter.getCount();i++){
            View listItem = adapter.getView(i,null,vg);
            listItem.measure(0,0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams par = userListView.getLayoutParams();
        par.height = totalHeight+(userListView.getDividerHeight()*(adapter.getCount()-1));
        userListView.setLayoutParams(par);
        userListView.requestLayout();
    }

    private void reveiceAllTeamMembersFromDBIntoUsersArray() {

        Log.d(TAG, "called all TeamMemberUids form Team");
        for (String userUid : selectedTeam.getTeamMembersUid()) {
            receiveUser(userUid);
        }

    }
}


