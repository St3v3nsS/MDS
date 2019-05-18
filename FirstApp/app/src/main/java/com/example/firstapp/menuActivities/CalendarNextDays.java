package com.example.firstapp.menuActivities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.firstapp.R;
import com.example.firstapp.services.ExpandableEvAsyncTask;

import java.util.ArrayList;
import java.util.Arrays;

public class CalendarNextDays extends Fragment {
    // This class handles the Calendar Screen by showing a calendar from which user can choose a date
    // for viewing the events or adding a new one
    View rootView;

    public CalendarNextDays() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_calendar_next_days, container, false);
        getActivity().setTitle("Next Days");

        CalendarView calendarView = (CalendarView) rootView.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            showCustomDialog(year, month, dayOfMonth, container);
        });
        return rootView;
    }

    private void showCustomDialog(int year, int month, int dayOfMonth, ViewGroup container) {
        // if we changed the date, show the alert dialog to choose from

        View dialogView = LayoutInflater.from(rootView.getContext()).inflate(R.layout.add_note_dialog, container, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());

        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        ViewFlipper flipper = (ViewFlipper) dialogView.findViewById(R.id.flipper_add_events);
        View currentView = flipper.getCurrentView();


        Button showEvents = (Button) currentView.findViewById(R.id.viewEvents);
        Button addEvents = (Button) currentView.findViewById(R.id.addEvent);

        showEvents.setOnClickListener(v ->{ // show the event --> not implemented yet
            flipper.setDisplayedChild(1);

            StringBuilder sb = new StringBuilder();
            sb.append(year + ":"); sb.append((month + 1) + ":"); sb.append(dayOfMonth+" ");

            View view = flipper.getCurrentView();
            ExpandableListView expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListViewChosenDate);
            ProgressBar progressBar = view.findViewById(R.id.gettingEventsProgressBarDate);
            new ExpandableEvAsyncTask(getContext(), progressBar, expandableListView, sb.toString()).execute("");
        });

        addEvents.setOnClickListener(v->{
            // create a new AddNote screen passing the selected date as arguments
            alertDialog.dismiss();

            AddNote addNote = new AddNote();
            Bundle arguments = new Bundle();
            StringBuilder sb = new StringBuilder();
            sb.append(year + ":"); sb.append((month + 1) + ":"); sb.append(dayOfMonth+" ");
            arguments.putString("date", sb.toString());

            addNote.setArguments(arguments);

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, addNote).commit();

        });
    }
}
