package com.example.firstapp.menuActivities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firstapp.R;

public class CalendarNextDays extends Fragment {

    public CalendarNextDays() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar_next_days, container, false);

        getActivity().setTitle("Next Days");
        return rootView;
    }
}
