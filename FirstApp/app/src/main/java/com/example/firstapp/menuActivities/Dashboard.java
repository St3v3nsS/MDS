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

    ExpandableListView expandableListView;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeLayout;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        getActivity().setTitle("Dashboard");
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.pullToRefresh);

        if(swipeLayout != null) {
            System.out.println("OK");
            swipeLayout.setOnRefreshListener(this);
        }

        expandableListView = (ExpandableListView) rootView.findViewById(R.id.expandableListView);
        progressBar = rootView.findViewById(R.id.gettingEventsProgressBar);
        new ExpandableEvAsyncTask(getContext(), progressBar, expandableListView).execute("");

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chooseFriend(view);

    }

    private void chooseFriend(View view) {

        Button chooseAFriend = (Button) rootView.findViewById(R.id.choose_friend);

        chooseAFriend.setOnClickListener(v->{

            ViewGroup viewGroup = view.findViewById(R.id.content_frame);
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.matching_friends, viewGroup, false);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setView(dialogView);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }

    @Override
    public void onRefresh() {
        Toast.makeText(getActivity(),"Refresh", Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
                new ExpandableEvAsyncTask(getContext(), progressBar, expandableListView).execute("");
            }
        },2500);
    }
}
