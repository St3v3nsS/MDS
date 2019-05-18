package com.example.firstapp.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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
import com.example.firstapp.activities.ChannelNotifications;
import com.example.firstapp.activities.MainActivity;
import com.example.firstapp.interfaces.Api;
import com.example.firstapp.models.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>
                        implements Filterable {
    // this class handles the show of all the user and the search button for filtering them
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

        return new UsersAdapter.ViewHolder(view, viewGroup.getContext());
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
        viewHolder.hideButton();
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

        private View mView;
        private Context context;
        private NotificationManagerCompat notificationManager;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            context = context;
            notificationManager = NotificationManagerCompat.from(context);
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

        public void buttonAction(String status, String username){

            Button button = (Button) mView.findViewById(R.id.add_button);

            button.setOnClickListener(v->{
                PendingIntent contentIntent = PendingIntent.getActivity(mView.getContext(), 0,
                        new Intent(mView.getContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification = new NotificationCompat.Builder(mView.getContext(), ChannelNotifications.CHANNEL_1)
                        .setSmallIcon(R.drawable.ic_not_icon)
                        .setLargeIcon(BitmapFactory.decodeResource(mView.getContext().getResources(),
                                R.mipmap.ic_launcher_round))
                        .setVibrate(new long[] { 1000, 1000 })
                        .setSound(Uri.parse(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI))
                        .setContentTitle("Friend Request")
                        .setContentText(username + " accepted your friend request!")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setContentIntent(contentIntent)
                        .build();

                Integer randomId = new Random().nextInt(799);

                Api addFriend = RetrofitClient.createService(Api.class);
                Call<ResponseBody> call = addFriend.addFriend(username);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200){
                            notificationManager.notify(randomId, notification);
                            Toast.makeText(mView.getContext(), "Added friend " + username, Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(mView.getContext(), "No friend " + username, Toast.LENGTH_LONG).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        call.cancel();
                    }
                });


            });
        }

        private void hideButton(){
            Button delete = mView.findViewById(R.id.delete_friend);
            delete.setVisibility(View.INVISIBLE);
        }
    }
}
