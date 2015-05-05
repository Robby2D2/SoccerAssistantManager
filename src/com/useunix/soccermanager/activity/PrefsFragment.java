package com.useunix.soccermanager.activity;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.useunix.soccermanager.R;

public class PrefsFragment extends PreferenceFragment {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
