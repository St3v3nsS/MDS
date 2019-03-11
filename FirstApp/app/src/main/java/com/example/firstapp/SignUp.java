package com.example.firstapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SignUp extends AppCompatActivity {

    private Profile profile;
    public  static ProgressBar progressBar;

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

        TextView login = (TextView) findViewById(R.id.alreadyLog);
        login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mainActivity = new Intent(SignUp.this, MainActivity.class);
                        SignUp.this.startActivity(mainActivity);
                    }
                }
        );

    }

    private void onSignClick() {

        CardView cardView = (CardView) findViewById(R.id.registerCardView);
        cardView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        EditText uname = (EditText) findViewById(R.id.sign_username);
                        EditText signEmail = (EditText) findViewById(R.id.sign_email);
                        EditText psw = (EditText) findViewById(R.id.sign_email);
                        String username = uname.getText().toString();
                        String email = signEmail.getText().toString();
                        String password = psw.getText().toString();

                        profile = new Profile(username, password, email);

                        progressBar = (ProgressBar) findViewById(R.id.sign_progressBar);
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        validateProfile();
                    }
                }
        );

    }

    private void validateProfile() {
        new ServerSide(profile, this).execute("https://httpstat.us/200");
    }

}

class ServerSide extends AsyncTask<String, String, String>{

    Profile profile;
    Context context;


    public ServerSide(Profile profile, Context context) {
        this.profile = profile;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        SignUp.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... strings) {
        String urlString = strings[0];
        OutputStream out = null;
        HttpURLConnection urlConnection;

        try{
            URL url = new URL(urlString);

            StringBuilder postData = new StringBuilder();
            postData.append(profile.getUsername());
            postData.append(profile.getPassword());
            postData.append(profile.getEmail());

            String data = postData.toString();
            if(data == null || data.isEmpty())
                return null;

            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            out = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(data);
            writer.flush();
            writer.close();


            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String string;

            if(urlConnection.getResponseCode() == 503){
                return null;
            }

            StringBuffer response = new StringBuffer();

            while ((string = reader.readLine()) != null)
                response.append(string);

            reader.close();
            urlConnection.disconnect();

            return response.toString();


        }catch (IOException exception){
            exception.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);

        if (SignUp.progressBar.isShown()){
            SignUp.progressBar.setVisibility(View.INVISIBLE);
        }

        if(response != null && !response.isEmpty()){

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setMessage("Account created!");
            AlertDialog alertDialog =alertBuilder.create();
            alertDialog.show();


            Intent login = new Intent(context, MainActivity.class);
            context.startActivity(login);
        }
        else{

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setMessage("EMAIL/USERNAME ALREADY IN USE!");
            AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();

            Activity activity = (Activity) context;
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }


    }
}
