package com.example.firstapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.firstapp.R;
import com.example.firstapp.interfaces.Api;
import com.example.firstapp.models.CodeChecker;
import com.example.firstapp.models.ResetPassword;
import com.example.firstapp.models.StringBody;
import com.example.firstapp.responses.AddNoteResponse;
import com.example.firstapp.services.RetrofitClient;

import org.w3c.dom.Text;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.Toast.LENGTH_SHORT;

public class ForgotPassword extends AppCompatActivity {
    /* This class handles the forgot password functionality. It is implemented using ViewFlipper*/

    private ViewFlipper viewFlipper;
    private String emailS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
        checkEmail();
    }

    private void checkEmail() {
        // First, enter a valid email for sending the reset code
        View view = viewFlipper.getCurrentView();
        Button send = (Button) view.findViewById(R.id.send_token);
        EditText email = (EditText) view.findViewById(R.id.email_forgot);

        send.setOnClickListener(v->{
            TextView textView = (TextView)findViewById(R.id.check_email_code);
            textView.setVisibility(View.VISIBLE);
            // Making an Api call to the server to send the code to email
            Api sendEmail = RetrofitClient.createService(Api.class);
            emailS = email.getText().toString();
            Call<AddNoteResponse> call = sendEmail.forgotPassword(new StringBody(email.getText().toString()));
            call.enqueue(new Callback<AddNoteResponse>() {
                @Override
                public void onResponse(Call<AddNoteResponse> call, Response<AddNoteResponse> response) {
                    System.out.println(response.code());
                    System.out.println(response.body());
                    if (response.code() == 200){
                        changeDisplayToCode(); // go to next View --- > code sent to email
                    }else {
                        Toast.makeText(ForgotPassword.this, "Some error occurred. Try again!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<AddNoteResponse> call, Throwable t) {
                    textView.setVisibility(View.INVISIBLE);
                    call.cancel();
                    Toast.makeText(ForgotPassword.this, "Some error occurred. Try again!", Toast.LENGTH_LONG).show();
                }
            });

        });


    }

    private void changeDisplayToCode() {
        // type the received code
        viewFlipper.setDisplayedChild(1);
        View view = viewFlipper.getCurrentView();

        EditText code = (EditText) view.findViewById(R.id.received_token);

        Button check = (Button) view.findViewById(R.id.check_valid);

        check.setOnClickListener(v->{
            Api api = RetrofitClient.createService(Api.class);
            Call<AddNoteResponse> call = api.checkResetToken(new CodeChecker(emailS, code.getText().toString()));
            call.enqueue(new Callback<AddNoteResponse>() {
                @Override
                public void onResponse(Call<AddNoteResponse> call, Response<AddNoteResponse> response) {
                    if(response.code() == 200){
                        changeDisplayToNewPassword(); // code correct ----> set new password
                    }else {
                        Toast.makeText(ForgotPassword.this, "Try again...", LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AddNoteResponse> call, Throwable t) {
                    Toast.makeText(ForgotPassword.this, "Some error occurred. Try again!", Toast.LENGTH_LONG).show();
                    call.cancel();
                }
            });
        });

    }

    private void changeDisplayToNewPassword() {
        // setting the new password
        viewFlipper.setDisplayedChild(2);
        View view = viewFlipper.getCurrentView();

        EditText pass = (EditText) view.findViewById(R.id.new_pass);
        EditText confPass = (EditText) view.findViewById(R.id.confirm_new_pass);

        Button done = (Button) view.findViewById(R.id.renew_password);

        done.setOnClickListener(v->{
            ResetPassword body = new ResetPassword(emailS, pass.getText().toString(), confPass.getText().toString());

            Api api = RetrofitClient.createService(Api.class);
            Call<AddNoteResponse> call = api.newPassword(body);

            call.enqueue(new Callback<AddNoteResponse>() {
                @Override
                public void onResponse(Call<AddNoteResponse> call, Response<AddNoteResponse> response) {
                    if (response.code() == 200){
                        // password changed successfully
                        viewFlipper.setDisplayedChild(0);
                        Toast.makeText(ForgotPassword.this, "Password Changed Successful", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(ForgotPassword.this, LoginActivity.class); // ---> Login View
                        ForgotPassword.this.startActivity(intent);
                    }
                    else{
                        Toast.makeText(ForgotPassword.this, "Some error occurred. Try again!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<AddNoteResponse> call, Throwable t) {
                    call.cancel();
                    Toast.makeText(ForgotPassword.this, "Some error occurred. Try again!", Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
