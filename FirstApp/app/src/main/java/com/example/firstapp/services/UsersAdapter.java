package com.example.firstapp.services;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firstapp.R;
import com.example.firstapp.models.Profile;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>
                        implements Filterable {
    private List<Profile> users;
    private List<Profile> usersFiltered;
    private RecyclerView.ViewHolder viewHolder;

    public UsersAdapter(List<Profile> users) {
        this.users = users;
        this.usersFiltered = users;
    }

    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.add_friend_dialog, viewGroup, false);

        return new UsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.ViewHolder viewHolder, int i) {

        Profile user;

        if(usersFiltered != null)
            user = usersFiltered.get(i);
        else{
            user = users.get(i);
        }
        viewHolder.setStatus(user.getEmail());
        viewHolder.setUsername(user.getUsername());
        viewHolder.buttonAction(user.getEmail(), user.getUsername());
    }


    @Override
    public int getItemCount() {
        if( usersFiltered != null){
            return usersFiltered.size();
        } else if (users != null){
            return users.size();
        }
        return 0;
    }

    public void setList(ArrayList<Profile> usersResponse) {
        this.users = usersResponse;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charSeq = constraint.toString();

                if (charSeq.isEmpty()){
                    usersFiltered = users;
                }else{
                    List<Profile> profiles = new ArrayList<>();
                    for (Profile profile: users){

                        if (profile.getUsername().toLowerCase().contains(charSeq.toLowerCase()) ||
                            profile.getEmail().toLowerCase().contains(charSeq.toLowerCase())){
                            profiles.add(profile);
                        }
                    }

                    usersFiltered = profiles;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = usersFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                usersFiltered = (ArrayList<Profile>) results.values;
                System.out.println(usersFiltered);
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setStatus(String status){

            TextView userStatus = (TextView) mView.findViewById(R.id.user_status);

            userStatus.setText(status);
        }

        public void setUsername(String username){
            TextView userUsername = (TextView) mView.findViewById(R.id.user_name);

            userUsername.setText(username);
        }

        public void buttonAction(String status, String Username){

            Button button = (Button) mView.findViewById(R.id.add_button);

            button.setOnClickListener(v->{
                Toast.makeText(mView.getContext(), "Added friend", Toast.LENGTH_LONG).show();
            });
        }
    }
}
