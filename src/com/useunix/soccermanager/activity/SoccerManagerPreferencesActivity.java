package com.useunix.soccermanager.activity;

import com.useunix.soccermanager.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SoccerManagerPreferencesActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
		addPreferencesFromResource(R.xml.preferences);
		
	}
}
