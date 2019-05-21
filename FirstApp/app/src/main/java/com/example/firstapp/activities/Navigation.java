package com.example.firstapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firstapp.R;
import com.example.firstapp.interfaces.Api;
import com.example.firstapp.menuActivities.AddNote;
import com.example.firstapp.menuActivities.CalendarNextDays;
import com.example.firstapp.menuActivities.Dashboard;
import com.example.firstapp.menuActivities.FriendsFragment;
import com.example.firstapp.menuActivities.NavShare;
import com.example.firstapp.menuActivities.ProfilePhoto;
import com.example.firstapp.models.Profile;
import com.example.firstapp.responses.PhotoResponse;
import com.example.firstapp.services.RetrofitClient;
import com.squareup.picasso.Picasso;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Navigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Profile profile;
    private static final String PREF_COOKIE = "cookies";
    private static final String HAS_COOKIE = "has cookie";
    private static final String DETAILS = "details";
    private static final String USER = "user";
    private static final String DESC = "describe";

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // open the dashboard fragment

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, new Dashboard()).commit();


        setContentView(R.layout.activity_navigation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getProfile();

        // draw the navigation
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        // edit the header text

        View header = navigationView.getHeaderView(0);

        TextView username = (TextView) header.findViewById(R.id.username);
        username.setText(profile.getUsername());
        TextView email = (TextView) header.findViewById(R.id.email);
        email.setText(profile.getEmail());

        // edit the profile photo

        addProfilePhoto(header);


        navigationView.setNavigationItemSelectedListener(this);
    }

    private void addProfilePhoto(View header) {
        imageView = (ImageView) header.findViewById(R.id.profile_picture);

        Api api = RetrofitClient.createService(Api.class);
        Call<PhotoResponse> call = api.getProfilePhoto();

        call.enqueue(new Callback<PhotoResponse>() {
            @Override
            public void onResponse(Call<PhotoResponse> call, Response<PhotoResponse> response) {
                if (response.code() == 200){
                    if (response.body() != null){
                        String url = response.body().getPhoto();
                        Uri imageUri = Uri.parse(url);
                        Picasso.get().load(imageUri).into(imageView);
                    }else{
                        Toast.makeText(Navigation.this, "No image found", Toast.LENGTH_LONG).show();
                    }

                }
                else{
                    Toast.makeText(Navigation.this, "Try again later!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PhotoResponse> call, Throwable throwable) {
                Toast.makeText(Navigation.this, "Try again later!", Toast.LENGTH_LONG).show();
                call.cancel();
            }
        });

    }

    private void getProfile() {
        // getting the data for profile show
        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");
        String email = getIntent().getStringExtra("email");

        profile = new Profile(username, password, email);


        SharedPreferences sharedPreferences = getSharedPreferences(DETAILS, MODE_PRIVATE);
        String user = sharedPreferences.getString(USER, null);
        if (user == null || !user.equals(username)){
            sharedPreferences.edit()
                    .putString(USER, username)
                    .apply();

            sharedPreferences.edit()
                    .putString(DESC, email)
                    .apply();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // get the user to the settings menu, come back in app by pressing 'BACK'
        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        Fragment fragment = null;

        int id = item.getItemId();

        if (id == R.id.nav_dashboard){
            fragment = new Dashboard(); // --> dashboard screen
        }else if (id == R.id.nav_add_profile_photo) {
            fragment = new ProfilePhoto(); // --> Add profile photo screen
        } else if (id == R.id.nav_add_note) {
            fragment = new AddNote();   // --> Add event screen
        } else if (id == R.id.nav_next_days) {
            fragment = new CalendarNextDays(); // --> calendar screen
        } else if (id == R.id.nav_friends){
            fragment = new FriendsFragment(); // --> friends screen
        } else if (id == R.id.nav_logout) {
            logout(); // --> logout dialog
        } else if (id == R.id.nav_share) {
            fragment = new NavShare(); // --> share this app
        }

        // create the view and draw

        if(fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.content_frame , fragment).commit();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        // handle the logout alert dialog
        ViewGroup viewGroup = findViewById(R.id.content_frame);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.logout_dialog, viewGroup, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button cancel = (Button) dialogView.findViewById(R.id.cancel); // cancel button
        Button yes = (Button) dialogView.findViewById(R.id.yes); // yes button

        cancel.setOnClickListener(v->{
            dialog.dismiss();
        });

        yes.setOnClickListener(v->{
            // make an api call to delete the cookies from server

            Api logoutCall = RetrofitClient.createService(Api.class);
            Call<ResponseBody> call = logoutCall.logout();
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println(response.code());

                    if (response.code() == 200){
                        // delete the user from shared preferences

                        SharedPreferences sharedPreferences = getSharedPreferences(PREF_COOKIE, MODE_PRIVATE);
                        sharedPreferences
                                .edit()
                                .clear()
                                .apply();

                        sharedPreferences = getSharedPreferences(DETAILS, MODE_PRIVATE);
                        sharedPreferences
                                .edit()
                                .clear()
                                .apply();

                        System.out.println(sharedPreferences.getString(USER, null));
                        System.out.println(sharedPreferences.getString(DESC, null));

                        // start the Login screen
                        Intent intent = new Intent(Navigation.this, LoginActivity.class);
                        Navigation.this.startActivity(intent);
                    }
                    else{
                        Toast.makeText(Navigation.this, "Some error occurred. Try again!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    call.cancel();
                    Toast.makeText(Navigation.this, "Some error occurred. Try again!", Toast.LENGTH_LONG).show();

                }
            });

        });

    }

}
