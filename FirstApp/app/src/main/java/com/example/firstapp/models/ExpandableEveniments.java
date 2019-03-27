package com.example.firstapp.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ExpandableEveniments {

    public static HashMap<String, List<String>> getEveniments(){
        HashMap<String, List<String>> eveniments = new LinkedHashMap<>();
        List<String> morningEvents = new ArrayList<>();

        morningEvents.add("Take coffe");
        morningEvents.add("Eat breakfast");
        morningEvents.add("Meeting with mr. Waffles!");

        List<String> noonEvents = new ArrayList<>();

        noonEvents.add("Eat lunch");
        noonEvents.add("Meeting with lawyer Andrew!");

        List<String> nightEvents = new ArrayList<>();

        nightEvents.add("Eat dinner");
        nightEvents.add("Go to club!");
        nightEvents.add("DO NOT DRINK TOO MUCH! JOB TOMORROW!");

        eveniments.put("Morning Events", morningEvents);
        eveniments.put("Noon Events", noonEvents);
        eveniments.put("Night Events", nightEvents);

        return eveniments;
    }
}
