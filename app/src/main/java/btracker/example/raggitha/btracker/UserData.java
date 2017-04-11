package btracker.example.raggitha.btracker;

import java.util.Date;

/**
 * Created by raggitha on 06-Apr-17.
 */

public class UserData {
    private String Name, Gender, Email, Team, DOB;


    public UserData(){

    }
    public UserData(String name, String DOFB, String team, String email, String gender) {
        Name = name;
        Gender = gender;
        Email = email;
        Team = team;
        this.DOB = DOFB;
    }

    public String getName() {
        return Name;
    }

    public String getGender() {
        return Gender;
    }

    public String getEmail() {
        return Email;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setTeam(String team) {
        Team = team;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getTeam() {
        return Team;
    }

    public String getDOB() {
        return this.DOB;
    }
}
