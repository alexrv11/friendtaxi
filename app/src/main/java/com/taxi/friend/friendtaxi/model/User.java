package com.taxi.friend.friendtaxi.model;

public class User {

    private String email;
    private String password;
    private String name;
    private String phone;

    public User() {

    }

    public User(String email, String password, String name, String phone) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPHone() {
        return phone;
    }

    public void setPHone(String phone) {
        this.phone = phone;
    }
}
