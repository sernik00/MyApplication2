package com.example.andrey.myapplication2;

import android.preference.PreferenceActivity;
import android.os.Bundle;

public class PreferActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }
}
