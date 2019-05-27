package com.example.firstapp.menuActivities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firstapp.R;
import com.example.firstapp.interfaces.Api;
import com.example.firstapp.models.Profile;
import com.example.firstapp.responses.FriendsResponse;
import com.example.firstapp.responses.HoursResponse;
import com.example.firstapp.services.FriendsAdapter;
import com.example.firstapp.services.LineDivider;
import com.example.firstapp.services.RetrofitClient;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchingFriends extends DialogFragment {
    // This class handles the matching problem, syncing the user events with his friend events

    View rootView;
    Button matchFriend;
    private RecyclerView friendsList;
    private ArrayList<Profile> friendsResponse = new ArrayList<>();
    private FriendsAdapter friendsAdapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        rootView = LayoutInflater.from(getContext()).inflate(R.layout.matching_friends, null, false);
        friendsList = (RecyclerView) rootView.findViewById(R.id.friends_view);

        friendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        friendsList.addItemDecoration(new LineDivider(getContext()));
        friendsAdapter = new FriendsAdapter(friendsResponse);
        friendsList.setAdapter(friendsAdapter);

        getFriends();
        getMatch();

        return new AlertDialog.Builder(getContext())
                .setView(rootView)
                .create();
    }

    private void getMatch() { // handle the match button
        Button match = rootView.findViewById(R.id.match_friend);

        match.setOnClickListener(v->{
            String name = "";
            for (int i = 0; i < friendsResponse.size(); i++){
                if(friendsList.findViewHolderForAdapterPosition(i).itemView.isSelected()){
                    name = friendsAdapter.getUsername(i);
                }
            }


            // check if there was a friend pressed
            if(!name.isEmpty()){

                Api api = RetrofitClient.createService(Api.class);
                Call<HoursResponse> call = api.matchFriend(name);
                String finalName = name;
                ProgressBar pb = (ProgressBar) rootView.findViewById(R.id.matching);
                pb.setVisibility(View.VISIBLE);
                call.enqueue(new Callback<HoursResponse>() { // make the call to get the intervals for this day
                    @Override
                    public void onResponse(Call<HoursResponse> call, Response<HoursResponse> response) {
                        if(response.code() == 200){

                            if (response.body() != null){ // if we got some intervals
                                String[] values = new String[response.body().getHours().size()];
                                ArrayList<String> hours = response.body().getHours();

                                for(int i = 0; i < hours.size(); i++){
                                    values[i] = hours.get(i); // save them
                                }

                                // create an alert dialog to show the specific intervals
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("You and " + finalName + " can go today together at");
                                builder.setItems(values, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) { // clicking on the date will show a message
                                        Toast.makeText(getContext(), "Go to Add Event fast and create one!"
                                                ,Toast.LENGTH_LONG).show();
                                    }
                                });

                                AlertDialog dialog = builder.create();
                                pb.setVisibility(View.INVISIBLE);
                                dialog.show(); // show the dialog


                            }else{ // if no available hours
                                pb.setVisibility(View.INVISIBLE);

                                Toast.makeText(getContext(), "No hours available, please try again later!"
                                        ,Toast.LENGTH_LONG).show();
                            }
                        }else{ // wrong response code from server
                            System.out.println(response.code());
                            pb.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "Something went wrong. Try again later!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<HoursResponse> call, Throwable throwable) { // something went bad on server
                        Toast.makeText(getContext(), "Something went wrong. Try again later!", Toast.LENGTH_LONG).show();
                        call.cancel();
                        pb.setVisibility(View.INVISIBLE);

                    }
                });
            }

        });
    }


    private void getFriends() {
        // api call to get the friends

        Api friendsApi = RetrofitClient.createService(Api.class);
        Call<FriendsResponse> call = friendsApi.getFriends();
        call.enqueue(new Callback<FriendsResponse>() {
            @Override
            public void onResponse(Call<FriendsResponse> call, Response<FriendsResponse> response) {
                if (response.code() == 200){
                    FriendsResponse friends = response.body();
                    if (friends != null){
                        // fill the recycler with data
                        friendsResponse = friends.getFriends();
                        friendsAdapter.setList(friendsResponse);
                        friendsAdapter.notifyDataSetChanged();
                    }
                    else{
                        Toast.makeText(getContext(), "No friends!", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(getContext(), "No friends!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<FriendsResponse> call, Throwable t) {
                call.cancel();
            }
        });

    }
}

