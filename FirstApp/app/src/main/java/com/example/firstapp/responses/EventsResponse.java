package com.example.firstapp.responses;

import com.example.firstapp.models.EventClass;

import java.util.List;

public class EventsResponse {
    // the event response

    private List<EventClass> events;

    public List<EventClass> getEvents() {
        return events;
    }
}
