package com.example.firstapp.menuActivities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import com.example.firstapp.R;

import java.util.Calendar;

public class DatePicker extends DialogFragment {
    // This class handles the DatePicker Dialog from where the user can choose the hour and minute of
    // the event

    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public Context getContext() {
        return context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(),  // return the time
                (TimePickerDialog.OnTimeSetListener) context,
                hour, minute, DateFormat.is24HourFormat(getActivity()));
    }
}
