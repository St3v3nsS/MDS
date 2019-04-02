package com.example.firstapp.models;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class Profile implements Serializable {

    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private String email;


    public Profile(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Profile() {
    }

    @Override
    public String toString() {
        return "Profile{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
