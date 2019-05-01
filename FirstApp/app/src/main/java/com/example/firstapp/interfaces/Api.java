package com.example.firstapp.interfaces;

import com.example.firstapp.SignUpCredentials;
import com.example.firstapp.models.EventClass;
import com.example.firstapp.models.ResetPassword;
import com.example.firstapp.models.StringBody;
import com.example.firstapp.responses.AddNoteResponse;
import com.example.firstapp.responses.EventsResponse;
import com.example.firstapp.responses.FriendsResponse;
import com.example.firstapp.responses.LoginResponse;
import com.example.firstapp.responses.UsersResponse;
import com.example.firstapp.services.LoginCredentials;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface Api {
    @Headers({
            "key: Content-Type",
            "value: application/json",
            "description: \"\""
    })
    @POST("login")
    Call<LoginResponse> loginWithCredentials(@Body LoginCredentials data);

    @Headers({
            "key: Content-Type",
            "value: application/json",
            "description: \"\""
    })
    @POST("register")
    Call<LoginResponse> register(@Body SignUpCredentials data);

    @Headers({
            "key: Content-Type",
            "value: application/json",
            "description: \"\""
    })
    @GET("user/events")
    Call<EventsResponse> getEvents();

    @Headers({
            "key: Content-Type",
            "value: application/json",
            "description: \"\""
    })
    @PATCH("user/events/{name}")
    Call<AddNoteResponse> addEvent(@Path ("name") String name, @Body EventClass eventClass);

    @Headers({
            "key: Content-Type",
            "value: application/json",
            "description: \"\""
    })
    @GET("user/friends")
    Call<FriendsResponse> getFriends();

    @Headers({
            "key: Content-Type",
            "value: application/json",
            "description: \"\""
    })
    @PATCH("user/friends/{name}")
    Call<ResponseBody> addFriend(@Path("name") String name);

    @Headers({
            "key: Content-Type",
            "value: application/json",
            "description: \"\""
    })
    @Multipart
    @POST("user/profile_photo")
    Call<ResponseBody> uploadPhoto(@Part MultipartBody.Part image);

    @Headers({
            "key: Content-Type",
            "value: application/json",
            "description: \"\""
    })
    @GET("users")
    Call<UsersResponse> getAllUsers();

    @Headers({
            "key: Content-Type",
            "value: application/json",
            "description: \"\""
    })
    @POST("forgot_password")
    Call<AddNoteResponse> forgotPassword(@Body StringBody email);


    @Headers({
            "key: Content-Type",
            "value: application/json",
            "description: \"\""
    })
    @POST("check_reset_token")
    Call<AddNoteResponse> checkResetToken(@Body StringBody token);

    @Headers({
            "key: Content-Type",
            "value: application/json",
            "description: \"\""
    })
    @POST("new_password")
    Call<AddNoteResponse> newPassword(@Body ResetPassword password);

    @Headers({
            "key: Content-Type",
            "value: application/json",
            "description: \"\""
    })
    @POST("logout")
    Call<ResponseBody> logout();
}
