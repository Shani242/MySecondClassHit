package model;

public class Client {

    private String email;
    private  String password;

    public Client(){}
    public Client(String phone, String email) {
        this.password = phone;
        this.email = email;
    }

    public String getPhone() {
        return password;
    }

    public void setPhone(String phone) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
