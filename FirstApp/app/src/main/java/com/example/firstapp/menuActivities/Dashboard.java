package com.example.firstapp.menuActivities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

public class Dashboard extends android.support.v4.app.Fragment {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    ProgressBar progressBar;
    HashMap<String, EventClass> expandableListDetail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        getActivity().setTitle("Dashboard");

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        progressBar = view.findViewById(R.id.gettingEventsProgressBar);
        new ExpandableEvAsyncTask(getContext(), progressBar, expandableListView).execute("");


    }
}
