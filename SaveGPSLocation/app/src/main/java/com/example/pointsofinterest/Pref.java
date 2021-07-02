package com.example.pointsofinterest;

import android.preference.PreferenceActivity;
import android.os.Bundle;

public class Pref extends PreferenceActivity
{
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
    }
}
