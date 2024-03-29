package com.example.firstapp.interfaces;

import com.example.firstapp.models.CodeChecker;
import com.example.firstapp.models.EventClass;
import com.example.firstapp.models.Profile;
import com.example.firstapp.models.ResetPassword;
import com.example.firstapp.models.StringBody;
import com.example.firstapp.responses.AddNoteResponse;
import com.example.firstapp.responses.EventsResponse;
import com.example.firstapp.responses.FriendsResponse;
import com.example.firstapp.responses.HoursResponse;
import com.example.firstapp.responses.LoginResponse;
import com.example.firstapp.responses.PhotoResponse;
import com.example.firstapp.responses.UsersResponse;
import com.example.firstapp.services.LoginCredentials;
import com.example.firstapp.services.SignUpCredentials;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Api {
    // This is the API interface which makes calls to the server
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
    @GET("events")
    Call<EventsResponse> getEvents();

    @Headers({
            "key: Content-Type",
            "value: application/json",
            "description: \"\""
    })
    @GET("events/")
    Call<EventsResponse> getEvents(@Query("date") String date);

    @Headers({
            "key: Content-Type",
            "value: application/json",
            "description: \"\""
    })
    @POST("events")
    Call<AddNoteResponse> addEvent(@Body EventClass eventClass);

    @Headers({
            "key: Content-Type",
            "value: application/json",
            "description: \"\""
    })
    @POST("events/del")
    Call<AddNoteResponse> deleteEvent(@Body EventClass eventClass);

    @Headers({
            "key: Content-Type",
            "value: application/json",
            "description: \"\""
    })
    @GET("friends")
    Call<FriendsResponse> getFriends();

    @Headers({
            "key: Content-Type",
            "value: application/json",
            "description: \"\""
    })
    @POST("friends")
    Call<AddNoteResponse> addFriend(@Body Profile friend);

    @Headers({
            "key: Content-Type",
            "value: application/json",
            "description: \"\""
    })
    @POST("friends/del")
    Call<AddNoteResponse> deleteFriend(@Body Profile friend);

    @Headers({
            "key: Content-Type",
            "value: application/json",
            "description: \"\""
    })
    @Multipart
    @POST("profile_photo")
    Call<StringBody> uploadPhoto(@Part MultipartBody.Part image);

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
    Call<AddNoteResponse> checkResetToken(@Body CodeChecker token);

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
    @GET("match_friends/")
    Call<HoursResponse> matchFriend(@Query("name") String friendName);

    @Headers({
            "key: Content-Type",
            "value: application/json",
            "description: \"\""
    })
    @GET("profile_photo")
    Call<PhotoResponse> getProfilePhoto();


    @Headers({
            "key: Content-Type",
            "value: application/json",
            "description: \"\""
    })
    @POST("logout")
    Call<ResponseBody> logout();


}
