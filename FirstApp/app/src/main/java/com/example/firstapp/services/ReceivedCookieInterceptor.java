package com.example.firstapp.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.firstapp.activities.LoginActivity;
import com.example.firstapp.activities.MainActivity;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Response;


public class ReceivedCookieInterceptor implements Interceptor {
    private static final String PREF_COOKIE = "cookies";
    private static final String HAS_COOKIE = "has cookie";


    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = new HashSet<>();

            cookies.addAll(originalResponse.headers("Set-Cookie"));
            System.out.println(originalResponse.header("Set-Cookie"));
            Log.v("OkHttp: ", "Getting headers: " + cookies);
            SharedPreferences preferences = MainActivity.getContext().getSharedPreferences(PREF_COOKIE, Context.MODE_PRIVATE);
            preferences
                    .edit()
                    .putStringSet(HAS_COOKIE, cookies)
                    .apply();

        }

        return originalResponse;
    }
}

