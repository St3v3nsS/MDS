package com.example.firstapp.services;

import com.google.gson.annotations.SerializedName;

public class LoginCredentials {

    private String username;
    private String password;

    @SerializedName("remember-me")
    private String rememberMe;

    public LoginCredentials(String username, String password) {
        this.username = username;
        this.password = password;
        this.rememberMe = "true";

    }
}
