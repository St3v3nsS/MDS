package com.example.firstapp.responses;

public class LoginResponse {
    // the login response

    private String username;
    private String email;
    private String message;

    public LoginResponse(String username, String email, String password, String message) {
        this.username = username;
        this.email = email;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
