package com.example.firstapp.models;

public class ResetPassword {

    private String password;
    private String confirmPassword;

    public ResetPassword(String password, String confirmPassword){
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public String getPassword() {
        return password;
    }


}
