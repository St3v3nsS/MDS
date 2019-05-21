package com.example.firstapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firstapp.R;
import com.example.firstapp.interfaces.Api;
import com.example.firstapp.responses.LoginResponse;
import com.example.firstapp.models.Profile;
import com.example.firstapp.services.RetrofitClient;
import com.example.firstapp.services.SignUpCredentials;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {
    // This class handles the signup activity by collecting all the data and sending it to the server

    private Profile profile;
    public ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sign_up);

        onSignClick();
        onLoginClick();
    }

    private void onLoginClick() {
        // if the user has already an account --> Login Activity
        TextView login = (TextView) findViewById(R.id.alreadyLog);
        login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mainActivity = new Intent(SignUp.this, LoginActivity.class);
                        SignUp.this.startActivity(mainActivity);
                    }
                }
        );

    }

    private void onSignClick() {
        // Collecting all the data for creating a new user
        CardView cardView = (CardView) findViewById(R.id.registerCardView);
        cardView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        EditText uname = (EditText) findViewById(R.id.sign_username);
                        EditText signEmail = (EditText) findViewById(R.id.sign_email);
                        EditText psw = (EditText) findViewById(R.id.sign_password);
                        String username = uname.getText().toString();
                        String email = signEmail.getText().toString();
                        String password = psw.getText().toString();

                        profile = new Profile(username, password, email);

                        System.out.println(profile);
                        progressBar = (ProgressBar) findViewById(R.id.sign_progressBar);
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        validateProfile();
                    }
                }
        );

    }

    private void validateProfile() {
        // make an API call to validate the data

        Api signupApi = RetrofitClient.createService(Api.class);
        final Call<LoginResponse> responseCall = signupApi.register(new SignUpCredentials(profile.getUsername(), profile.getEmail(), profile.getPassword()));
        progressBar.setVisibility(View.VISIBLE);
        responseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (progressBar.isShown()) {
                    progressBar.setVisibility(View.INVISIBLE);
                }

                if (response.code() == 200) {

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SignUp.this);
                    alertBuilder.setMessage("Account created!");
                    AlertDialog alertDialog = alertBuilder.create();
                    alertDialog.show();

                    // go to login Activity if the account was created
                    Intent login = new Intent(SignUp.this, LoginActivity.class);
                    SignUp.this.startActivity(login);
                }

                else{
                    // try again otherwise
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SignUp.this);
                    alertBuilder.setMessage("EMAIL/USERNAME ALREADY IN USE!");
                    AlertDialog alertDialog = alertBuilder.create();
                    alertDialog.show();

                    Activity activity = SignUp.this;
                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

                if (progressBar.isShown()) {
                    progressBar.setVisibility(View.INVISIBLE);
                }

                responseCall.cancel();
                Toast.makeText(SignUp.this, "Signup failed", Toast.LENGTH_LONG).show();

                t.printStackTrace();

                Activity activity = SignUp.this;
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });

    }

}