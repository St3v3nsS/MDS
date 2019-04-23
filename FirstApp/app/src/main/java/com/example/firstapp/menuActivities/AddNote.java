package com.example.firstapp.menuActivities;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.firstapp.callbacks.AddNoteCallback;
import com.example.firstapp.R;
import com.example.firstapp.interfaces.Api;
import com.example.firstapp.models.EventClass;
import com.example.firstapp.models.Profile;
import com.example.firstapp.responses.FriendsResponse;
import com.example.firstapp.services.RetrofitClient;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.firstapp.services.AddNoteClient.getResponse;
import static java.util.Arrays.asList;

public class AddNote extends Fragment {

    private EventClass event;
    private Button addNote;
    private TextView startDate;
    private TextView endDate;
    private String startDateS;
    private String endDateS;
    private String dateBegin;

    private View rootView;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_add_note, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        addNote = (Button) rootView.findViewById(R.id.add_note_add_event);

        startDate = (TextView) rootView.findViewById(R.id.add_note_start_date);
        endDate = (TextView) rootView.findViewById(R.id.add_note_end_date);
        getData();
        getActivity().setTitle("Add Note");

        Bundle bundle = getArguments();
        if (bundle != null)
            dateBegin = bundle.getString("date");

        return rootView;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getData() {
        final EditText name = (EditText) rootView.findViewById(R.id.add_note_name);
        final Calendar calendar = Calendar.getInstance();
        final MultiAutoCompleteTextView friends = (MultiAutoCompleteTextView) rootView.findViewById(R.id.add_note_friends);
        startDate.setOnClickListener(v -> {                 //  Handle startDate on click for getting data
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);       // get the Hour
            int currentMinutes = calendar.get(Calendar.MINUTE);         // get the minute
            int currentYear = calendar.get(Calendar.YEAR);              // get the year
            int currentMonth = calendar.get(Calendar.MONTH) + 1;            // get the month
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);//get the day
            TimePickerDialog timePickerDialog = new TimePickerDialog(rootView.getContext(),     // create the dialog
                    (view, hourOfDay, minute) -> {      // onTimeSet function
                        StringBuilder sb = new StringBuilder();
                        if(dateBegin == null){
                            sb.append(currentYear+"-");
                            if(currentMonth < 10){
                                sb.append("0");
                            }
                            sb.append(currentMonth+"-"); sb.append(currentDay+"T");
                        }
                        else{
                            sb.append(dateBegin);
                        }

                        if (hourOfDay <= 9){
                            sb.append("0");
                        }

                        sb.append(hourOfDay);
                        sb.append(":");

                        if (minute <= 9){
                            sb.append("0");
                        }
                        sb.append(minute);

                        String toShow = sb.replace(sb.indexOf("T"), sb.indexOf("T")+1, " ").toString();

                        sb.append(":");
                        sb.append(calendar.get(Calendar.SECOND));

                        startDateS = sb.toString();
                        startDate.setText(toShow);  // set the time
                    }, currentHour, currentMinutes, true);
            timePickerDialog.show();
        });

        endDate.setOnClickListener(v ->{        // Handle the endDate on click the same way as we did with startDate
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            int currentMinutes = calendar.get(Calendar.MINUTE);
            int currentYear = calendar.get(Calendar.YEAR);
            int currentMonth = calendar.get(Calendar.MONTH) + 1;
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
            TimePickerDialog timePickerDialog = new TimePickerDialog(rootView.getContext(),
                    (view, hourOfDay, minute) -> {
                        StringBuilder sb = new StringBuilder();
                        if(dateBegin == null){
                            sb.append(currentYear+"-");
                            if(currentMonth < 10){
                                sb.append("0");
                            }
                            sb.append(currentMonth+"-"); sb.append(currentDay+"T");
                        }
                        else{
                            sb.append(dateBegin);
                        }

                        if (hourOfDay <= 9){
                            sb.append("0");
                        }

                        sb.append(hourOfDay);
                        sb.append(":");

                        if (minute <= 9){
                            sb.append("0");
                        }
                        sb.append(minute);
                        String toShow = sb.replace(sb.indexOf("T"), sb.indexOf("T")+1, " ").toString();

                        sb.append(":");
                        sb.append(calendar.get(Calendar.SECOND));

                        endDateS = sb.toString();

                        endDate.setText(toShow);
                    }, currentHour, currentMinutes, true);
            timePickerDialog.show();
        });

        Api getFriends = RetrofitClient.createService(Api.class);   // get the list of friends for multiautocomplete
        Call<FriendsResponse> call = getFriends.getFriends();

        call.enqueue(new Callback<FriendsResponse>() {  // enqueue the call
            @Override
            public void onResponse(Call<FriendsResponse> call, Response<FriendsResponse> response) {
                final List<String> friendResponse = new ArrayList<>();
                Context ctx = getContext();
                FriendsResponse response1 = response.body();

                if(response1 == null){
                    //Toast.makeText(getActivity(), "You have no friends, try to make some!", Toast.LENGTH_LONG).show();
                    friends.setVisibility(View.INVISIBLE);
                }else{
                    friends.setVisibility(View.VISIBLE);
                    final List<Profile> responseParse = response1.getFriends();       // get the response

                    for( Profile p: responseParse){
                        friendResponse.add(p.getUsername());    // create the adapter's String list
                    }
                }

                if(ctx != null){
                    ArrayAdapter<String> friendsList = new ArrayAdapter<>(ctx, R.layout.spinner, friendResponse);   // create the adapter
                    friends.setAdapter(friendsList);    // set the adapter
                    friends.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());   // set string tokenizer
                }

            }

            @Override
            public void onFailure(Call<FriendsResponse> call, Throwable t) {
                call.cancel();
            }   // handle the failed call
        });



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


                event = new EventClass(evName, startDateS, endDateS, evFriends);
                getResponse(fragment, new AddNoteCallback(), event, fragmentTransaction);

            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
