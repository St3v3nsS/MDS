package com.example.firstapp.menuActivities;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.firstapp.callbacks.AddNoteCallback;
import com.example.firstapp.R;
import com.example.firstapp.models.EventClass;

import java.util.List;

import static com.example.firstapp.services.AddNoteClient.getResponse;
import static java.util.Arrays.asList;

public class AddNote extends Fragment {

    private EventClass event;
    private Button addNote;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_add_note, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        addNote = (Button) rootView.findViewById(R.id.add_note_add_event);

        getData(rootView);
        getActivity().setTitle("Add Note");

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void getData(View v) {
        final EditText name = (EditText) v.findViewById(R.id.add_note_name);
        final EditText startDate = (EditText) v.findViewById(R.id.add_note_start_date);
        final EditText endDate = (EditText) v.findViewById(R.id.add_note_end_date);
        final EditText friends = (EditText) v.findViewById(R.id.add_note_friends);




        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String evName = name.getText().toString();
                final String evStartDate = startDate.getText().toString();
                final String evEndDate = endDate.getText().toString();
                final List<String> evFriends = asList(friends.getText().toString().split(", "));

                AddNote fragment = (AddNote)
                        getFragmentManager().findFragmentById(R.id.content_frame);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                event = new EventClass(evName, evStartDate, evEndDate, evFriends);
                getResponse(fragment, new AddNoteCallback(), event, fragmentTransaction);

            }
        });

    }


}
