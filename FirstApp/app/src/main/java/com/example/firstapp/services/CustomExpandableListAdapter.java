package com.example.firstapp.services;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firstapp.R;
import com.example.firstapp.interfaces.Api;
import com.example.firstapp.models.EventClass;
import com.example.firstapp.models.StringBody;
import com.example.firstapp.responses.AddNoteResponse;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
    // This class is used for the expandable list from dashboard which contains the events

    private Context context;
    private List<String> expandableListTitles;
    private HashMap<String, EventClass> expandableEvents;

    public CustomExpandableListAdapter(Context context, List<String> expandableListTitles,
                                       HashMap<String, EventClass> expandableEvents) {
        this.context = context;
        this.expandableListTitles = expandableListTitles;
        this.expandableEvents = expandableEvents;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return expandableEvents.get(expandableListTitles.get(groupPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final EventClass expandedListText = (EventClass) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.expandedListItem);
        expandedListTextView.setText(expandedListText.toString());

        Button deleteEvent = (Button) convertView.findViewById(R.id.delete_event);
        deleteEvent.setOnClickListener(ev->{
            Api api = RetrofitClient.createService(Api.class);
            Call<AddNoteResponse> call = api.deleteEvent(new EventClass(expandedListText.getName()));
            call.enqueue(new Callback<AddNoteResponse>() {
                @Override
                public void onResponse(Call<AddNoteResponse> call, Response<AddNoteResponse> response) {
                    if(response.code() == 200){
                        Toast.makeText(context, "Successfully deleted! Please Refresh and wait!", Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(context, "Some error occured. Please try again later!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<AddNoteResponse> call, Throwable t) {
                    Toast.makeText(context, "Some error occured. Please try again later!", Toast.LENGTH_LONG).show();
                    call.cancel();
                }
            });
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getGroupCount() {
        return expandableEvents.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return expandableListTitles.get(groupPosition);
    }

}
