package com.example.firstapp.models;

import java.util.List;
import java.util.Set;

public class EventClass {

    private String name;
    private String startDate;
    private String endDate;
    private List<String> friends;


    public EventClass(String name, String startDate, String endDate, List<String> friends) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.friends = friends;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    @Override
    public String toString() {
        return  "Name: " + name + '\n' +
                "Start Date: " + startDate + '\n' +
                "End Date: " + endDate + '\n' +
                "Friends: " + friends;
    }
}
