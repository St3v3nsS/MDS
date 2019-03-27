package com.example.firstapp.interfaces;

import com.example.firstapp.LoginCredentials;
import com.example.firstapp.SignUpCredentials;
import com.example.firstapp.models.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Api {
    @POST("login")
    Call<LoginResponse> loginWithCredentials(@Body LoginCredentials data);


    @POST("register")
    Call<LoginResponse> register(@Body SignUpCredentials data);
}
