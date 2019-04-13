package com.example.firstapp.interfaces;

import com.example.firstapp.LoginCredentials;
import com.example.firstapp.SignUpCredentials;
import com.example.firstapp.models.EventClass;
import com.example.firstapp.responses.AddNoteResponse;
import com.example.firstapp.responses.EventsResponse;
import com.example.firstapp.responses.FriendsResponse;
import com.example.firstapp.responses.LoginResponse;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface Api {
    @POST("login")
    Call<LoginResponse> loginWithCredentials(@Body LoginCredentials data);


    @POST("register")
    Call<LoginResponse> register(@Body SignUpCredentials data);

    @GET("user/events")
    Call<EventsResponse> getEvents();

    @PATCH("user/events/{name}")
    Call<AddNoteResponse> addEvent(@Path ("name") String name, @Body EventClass eventClass);

    @GET("user/friends")
    Call<FriendsResponse> getFriends();

    @Multipart
    @POST("user/profile_photo")
    Call<ResponseBody> uploadPhoto(@Part MultipartBody.Part image);

}
