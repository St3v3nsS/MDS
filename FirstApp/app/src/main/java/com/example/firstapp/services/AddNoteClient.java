package com.example.firstapp.services;

import android.support.v4.app.FragmentTransaction;

import com.example.firstapp.callbacks.AddNoteCallback;
import com.example.firstapp.interfaces.Api;
import com.example.firstapp.menuActivities.AddNote;
import com.example.firstapp.models.EventClass;
import com.example.firstapp.responses.AddNoteResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNoteClient {
    // This class handles the add event action
    public static void getResponse(final AddNote myNote, final AddNoteCallback callback, EventClass event,
                                   final FragmentTransaction fragmentTransaction){
        Api patchEvent = RetrofitClient.createService(Api.class);
        Call<AddNoteResponse> call = patchEvent.addEvent(event);
        call.enqueue(new Callback<AddNoteResponse>() {
            @Override
            public void onResponse(Call<AddNoteResponse> call, Response<AddNoteResponse> response) {
                callback.onResponse(myNote, fragmentTransaction); // refresh after added new event

            }

            @Override
            public void onFailure(Call<AddNoteResponse> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
            }
        });
    }
}
