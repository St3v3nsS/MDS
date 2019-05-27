package com.example.firstapp.responses;

import com.example.firstapp.models.Profile;

import java.util.ArrayList;

public class FriendsResponse {
    // response for friends call

    private ArrayList<Profile> friends;


    public FriendsResponse(ArrayList<Profile> friends) {
        this.friends = friends;
    }

    public ArrayList<Profile> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<Profile> friends) {
        this.friends = friends;
    }
}
