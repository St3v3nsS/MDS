package com.example.firstapp.menuActivities;

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
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.firstapp.R;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        getActivity().setTitle("Dashboard");
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.pullToRefresh);

        if(swipeLayout != null) {
            System.out.println("OK");
            swipeLayout.setOnRefreshListener(this);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        progressBar = view.findViewById(R.id.gettingEventsProgressBar);
        new ExpandableEvAsyncTask(getContext(), progressBar, expandableListView).execute("");


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
