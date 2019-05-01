package com.example.firstapp.services;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.firstapp.R;
import com.example.firstapp.models.Profile;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private List<Profile> friends;
    static Map<Integer, View> views = new HashMap<>();


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

        views.put(i, viewHolder.getmView());
        viewHolder.getmView().setOnClickListener(v->{
            if (i == viewHolder.getAdapterPosition()){
                if(viewHolder.getmView().isSelected()){
                    viewHolder.getmView().setSelected(false);
                }
                else{
                    for(int j = 0; j < views.size(); j++){
                        views.get(j).setSelected(false);
                    }
                    views.get(viewHolder.getAdapterPosition()).setSelected(false);
                    viewHolder.getmView().setSelected(true);
                }
            }
            else{
                viewHolder.getmView().setSelected(false);
            }
        });
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

    public String getUsername(int position){
        return friends.get(position).getUsername();
    }

    public void setList(ArrayList<Profile> friendsResponse) {
        this.friends = friendsResponse;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public View getmView() {
            return mView;
        }

        public void setStatus(String status){

            TextView friendStatus = (TextView) mView.findViewById(R.id.user_status);

            friendStatus.setText(status);
        }

        public void setUsername(String username){
            TextView friendUsername = (TextView) mView.findViewById(R.id.user_name);

            friendUsername.setText(username);
        }

        public String getUsername(){
            TextView friendUsername = (TextView) mView.findViewById(R.id.user_name);

            return friendUsername.getText().toString();
        }
    }
}
