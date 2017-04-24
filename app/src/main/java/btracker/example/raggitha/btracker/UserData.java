package btracker.example.raggitha.btracker;



public class UserData {
    private String Name, Gender, Email, Team, DOB, Manager;
    private boolean userVerified;


    public UserData(){
    }

    public UserData(String name, String DOFB, String team, String email, String gender, String manager, boolean userVerified2) {
        Name = name;
        Gender = gender;
        Email = email;
        Team = team;
        this.DOB = DOFB;
        Manager = manager;
        userVerified = userVerified2;
    }

    public String getName() {
        return Name;
    }

    public String getGender() {
        return Gender;
    }

    public boolean getUserVerified(){ return userVerified;}

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

    public void setUserVerified(boolean verified) { userVerified = verified; }

    public void setTeam(String team) {
        Team = team;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public void setManager(String manager) { Manager = manager; }




}
