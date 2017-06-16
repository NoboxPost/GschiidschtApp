package net.ictcampus.gschiidschtapp.model;

import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by vanonir on 16.06.2017.
 */
public class TeamTest {
    @Test
    public void getTeamDBRef() throws Exception {
        Team team =new Team();
        assertNotNull(team.getTeamDBRef("teams"));
    }

    @Test
    public void addUser() throws Exception {
        Team team = new Team();
        assertNotNull( team.getTeamMembersUid());
        team.addUser("Test");
        assertEquals("Test",team.getTeamMembersUid().get(0));
    }

    @Test
    public void addAdmin() throws Exception {
        Team team = new Team();
        assertNotNull( team.getTeamMembersUid());
        team.addAdmin("Test");
        assertEquals("Test",team.getAdminsUserUid().get(0));
    }

    @Test
    public void getAdminsUserUid() throws Exception {
        Team team = new Team();
        ArrayList teamAdminsUserUid= team.getAdminsUserUid();

        assertEquals(teamAdminsUserUid,team.getAdminsUserUid());
    }

}