package com.example.firstapp.callbacks;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.firstapp.R;
import com.example.firstapp.menuActivities.AddNote;

public class AddNoteCallback {
    // this class is a used as a Callback for adding a new event --> attaching the addNote fragment
    public void onResponse(AddNote addNote, FragmentTransaction fragmentTransaction) {
        fragmentTransaction
                .setReorderingAllowed(false)
                .detach(addNote)
                .attach(addNote)
                .commit();
        fragmentTransaction.replace(R.id.content_frame, new AddNote());

    }

}
