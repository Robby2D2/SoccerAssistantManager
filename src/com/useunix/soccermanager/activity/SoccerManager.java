/*
Copyright 2015 Danek Consulting Company

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.useunix.soccermanager.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.useunix.soccermanager.R;

@TargetApi(3)
public class SoccerManager extends Activity {

    private static final int ACTIVITY_PREFERENCES=0;
    private static final int ACTIVITY_PLAYER_LIST=1;
    private static final int ACTIVITY_PLAY_GAME=2;
    private static final int ACTIVITY_GAME_LIST=3;
    private static final int ACTIVITY_TEAM_LIST=4;
    public static final String TAG = SoccerManager.class.getSimpleName();
    public static final String INTENT_SHIFT_TIMER_ENDED = SoccerManager.class.getName() + ".INTENT_SHIFT_TIMER_ENDED";
    public static final String CURRENT_SHIFT = SoccerManager.class.getName() + ".CURRENT_SHIFT";

	private Button playerListButton;
	private Button teamListButton;
	private Button gameListButton;
	private Button playGameButton;
	private Button settingsButton;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        playerListButton = (Button) findViewById(R.id.player_list_button);
        final Intent playerListIntent = new Intent(this, PlayerList.class);
        playerListButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
                startActivityForResult(playerListIntent, ACTIVITY_PLAYER_LIST);
        	}
        });

        teamListButton = (Button) findViewById(R.id.team_list_button);
        final Intent teamListIntent = new Intent(this, TeamList.class);
        teamListButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
                startActivityForResult(teamListIntent, ACTIVITY_TEAM_LIST);
        	}
        });

        gameListButton = (Button) findViewById(R.id.game_list_button);
        final Intent gameListIntent = new Intent(this, GameList.class);
        gameListButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
                startActivityForResult(gameListIntent, ACTIVITY_GAME_LIST);
        	}
        });
        
        playGameButton = (Button) findViewById(R.id.play_game_button);
        final Intent playGameIntent = new Intent(this, PlayGame.class);
        playGameButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
                startActivityForResult(playGameIntent, ACTIVITY_PLAY_GAME);
        	}
        });
        
        settingsButton = (Button) findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		showPreferences();
        	}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTitle(this);
    }

    public static void updateTitle(Activity activity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String teamNameKey = activity.getString(R.string.team_name_key);
        String teamName = sharedPreferences.getString(teamNameKey, "Growl");
        activity.setTitle("Soccer Manager for Team " + teamName);
    }

    public void showPreferences() {
    	final Intent intent = new Intent(this, SoccerManagerPreferencesActivity.class);
        startActivityForResult(intent, ACTIVITY_PREFERENCES);
    }

}
