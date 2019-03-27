package com.example.firstapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firstapp.LoginCredentials;
import com.example.firstapp.R;
import com.example.firstapp.interfaces.Api;
import com.example.firstapp.models.LoginResponse;
import com.example.firstapp.models.Profile;
import com.example.firstapp.services.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends Activity {

    Profile profile;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        onLoginClick();
        onSignupClick();

    }

    private void onSignupClick() {

        TextView signup = (TextView) findViewById(R.id.register);
        signup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent signUpIntent = new Intent(MainActivity.this, SignUp.class);
                        MainActivity.this.startActivity(signUpIntent);
                    }
                }
        );
    }

    public void onLoginClick() {

        CardView cardView = (CardView)findViewById(R.id.cardView);
        cardView.setOnClickListener(
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

        Api loginClient = RetrofitClient.createService(Api.class);
        Call<LoginResponse> call = loginClient.loginWithCredentials(new LoginCredentials(profile.getUsername(), profile.getPassword()));

        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (progressBar.isShown()){
                    progressBar.setVisibility(View.INVISIBLE);
                }

                if(response.code() == 200){
                    Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_LONG).show();


                    Intent dashboard = new Intent(MainActivity.this, Navigation.class);
                    dashboard.putExtra("username", profile.getUsername());
                    dashboard.putExtra("password", profile.getPassword());
                    dashboard.putExtra("email", response.body().getEmail());
                    MainActivity.this.startActivity(dashboard);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

                if (progressBar.isShown()){
                    progressBar.setVisibility(View.INVISIBLE);
                }

                call.cancel();

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setMessage("Login failed!");

                AlertDialog alert = alertDialog.create();
                alert.show();

                Activity activity = MainActivity.this;
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("Pausing...");
    }
}
