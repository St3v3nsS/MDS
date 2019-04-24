package com.example.firstapp.menuActivities;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.firstapp.R;
import com.example.firstapp.interfaces.Api;
import com.example.firstapp.models.Profile;
import com.example.firstapp.responses.FriendsResponse;
import com.example.firstapp.services.FriendsAdapter;
import com.example.firstapp.services.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsFragment extends Fragment {

    private RecyclerView friendsList;
    private ArrayList<Profile> friendsResponse;
    private FriendsAdapter friendsAdapter;
    private Button addFriend;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        getActivity().setTitle("Friends");


        friendsList = (RecyclerView) rootView.findViewById(R.id.friends_view);
        friendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        friendsAdapter = new FriendsAdapter(friendsResponse);
        friendsList.setAdapter(friendsAdapter);

        addFriend = (Button) rootView.findViewById(R.id.add_friend);

        handleButton(container);

        Api friendReq = RetrofitClient.createService(Api.class);
        Call<FriendsResponse> call = friendReq.getFriends();
        call.enqueue(new Callback<FriendsResponse>() {
            @Override
            public void onResponse(Call<FriendsResponse> call, Response<FriendsResponse> response) {

                FriendsResponse friendsResponse1 = response.body();
                if(friendsResponse1 != null){
                    friendsResponse = friendsResponse1.getFriends();
                    friendsAdapter.setList(friendsResponse);
                    friendsAdapter.notifyDataSetChanged();
                    System.out.println(friendsResponse);
                }
                else{
                    Toast.makeText(getContext(), "No friend, add one!", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<FriendsResponse> call, Throwable t) {
                call.cancel();
            }
        });

        return rootView;
    }

    private void handleButton(ViewGroup container) {

        addFriend.setOnClickListener(v->{

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, new AddFriend()).commit();

        });
    }


}
