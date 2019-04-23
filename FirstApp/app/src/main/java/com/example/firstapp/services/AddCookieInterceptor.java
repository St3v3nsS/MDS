package com.example.firstapp.services;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.firstapp.activities.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddCookieInterceptor implements Interceptor {

    private static final String PREF_COOKIE = "cookies";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContext());
        Set<String> cookies = preferences.getStringSet(PREF_COOKIE, new HashSet<>());
        for (String cookie : cookies) {
            builder.addHeader("Cookie", cookie);
            Log.v("OkHttp", "Adding Header: " + cookie); // This is done so I know which headers are being added; this interceptor is used after the normal logging of OkHttp
        }

        return chain.proceed(builder.build());
    }
}
