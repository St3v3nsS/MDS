package com.example.firstapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.telecom.Call;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends Activity {

    Profile profile;
    public static String txtJson;
    public static ProgressBar progressBar;

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

    protected void checkLogin(Profile profile){
        new CallApi(MainActivity.this, profile).execute("https://httpstat.us/200");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("Pausing...");
    }
}

class CallApi extends AsyncTask<String, String, String>{

    Context ctx;
    Profile profile;

    public CallApi(Context ctx, Profile profile) {
        this.ctx = ctx;
        this.profile = profile;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        MainActivity.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... strings) {
        String urlString = strings[0];
        OutputStream out = null;
        HttpURLConnection urlConnection;

        try{
            URL url = new URL(urlString);

            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(profile.getUsername());
            stringBuilder.append(profile.getPassword());

            String data = stringBuilder.toString();
            System.out.println("data is: " + data);

            /*if(data == null || data.isEmpty()) {
                System.out.println("is null");
                return data;
            }*/

            out = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(data);
            writer.flush();
            writer.close();


            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String string;

            if(urlConnection.getResponseCode() == 503)
                return null;

            StringBuffer response = new StringBuffer();

            while ((string = reader.readLine()) != null)
                response.append(string);


            reader.close();
            urlConnection.disconnect();

            return response.toString();

        }catch (Exception e){
            e.printStackTrace();
        }


        return null;

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (MainActivity.progressBar.isShown()){
            MainActivity.progressBar.setVisibility(View.INVISIBLE);
        }

        if(s != null){
            Toast.makeText(ctx, "Login successful", Toast.LENGTH_LONG).show();


            Intent dashboard = new Intent(ctx, Navigation.class);
            dashboard.putExtra("username", profile.getUsername());
            dashboard.putExtra("password", profile.getPassword());
            dashboard.putExtra("email", profile.getEmail());
            ctx.startActivity(dashboard);
        }
        else{
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);
            alertDialog.setMessage("Login failed!");

            AlertDialog alert = alertDialog.create();
            alert.show();

            Activity activity = (Activity) ctx;
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }
    }
}

