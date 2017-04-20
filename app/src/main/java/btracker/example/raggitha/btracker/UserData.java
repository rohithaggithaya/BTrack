package btracker.example.raggitha.btracker;



public class UserData {
    private String Name, Gender, Email, Team, DOB, Manager;


    public UserData(){

    }
    public UserData(String name, String DOFB, String team, String email, String gender, String manager) {
        Name = name;
        Gender = gender;
        Email = email;
        Team = team;
        this.DOB = DOFB;
        Manager = manager;
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

    public String getTeam() {
        return Team;
    }

    public String getDOB() {
        return this.DOB;
    }

    public String getManager() {
        return Manager;
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

    public void setManager(String manager) { Manager = manager; }




}
