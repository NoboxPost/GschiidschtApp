package net.ictcampus.gschiidschtapp.model;

import android.content.res.Resources;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.ictcampus.gschiidschtapp.R;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A Team Object is a collection of different users such as admins and members.
 * Also it knows which member got how many times elected.
 * Team is like a database-mirror for better programming-usage.
 */

public class Team implements Serializable{

    private String teamId;
    private String teamName;
    private ArrayList<String> adminsUserUid = new ArrayList<>();
    private ArrayList<String> teamMembersUid = new ArrayList<>();
    private ArrayList<String> electionsId = new ArrayList<>();
    private String currentElection;
    private String currentElected;

    public Team() {
    }

    public Team(String teamId, String name, String uidOfFounder) {
        this.teamId = teamId;
        this.teamName = name;
        this.adminsUserUid.add(uidOfFounder);
        this.teamMembersUid.add(uidOfFounder);
    }

    public DatabaseReference getTeamDBRef(String dbTeamsReferenceName){
        return FirebaseDatabase.getInstance().getReference(dbTeamsReferenceName).child(teamId);
    }

    public void addUser(String userUid){
        teamMembersUid.add(userUid);
    }
    public void addAdmin(String userUid){
        adminsUserUid.add(userUid);
    }


    public ArrayList<String> getAdminsUserUid() {
        return adminsUserUid;
    }

    public void setAdminsUserUid(ArrayList<String> adminsUserUid) {
        this.adminsUserUid = adminsUserUid;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getCurrentElection() {
        return currentElection;
    }

    public void setCurrentElection(String currentElection) {
        this.currentElection = currentElection;
    }

    public ArrayList<String> getTeamMembersUid() {
        return teamMembersUid;
    }

    public void setTeamMembersUid(ArrayList<String> teamMembersUid) {
        this.teamMembersUid = teamMembersUid;
    }

    public ArrayList<String> getElectionsId() {
        return electionsId;
    }

    public void setElectionsId(ArrayList<String> electionsId) {
        this.electionsId = electionsId;
    }

    public String getCurrentElected() {
        return currentElected;
    }

    public void setCurrentElected(String currentElected) {
        this.currentElected = currentElected;
    }
}
