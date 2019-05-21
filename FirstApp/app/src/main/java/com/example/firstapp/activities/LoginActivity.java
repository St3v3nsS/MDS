package com.example.firstapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.example.firstapp.models.Profile;
import com.example.firstapp.responses.LoginResponse;
import com.example.firstapp.services.LoginCredentials;
import com.example.firstapp.services.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends Activity {
    // This class handles the Login Activity by checking the credentials or navigating through
    // sign up or forgot password
    private static final String DETAILS = "details";
    private static final String USER = "user";
    private static final String DESC = "describe";
    private static final String PASSWORD = "password";
    Profile profile;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting the full screen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // Handle the clicks
        onLoginClick(); // login button -- > Check credentials
        onSignupClick(); // signup button ---> new Activity ( Signup)
        onForgotClick();  // forgot password text --> new Activity

    }

    private void onForgotClick() {

        // this creates a new instance of ForgotPassword

        TextView forgot = (TextView) findViewById(R.id.forgot_pass);

        forgot.setOnClickListener(v->{
            Intent forgotIntent = new Intent(this, ForgotPassword.class);
            this.startActivity(forgotIntent);
        });

    }

    private void onSignupClick() {

        // this creates a new instance of SignUp

        TextView signup = (TextView) findViewById(R.id.register);
        signup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent signUpIntent = new Intent(LoginActivity.this, SignUp.class);
                        LoginActivity.this.startActivity(signUpIntent);
                    }
                }
        );
    }

    public void onLoginClick() {

        CardView cardView = (CardView)findViewById(R.id.cardView);
        cardView.setOnClickListener(    // getting the Login Button
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  Get username and pass from View
                        String username, password;
                        EditText uname = (EditText) findViewById(R.id.username);
                        EditText psw = (EditText) findViewById(R.id.password);
                        username = uname.getText().toString();
                        password = psw.getText().toString();

                        profile = new Profile(username, password, "username@domain.com");

                        progressBar = (ProgressBar) findViewById(R.id.loading); //  Get The progressBar
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE); //  Block user Interaction

                        //  check login Credentials
                        checkLogin(profile);

                    }

                }

        );

    }

    protected void checkLogin(final Profile profile){
        // Making an API call to the server

        Api loginClient = RetrofitClient.createService(Api.class);
        Call<LoginResponse> call = loginClient.loginWithCredentials(new LoginCredentials(profile.getUsername(), profile.getPassword()));

        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                // hide the progress bar
                if (progressBar.isShown()){
                    progressBar.setVisibility(View.INVISIBLE);
                }

                if(response.code() == 200){
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_LONG).show();

                    // Creating the Navigation View with the user details
                    Intent dashboard = new Intent(LoginActivity.this, Navigation.class);
                    dashboard.putExtra("username", profile.getUsername());
                    dashboard.putExtra("password", profile.getPassword());
                    dashboard.putExtra("email", response.body().getEmail());

                    SharedPreferences sharedPreferences = getSharedPreferences(DETAILS, MODE_PRIVATE);
                    sharedPreferences.edit()
                            .putString(USER, profile.getUsername())
                            .apply();

                    sharedPreferences.edit()
                            .putString(PASSWORD, profile.getPassword())
                            .apply();

                    sharedPreferences.edit()
                            .putString(DESC, response.body().getEmail())
                            .apply();

                    LoginActivity.this.startActivity(dashboard);
                }
                else{
                    System.out.println(response.code());
                    // retry
                    Activity activity = LoginActivity.this;
                    Toast.makeText(activity, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();

                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

                if (progressBar.isShown()){
                    progressBar.setVisibility(View.INVISIBLE);
                }

                // make an alert Dialog for a failed login
                call.cancel();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
                alertDialog.setMessage("Login failed!");

                AlertDialog alert = alertDialog.create();
                alert.show();

                Activity activity = LoginActivity.this;
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("Pausing...");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
