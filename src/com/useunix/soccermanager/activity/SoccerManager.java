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
import com.useunix.soccermanager.domain.*;

@TargetApi(11)
public class SoccerManager extends Activity {

    private static final int ACTIVITY_PREFERENCES=0;
    private static final int ACTIVITY_PLAYER_LIST=1;
    private static final int ACTIVITY_PLAY_GAME=2;
    private static final int ACTIVITY_GAME_LIST=3;
    private static final int ACTIVITY_TEAM_LIST=4;
    public static final String TAG = SoccerManager.class.getSimpleName();
    public static final String INTENT_SHIFT_TIMER_ENDED = SoccerManager.class.getName() + ".INTENT_SHIFT_TIMER_ENDED";
    public static final String CURRENT_SHIFT = SoccerManager.class.getName() + ".CURRENT_SHIFT";

	private Button teamListButton;
	private Button resumeGameButton;
	private Button playGameButton;
	private Button settingsButton;

    private SoccerManagerDataHelper dataHelper;
    private GameDao gameDao;
    private TeamDao teamDao;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        teamListButton = (Button) findViewById(R.id.team_list_button);
        final Intent teamListIntent = new Intent(this, TeamList.class);
        teamListButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
                startActivityForResult(teamListIntent, ACTIVITY_TEAM_LIST);
        	}
        });

        playGameButton = (Button) findViewById(R.id.play_game_button);
        final Intent playGameIntent = new Intent(this, PlayGame.class);
        playGameButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
                startActivityForResult(playGameIntent, ACTIVITY_PLAY_GAME);
        	}
        });

        dataHelper = new SoccerManagerDataHelper(this);
        gameDao = new GameDao(dataHelper.getWritableDatabase());
        teamDao = new TeamDao(dataHelper.getWritableDatabase());
        Team team = SoccerManager.getActiveTeam(this, teamDao);
        resumeGameButton = (Button) findViewById(R.id.resume_game_button);
        final Intent resumeGameIntent = new Intent(this, GameShift.class);
        Game mostRecentGame = gameDao.findMostRecentGame(team.getId());
        if (mostRecentGame != null) {
            resumeGameIntent.putExtra("GAME_ID", mostRecentGame.getId());
        }
        resumeGameButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
                startActivityForResult(resumeGameIntent, ACTIVITY_PLAY_GAME);
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

    public static Team getActiveTeam(Activity activity, TeamDao teamDao) {
        return teamDao.findTeamByName(getActiveTeamName(activity));
    }

    private static String getActiveTeamName(Activity activity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String teamNameKey = activity.getString(R.string.team_name_key);
        return sharedPreferences.getString(teamNameKey, "Growl");
    }

    public static void updateTitle(Activity activity) {
        String teamName = getActiveTeamName(activity);
        activity.setTitle("Soccer Manager for Team " + teamName);
    }

    public void showPreferences() {
    	final Intent intent = new Intent(this, SoccerManagerPreferencesActivity.class);
        startActivityForResult(intent, ACTIVITY_PREFERENCES);
    }

}
