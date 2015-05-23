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

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.useunix.soccermanager.R;
import com.useunix.soccermanager.domain.*;

public class TeamEdit extends Activity {

	private EditText teamNameText;
    private Long id;
    private SoccerManagerDataHelper soccerManagerDataHelper;
    private TeamDao teamDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        soccerManagerDataHelper = new SoccerManagerDataHelper(this);
        teamDao = new TeamDao(soccerManagerDataHelper.getWritableDatabase());

        setContentView(R.layout.team_edit);
        
        teamNameText = (EditText) findViewById(R.id.team_name);

        Button saveButton = (Button) findViewById(R.id.save);

        Bundle extras = getIntent().getExtras();
        id = extras != null ? extras.getLong(PlayerDao.ID_COL) : null;

		populateFields();
		
		saveButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        	    setResult(RESULT_OK);
        	    finish();
        	}
        });
    }
    
    private void populateFields() {
        if (id != null) {
            Team team = teamDao.getTeam(id);
            teamNameText.setText(team.getName());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    private void saveState() {
        String teamName = teamNameText.getText().toString();

        if (id == null) {
            long newId = teamDao.create(new Team(teamName)).getId();
            if (newId > 0) {
                id = newId;
            }
        } else {
            teamDao.update(new Team(id, teamName));
        }
    }
    
}
