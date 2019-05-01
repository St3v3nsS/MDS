package com.example.firstapp.menuActivities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firstapp.R;
import com.example.firstapp.interfaces.Api;
import com.example.firstapp.models.Profile;
import com.example.firstapp.responses.FriendsResponse;
import com.example.firstapp.services.FriendsAdapter;
import com.example.firstapp.services.LineDivider;
import com.example.firstapp.services.RetrofitClient;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchingFriends extends DialogFragment {

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

    private void getMatch() {
        Button match = rootView.findViewById(R.id.match_friend);

        match.setOnClickListener(v->{
            for (int i = 0; i < friendsResponse.size(); i++){
                if(friendsList.findViewHolderForAdapterPosition(i).itemView.isSelected()){
                    Toast.makeText(getContext(), friendsAdapter.getUsername(i), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void getFriends() {


        Api friendsApi = RetrofitClient.createService(Api.class);
        Call<FriendsResponse> call = friendsApi.getFriends();
        call.enqueue(new Callback<FriendsResponse>() {
            @Override
            public void onResponse(Call<FriendsResponse> call, Response<FriendsResponse> response) {
                if (response.code() == 200){
                    FriendsResponse friends = response.body();
                    if (friends != null){
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

