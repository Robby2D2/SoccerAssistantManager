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

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.useunix.soccermanager.R;
import com.useunix.soccermanager.domain.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GameList extends ListActivity {
	private static final String TAG = GameList.class.getName();
	
    private static final int ACTIVITY_CREATE_PLAYER = 0;
    private static final int ACTIVITY_EDIT_PLAYER = 1;
    private static final int ACTIVITY_PLAY_GAME = 2;

	private SoccerManagerDataHelper dataHelper;
    private TeamDao teamDao;
    private GameDao gameDao;
    private Team team;

    private static final int CREATE_PLAYER_MENU_ID = Menu.FIRST;
    private static final int DELETE_PLAYER_MENU_ID = Menu.FIRST + 1;
    
    private Button createNewPlayerButton;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_list);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String teamNameKey = getString(R.string.team_name_key);
        String teamName = sharedPreferences.getString(teamNameKey, "Growl");

        dataHelper = new SoccerManagerDataHelper(this);
        gameDao = new GameDao(dataHelper.getWritableDatabase());
        teamDao = new TeamDao(dataHelper.getWritableDatabase());
        team = teamDao.findTeamByName(teamName);

        createNewPlayerButton = (Button)findViewById(R.id.add_player_button);
        createNewPlayerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "createNewPlayerButton clicked");
            }
        });
        
        fillData();
    }
    
    private void fillData() {
        Cursor gameCursor = gameDao.getAllCursor(team.getId());
        startManagingCursor(gameCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{GameDao.ID_COL, GameDao.START_TIME_COL, GameDao.CURRENT_SHIFT_ID_COL};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.id, R.id.startTime, R.id.currentShift};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter gamesAdapter =
        	    new SimpleCursorAdapter(this, R.layout.game_list_row, gameCursor, from, to);

        gamesAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            public boolean setViewValue(View aView, Cursor aCursor, int aColumnIndex) {

                if (aColumnIndex == 1) {
                    Date startDate = new Date(aCursor.getLong(aColumnIndex));
                    TextView textView = (TextView) aView;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    textView.setText(sdf.format(startDate));
                    return true;
                }

                return false;
            }
        });

        setListAdapter(gamesAdapter);
    }


    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
	        case CREATE_PLAYER_MENU_ID:
	            createPlayer();
	            return true;
        }
       
        return super.onMenuItemSelected(featureId, item);
    }
    
    public void createPlayer() {
    	Intent i = new Intent(this, PlayerEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE_PLAYER);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, PlayGame.class);
        i.putExtra("GAME_ID", id);
        startActivityForResult(i, ACTIVITY_PLAY_GAME);
    }

}