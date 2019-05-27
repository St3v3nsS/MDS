package com.example.firstapp.menuActivities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firstapp.R;
import com.example.firstapp.activities.MainActivity;
import com.example.firstapp.models.EventClass;
import com.example.firstapp.models.ExpandableEveniments;
import com.example.firstapp.services.CustomExpandableListAdapter;
import com.example.firstapp.services.ExpandableEvAsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Dashboard extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {
    // this class is used for showing the events and the button for matching friends. It is the main page after login.

    ExpandableListView expandableListView;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeLayout;
    TextView noEvents;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        getActivity().setTitle("Dashboard");
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.pullToRefresh); // add refresh on swipe down

        if(swipeLayout != null) {
            swipeLayout.setOnRefreshListener(this);
        }
        // getting the expandable list, progress bar and the no events text then create the ExpandableEv class
        expandableListView = (ExpandableListView) rootView.findViewById(R.id.expandableListView);
        progressBar = rootView.findViewById(R.id.gettingEventsProgressBar);
        noEvents = rootView.findViewById(R.id.text_no_events);
        new ExpandableEvAsyncTask(getContext(), progressBar, expandableListView, noEvents).execute("");

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chooseFriend(view);

    }

    // the button to choose friends
    private void chooseFriend(View view) {

        Button chooseAFriend = (Button) rootView.findViewById(R.id.choose_friend); // get the choose friend button

        chooseAFriend.setOnClickListener(v->{ // add listener for on click

            FragmentManager manager = getFragmentManager();
            MatchingFriends dialog = new MatchingFriends();
            dialog.show(manager, "AlertDialog");
        });
    }

    @Override
    public void onRefresh() { // reload the events
        Toast.makeText(getActivity(),"Refresh", Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
                new ExpandableEvAsyncTask(getContext(), progressBar, expandableListView, noEvents).execute("");
            }
        },2500);
    }
}
