package com.example.firstapp.services;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.firstapp.models.EventClass;
import com.example.firstapp.models.ExpandableEveniments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableEvAsyncTask extends AsyncTask<String, Void, String> {

    private Context context;
    private ProgressBar progressBar;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private HashMap<String, EventClass> expandableListDetail;
    private TextView textView;
    private String date;

    public ExpandableEvAsyncTask(Context context, ProgressBar progressBar, ExpandableListView expandableListView, TextView noEvents) {
        this.context = context;
        this.progressBar = progressBar;
        this.expandableListView = expandableListView;
        this.textView = noEvents;
    }

    public ExpandableEvAsyncTask(Context context, ProgressBar progressBar, ExpandableListView expandableListView, String date, TextView noEvents) {
        this.context = context;
        this.progressBar = progressBar;
        this.expandableListView = expandableListView;
        this.date = date;
        this.textView = noEvents;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (!progressBar.isShown()){
            progressBar.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected String doInBackground(String... strings) {
        if (date != null){
            expandableListDetail = ExpandableEveniments.getEveniments(date);
        }else{
            expandableListDetail = ExpandableEveniments.getEveniments();
        }

        if(expandableListDetail.size() == 0){
            return "Fail";
        }

        return "SUCCESS!";

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if(progressBar.isShown()){
            progressBar.setVisibility(View.INVISIBLE);
        }

        if(s.equals("SUCCESS!")){
            textView.setVisibility(View.INVISIBLE);
            expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
            expandableListAdapter = new CustomExpandableListAdapter(context, expandableListTitle, expandableListDetail);
            expandableListView.setAdapter(expandableListAdapter);
        }
        else {
            textView.setVisibility(View.VISIBLE);
        }
    }
}
