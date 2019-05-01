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

    private ViewFlipper viewFlipper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
        checkEmail();
    }

    private void checkEmail() {
        View view = viewFlipper.getCurrentView();
        Button send = (Button) view.findViewById(R.id.send_token);
        EditText email = (EditText) view.findViewById(R.id.email_forgot);

        send.setOnClickListener(v->{
            TextView textView = (TextView)findViewById(R.id.check_email_code);
            textView.setVisibility(View.VISIBLE);
            Api sendEmail = RetrofitClient.createService(Api.class);
            Call<AddNoteResponse> call = sendEmail.forgotPassword(new StringBody(email.getText().toString()));
            call.enqueue(new Callback<AddNoteResponse>() {
                @Override
                public void onResponse(Call<AddNoteResponse> call, Response<AddNoteResponse> response) {
                    if (response.code() == 200){
                        changeDisplayToCode();
                    }
                }

                @Override
                public void onFailure(Call<AddNoteResponse> call, Throwable t) {
                    textView.setVisibility(View.INVISIBLE);
                    call.cancel();
                }
            });

        });


    }

    private void changeDisplayToCode() {
        viewFlipper.setDisplayedChild(1);
        View view = viewFlipper.getCurrentView();

        EditText code = (EditText) view.findViewById(R.id.received_token);

        Button check = (Button) view.findViewById(R.id.check_valid);

        check.setOnClickListener(v->{
            Api api = RetrofitClient.createService(Api.class);
            Call<AddNoteResponse> call = api.checkResetToken(new StringBody(code.getText().toString()));
            call.enqueue(new Callback<AddNoteResponse>() {
                @Override
                public void onResponse(Call<AddNoteResponse> call, Response<AddNoteResponse> response) {
                    if(response.code() == 200){
                        changeDisplayToNewPassword();
                    }else {
                        Toast.makeText(ForgotPassword.this, "Try again...", LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AddNoteResponse> call, Throwable t) {
                    call.cancel();
                }
            });
        });

    }

    private void changeDisplayToNewPassword() {
        viewFlipper.setDisplayedChild(2);
        View view = viewFlipper.getCurrentView();

        EditText pass = (EditText) view.findViewById(R.id.new_pass);
        EditText confPass = (EditText) view.findViewById(R.id.confirm_new_pass);

        ResetPassword body = new ResetPassword(pass.getText().toString(), confPass.getText().toString());

        Button done = (Button) view.findViewById(R.id.renew_password);

        done.setOnClickListener(v->{
            Api api = RetrofitClient.createService(Api.class);
            Call<AddNoteResponse> call = api.newPassword(body);

            call.enqueue(new Callback<AddNoteResponse>() {
                @Override
                public void onResponse(Call<AddNoteResponse> call, Response<AddNoteResponse> response) {
                    if (response.code() == 200){
                        viewFlipper.setDisplayedChild(0);
                        Toast.makeText(ForgotPassword.this, "Password Changed Successful", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
                        ForgotPassword.this.startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<AddNoteResponse> call, Throwable t) {
                    call.cancel();
                }
            });
        });
    }
}
