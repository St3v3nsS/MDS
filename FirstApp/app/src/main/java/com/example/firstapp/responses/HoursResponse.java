package com.example.firstapp.responses;

import java.util.ArrayList;

public class HoursResponse {

    private ArrayList<String> hours;


    public HoursResponse(ArrayList<String> hours) {
        this.hours = hours;
    }

    public ArrayList<String> getHours() {
        return hours;
    }

    public void setHours(ArrayList<String> hours) {
        this.hours = hours;
    }
}
