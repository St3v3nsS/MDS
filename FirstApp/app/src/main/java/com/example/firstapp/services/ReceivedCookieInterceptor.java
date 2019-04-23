package com.example.firstapp.services;

import android.preference.PreferenceManager;
import android.util.Log;

import com.example.firstapp.activities.MainActivity;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;


public class ReceivedCookieInterceptor implements Interceptor {
    private static final String PREF_COOKIE = "cookies";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = new HashSet<>();

            cookies.addAll(originalResponse.headers("Set-Cookie"));
            Log.v("OkHttp: ", "Getting headers: " + cookies);
            PreferenceManager.getDefaultSharedPreferences(MainActivity.getContext())
                    .edit()
                    .putStringSet(PREF_COOKIE, cookies)
                    .apply();
        }

        return originalResponse;
    }
}

