package com.useunix.soccermanager.activity;

import com.useunix.soccermanager.R;
import com.useunix.soccermanager.domain.Player;
import com.useunix.soccermanager.domain.PlayerDao;
import com.useunix.soccermanager.domain.SoccerManagerDataHelper;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PlayerEdit extends Activity {

	private EditText firstNameText;
    private EditText lastNameText;
    private Long id;
    private SoccerManagerDataHelper soccerManagerDataHelper;
    private PlayerDao playerDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        soccerManagerDataHelper = new SoccerManagerDataHelper(this);
        playerDao = new PlayerDao(soccerManagerDataHelper.getWritableDatabase());
        setContentView(R.layout.player_edit);
        
       
        firstNameText = (EditText) findViewById(R.id.first_name);
        lastNameText = (EditText) findViewById(R.id.last_name);
      
        Button saveButton = (Button) findViewById(R.id.save);
       
        id = savedInstanceState != null ? savedInstanceState.getLong(PlayerDao.ID_COL) 
                							: null;
		if (id == null) {
			Bundle extras = getIntent().getExtras();            
			id = extras != null ? extras.getLong(PlayerDao.ID_COL) 
									: null;
		}

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
            Cursor playerCursor = playerDao.get(id);
            startManagingCursor(playerCursor);
            firstNameText.setText(playerCursor.getString(
            		playerCursor.getColumnIndexOrThrow(PlayerDao.FIRST_NAME_COL)));
            lastNameText.setText(playerCursor.getString(
            		playerCursor.getColumnIndexOrThrow(PlayerDao.LAST_NAME_COL)));
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(PlayerDao.ID_COL, id);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }
    
    private void saveState() {
        String firstName = firstNameText.getText().toString();
        String lastName = lastNameText.getText().toString();

        if (id == null) {
            long newId = playerDao.create(new Player(firstName, lastName)).getId();
            if (newId > 0) {
                id = newId;
            }
        } else {
            playerDao.update(new Player(id, firstName, lastName));
        }
    }
    
}
