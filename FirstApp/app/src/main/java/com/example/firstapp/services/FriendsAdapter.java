package com.example.firstapp.services;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.firstapp.R;
import com.example.firstapp.models.Profile;


import java.util.ArrayList;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private List<Profile> friends;
    private RecyclerView.ViewHolder viewHolder;

    public FriendsAdapter(List<Profile> friends) {
        this.friends = friends;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_display, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.setStatus(friends.get(i).getEmail());
        viewHolder.setUsername(friends.get(i).getUsername());
    }


    @Override
    public int getItemCount() {
        if( friends != null){
            return friends.size();
        }
        return 0;
    }

    public void setList(ArrayList<Profile> friendsResponse) {
        this.friends = friendsResponse;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setStatus(String status){

            TextView friendStatus = (TextView) mView.findViewById(R.id.user_status);

            friendStatus.setText(status);
        }

        public void setUsername(String username){
            TextView friendUsername = (TextView) mView.findViewById(R.id.user_name);

            friendUsername.setText(username);
        }
    }
}
