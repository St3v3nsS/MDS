package com.example.firstapp.models;

public class StringBody {
    // simple string class for simple responses

    private String email;

    public StringBody(String string) {
        this.email = string;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
