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

    private static final HashMap<String, EventClass> eveniments = new LinkedHashMap<>();


    public static HashMap<String, EventClass> getEveniments(){

        Api retrofitClient = RetrofitClient.createService(Api.class);
        Call<EventsResponse> call = retrofitClient.getEvents();

        try {
            EventsResponse body = call.execute().body();

            if (body == null){
                eveniments.put("No events yet!", new EventClass("Try add a note"));
                return eveniments;
            }

            List<EventClass> eventClassList = body.getEvents();
            for (int i = 0; i < eventClassList.size(); i++) {
                eveniments.put("Event ~~~~~~~ " + eventClassList.get(i).getName(), eventClassList.get(i));
            }
        }catch (IOException eveniments1){
            eveniments1.printStackTrace();
        }
        return eveniments;

    }

    public static void addEvent(@NotNull EventClass eventClass){
        eveniments.put("Event ~~~~~~~ " + eventClass.getName(), eventClass);
    }

    public static void deleteEvent(@NotNull EventClass eventClass){
        eveniments.remove(eventClass.getName());
    }


}
