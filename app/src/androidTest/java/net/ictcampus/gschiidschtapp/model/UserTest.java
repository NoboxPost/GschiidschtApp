package net.ictcampus.gschiidschtapp.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by vanonir on 16.06.2017.
 */
public class UserTest {


    @Test
    public void toStringTest() {
        User user = new User();
        user.setName("Test");
        user.setEmail("test@test");
        assertEquals("Test test@test",user.toString());
    }

    @Test
    public void getEmail() throws Exception {
        User user = new User();
        assertNull(user.getEmail());
        user.setEmail("test");
        assertEquals("test",user.getEmail());
    }

    @Test
    public void setEmail() throws Exception {
        User user = new User();
        assertNull(user.getEmail());
        user.setEmail("test");
        assertEquals("test",user.email);
    }

    @Test
    public void getName() throws Exception {
        User user = new User();
        assertNull(user.getName());
    }

    @Test
    public void setName() throws Exception {
        User user = new User();
        user.setName("test");
        assertEquals("test",user.name);
    }

    @Test
    public void getTeams() throws Exception {
        User user = new User();
        assertNull(user.getTeams());
    }

    @Test
    public void setTeams() throws Exception {
        User user = new User();
        ArrayList arrayList= new ArrayList<String>();
        arrayList.add("Test");
        user.setTeams(arrayList);
        assertNotNull(user.teams);
        assertEquals(arrayList,user.teams);
    }

    @Test
    public void getUid() throws Exception {
        User user = new User();
        assertNull(user.getUid());
        user.setUid("test");
        assertEquals("test",user.getUid());
    }

    @Test
    public void setUid() throws Exception {
        User user = new User();
        user.setUid("test");
        assertEquals("test",user.uid);
    }

}