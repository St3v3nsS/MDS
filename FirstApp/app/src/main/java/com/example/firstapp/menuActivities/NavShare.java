package com.example.firstapp.menuActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firstapp.R;

public class NavShare extends android.support.v4.app.Fragment {
    // This class handles the Share Button by creating an Chooser for apps to share the message
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nav_share, container, false);

        getActivity().setTitle("Share");
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        StringBuilder sb = new StringBuilder();
        sb.append("Hi, I am using the Asched App. I like this and I want you to check it out.");
        sb.append("https://play.google.com/store/apps/details?id=" + this.getContext().getPackageName());
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Test");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        startActivity(Intent.createChooser(sharingIntent, "Test"));

        return rootView;
    }
}
