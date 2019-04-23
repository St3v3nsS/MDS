package com.example.firstapp.menuActivities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.firstapp.R;
import com.example.firstapp.interfaces.Api;
import com.example.firstapp.models.Profile;
import com.example.firstapp.responses.FriendsResponse;
import com.example.firstapp.responses.UsersResponse;
import com.example.firstapp.services.RetrofitClient;
import com.example.firstapp.services.UsersAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFriend extends Fragment {

    private View rootView;
    private RecyclerView users;
    private UsersAdapter usersAdapter;
    private ArrayList<Profile> usersList;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_friend, container, false);
        getActivity().setTitle("Add Friends");

        users = (RecyclerView) rootView.findViewById(R.id.all_users);
        users.setLayoutManager(new LinearLayoutManager(getContext()));

        usersAdapter = new UsersAdapter(usersList);
        users.setAdapter(usersAdapter);

        searchView = rootView.findViewById(R.id.search_user);


        Api usersReq = RetrofitClient.createService(Api.class);
        Call<UsersResponse> call = usersReq.getAllUsers();
        call.enqueue(new Callback<UsersResponse>() {
            @Override
            public void onResponse(Call<UsersResponse> call, Response<UsersResponse> response) {

                if(response.code() == 200){
                    UsersResponse responseUser = response.body();
                    if (responseUser == null){
                        Toast.makeText(getContext(), "NO FRIENDS", Toast.LENGTH_LONG).show();
                    }
                    else{
                        usersList = response.body().getUsers();
                        System.out.println(usersList);

                        usersAdapter.setList(usersList);
                        usersAdapter.notifyDataSetChanged();

                        fetchUsers();
                    }

                }

            }

            @Override
            public void onFailure(Call<UsersResponse> call, Throwable t) {
                call.cancel();
            }
        });


        return rootView;
    }

    private void fetchUsers() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                usersAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                usersAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
