package com.example.firstapp.services;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firstapp.R;
import com.example.firstapp.interfaces.Api;
import com.example.firstapp.models.Profile;
import com.example.firstapp.models.StringBody;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    // This class is used for showing the friends with all their information in the recycler view
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
        viewHolder.onButtonClick(friends.get(i));
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

        public void onButtonClick(Profile profile) {
            Button delete = (Button) mView.findViewById(R.id.delete_friend);

            delete.setOnClickListener(ev->{
                Api api = RetrofitClient.createService(Api.class);
                Call<StringBody> call = api.deleteFriend(profile);
                call.enqueue(new Callback<StringBody>() {
                    @Override
                    public void onResponse(Call<StringBody> call, Response<StringBody> response) {
                        if (response.code() == 200){
                            Toast.makeText(mView.getContext(), "Deleted friend", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(mView.getContext(), "Some error occurred. Try again!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<StringBody> call, Throwable t) {
                        Toast.makeText(mView.getContext(), "Some error occurred. Try again!", Toast.LENGTH_LONG).show();
                        call.cancel();
                    }
                });
            });
        }
    }
}
