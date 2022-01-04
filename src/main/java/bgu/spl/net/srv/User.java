package bgu.spl.net.srv;

public class User {
    private String userName;
    private String password;
    private String birthday;

    public User(String userName, String password, String birthday) {
        this.userName = userName;
        this.password = password;
        this.birthday = birthday;
    }

    public String getPassword() {
        return password;
    }

    public String getBirthday() {
        return birthday;
    }
    public int getIntYEAR(){
       int year = Integer.parseInt(birthday.substring(6,4));
       return year;
    }
    public int getIntMONTH(){
        int month = Integer.parseInt(birthday.substring(3,2));
        return month;
    }
    public int getIntDAY(){
        int day = Integer.parseInt(birthday.substring(0,2));
        return day;
    }

    public String getUserName() {
        return userName;
    }

}
