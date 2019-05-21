package com.example.firstapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.example.firstapp.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    // This class handles the Loading screen. It will check in shared preferences if the user
    // logged in and skip the Login page, otherwise creates the Login Screen

    public static MainActivity instance;

    ProgressBar progressBar;
    private static final String PREF_COOKIE = "cookies";
    private static final String HAS_COOKIE = "has cookie";
    private static final String DETAILS = "details";
    private static final String USER = "user";
    private static final String DESC = "describe";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.loading);
        progressBar = findViewById(R.id.loading);

        getPrefs(this);

    }


    private void getPrefs(MainActivity mainActivity) {
        if (!progressBar.isShown())
            progressBar.setVisibility(View.VISIBLE);

        // getting the preferences

        SharedPreferences preferences = getSharedPreferences(PREF_COOKIE, MODE_PRIVATE);
        Set<String> cookies = preferences.getStringSet(HAS_COOKIE, new HashSet<>());
        System.out.println(cookies);
        String user  = getSharedPreferences(DETAILS, MODE_PRIVATE).getString(USER, null);
        Thread timer = new Thread(){
            @Override
            public void run() {
                try{
                    sleep(2000);    // make the progress bar looping for 2sec
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    System.out.println(cookies);
                    // if there are no cookies ---> Login
                    if(cookies != null && cookies.size() < 2 && user == null){

                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            MainActivity.this.startActivity(intent);

                    }
                    else{
                        // if there are, create the Navigation screen with preferences set
                        SharedPreferences sharedPreferences = getSharedPreferences(DETAILS, MODE_PRIVATE);
                        String user = sharedPreferences.getString(USER, null);
                        String email = sharedPreferences.getString(DESC, null);

                        Intent intent = new Intent(MainActivity.this, Navigation.class);
                        intent.putExtra("username", user);
                        intent.putExtra("password", "no-show");
                        intent.putExtra("email", email);

                        MainActivity.this.startActivity(intent);
                    }
                }
            }
        };
        timer.start();
    }

    public static Context getContext(){
        return instance;
    }

}


