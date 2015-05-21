/*
 * Copyright (c)  2015 Danek Consulting Company
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.useunix.soccermanager.activity;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.useunix.soccermanager.R;
import com.useunix.soccermanager.domain.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@TargetApi(11)
public class TeamList extends ListActivity {
	private static final String TAG = TeamList.class.getName();

	private SoccerManagerDataHelper dataHelper;
    private TeamDao teamDao;
    private GameDao gameDao;
    private String teamName;
    private ListView mainListView;
    private static final int CREATE_TEAM_MENU_ID = Menu.FIRST;
    private static final int DELETE_TEAM_MENU_ID = Menu.FIRST + 1;
    
    private Button createNewPlayerButton;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_list);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String teamNameKey = getString(R.string.team_name_key);
        teamName = sharedPreferences.getString(teamNameKey, "Growl");

        dataHelper = new SoccerManagerDataHelper(this);
        gameDao = new GameDao(dataHelper.getWritableDatabase());
        teamDao = new TeamDao(dataHelper.getWritableDatabase());

        createNewPlayerButton = (Button)findViewById(R.id.add_player_button);
        createNewPlayerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "createNewPlayerButton clicked");
            }
        });
        mainListView = getListView();
        fillData();
        mainListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        for (int count = 0; count < mainListView.getCount(); count++) {
            Cursor teamCursor = (Cursor)mainListView.getItemAtPosition(count);
            Team team = teamDao.getTeam(teamCursor);
            if (teamName.equals(team.getName())) {
                mainListView.setItemChecked(count, true);
            }
        }
    }
    
    private void fillData() {
        Cursor teamCursor = teamDao.getAllCursor();

        setListAdapter(new TeamListAdapter(this, teamCursor, 0));
    }

    private class TeamListAdapter extends CursorAdapter {
        private LayoutInflater layoutInflater;

        public TeamListAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Team team = teamDao.getTeam(cursor);
            CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(R.id.teamCheckedTextView);
            checkedTextView.setText(team.getId() + ": " + team.getName());
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return layoutInflater.inflate(R.layout.team_list_row, null);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
	        case CREATE_TEAM_MENU_ID:
	            createNewTeam();
	            return true;
        }
       
        return super.onMenuItemSelected(featureId, item);
    }
    
    public void createNewTeam() {
//    	Intent i = new Intent(this, TeamEdit.class);
//        startActivityForResult(i, ACTIVITY_CREATE_PLAYER);
    }



    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Team team = teamDao.getTeam(id);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String teamNameKey = getString(R.string.team_name_key);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(teamNameKey, team.getName());
        editor.commit();
//        Intent i = new Intent(this, PlayGame.class);
//        i.putExtra("GAME_ID", id);
//        startActivityForResult(i, ACTIVITY_PLAY_GAME);
    }

}