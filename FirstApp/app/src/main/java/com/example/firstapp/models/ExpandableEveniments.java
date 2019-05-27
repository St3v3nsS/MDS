package com.example.firstapp.models;

import com.example.firstapp.interfaces.Api;
import com.example.firstapp.responses.EventsResponse;
import com.example.firstapp.services.RetrofitClient;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;

public class ExpandableEveniments {
    // the events list to set in the dashboard

    private static final HashMap<String, EventClass> eveniments = new LinkedHashMap<>();
    private static final HashMap<String, EventClass> eventsDate = new LinkedHashMap<>();

    // get the events for current date
    public static HashMap<String, EventClass> getEveniments(){

        Api retrofitClient = RetrofitClient.createService(Api.class);
        Call<EventsResponse> call = retrofitClient.getEvents();

        try {
            EventsResponse body = call.execute().body();

            // no events clear everything
            if (body == null){
                eveniments.clear();
                return eveniments;
            }
            eveniments.clear();

            // add the events received
            List<EventClass> eventClassList = body.getEvents();
            for (int i = 0; i < eventClassList.size(); i++) {
                eveniments.put("Event ~~~~~~~ " + eventClassList.get(i).getName(), eventClassList.get(i));
            }
        }catch (IOException eveniments1){
            eveniments1.printStackTrace();
        }
        return eveniments;

    }

    // get the events from specific date
    public static HashMap<String, EventClass> getEveniments(String date){

        Api retrofitClient = RetrofitClient.createService(Api.class);
        Call<EventsResponse> call = retrofitClient.getEvents(date);

        try {
            EventsResponse body = call.execute().body();

            if (body == null){
                eventsDate.clear();
                return eventsDate;
            }

            eventsDate.clear();

            List<EventClass> eventClassList = body.getEvents();
            for (int i = 0; i < eventClassList.size(); i++) {
                eventsDate.put(eventClassList.get(i).getName(), eventClassList.get(i));
            }
        }catch (IOException eveniments1){
            eveniments1.printStackTrace();
        }
        return eventsDate;

    }


    public static void addEvent(@NotNull EventClass eventClass){
        eveniments.put("Event ~~~~~~~ " + eventClass.getName(), eventClass);
    }

    public static void deleteEvent(@NotNull EventClass eventClass){
        eveniments.remove(eventClass.getName());
    }


}
