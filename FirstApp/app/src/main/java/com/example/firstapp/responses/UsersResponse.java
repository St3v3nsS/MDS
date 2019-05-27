package com.example.firstapp.responses;

import com.example.firstapp.models.Profile;

import java.util.ArrayList;

public class UsersResponse {
    // all users response

    private ArrayList<Profile> users;

    public UsersResponse(ArrayList<Profile> users) {
        this.users = users;
    }

    public ArrayList<Profile> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<Profile> users) {
        this.users = users;
    }
}
