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

    public String getUserName() {
        return userName;
    }
}
