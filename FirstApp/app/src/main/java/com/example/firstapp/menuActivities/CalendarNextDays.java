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
import android.widget.Toast;

import com.example.firstapp.R;

import java.util.ArrayList;
import java.util.Arrays;

public class CalendarNextDays extends Fragment {

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


        View dialogView = LayoutInflater.from(rootView.getContext()).inflate(R.layout.add_note_dialog, container, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());

        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button showEvents = (Button) dialogView.findViewById(R.id.viewEvents);
        Button addEvents = (Button) dialogView.findViewById(R.id.addEvent);

        showEvents.setOnClickListener(v ->{
            Toast.makeText(dialogView.getContext(), "Alohaa", Toast.LENGTH_LONG).show();
        });

        addEvents.setOnClickListener(v->{
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
