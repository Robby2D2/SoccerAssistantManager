package com.useunix.soccermanager.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.useunix.soccermanager.R;
import com.useunix.soccermanager.domain.*;

public class PlayerList extends ListActivity {
	private static final String TAG = PlayerList.class.getName();
	
    private static final int ACTIVITY_CREATE_PLAYER = 0;
    private static final int ACTIVITY_EDIT_PLAYER = 1;
    
	private SoccerManagerDataHelper dataHelper;
    private PlayerDao playerDao;
    private TeamDao teamDao;

    private static final int CREATE_PLAYER_MENU_ID = Menu.FIRST;
    private static final int DELETE_PLAYER_MENU_ID = Menu.FIRST + 1;
    
    private Button createNewPlayerButton;
    private Team team;

    private int alternatingColorOne;
    private int alternatingColorTwo;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_list);
        
        dataHelper = new SoccerManagerDataHelper(this);
        playerDao = new PlayerDao(dataHelper.getWritableDatabase());
        teamDao = new TeamDao(dataHelper.getWritableDatabase());
        team = SoccerManager.getActiveTeam(this, teamDao);

        alternatingColorOne = getResources().getColor(R.color.alternating_color_one);
        alternatingColorTwo = getResources().getColor(R.color.alternating_color_two);

        createNewPlayerButton = (Button)findViewById(R.id.add_player_button);
        createNewPlayerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "createNewPlayerButton clicked");
                createPlayer();
            }
        });
        
        registerForContextMenu(getListView());
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillData();
        SoccerManager.updateTitle(this);
    }

    private void fillData() {
        Cursor playerCursor = playerDao.getAllCursor(team.getId());
        startManagingCursor(playerCursor);
        
        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{PlayerDao.ID_COL, PlayerDao.FIRST_NAME_COL, PlayerDao.LAST_NAME_COL};
        
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.id, R.id.firstName, R.id.lastName};
        
        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter players = 
        	    new SimpleCursorAdapter(this, R.layout.player_list_row, playerCursor, from, to) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        if (position % 2 == 1) {
                            view.setBackgroundColor(alternatingColorOne);
                        } else {
                            view.setBackgroundColor(alternatingColorTwo);
                        }
                        return view;
                    }
                };
        setListAdapter(players);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, CREATE_PLAYER_MENU_ID, 0, R.string.create_player);
        return true;
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
        Intent i = new Intent(this, PlayerEdit.class);
        i.putExtra(PlayerDao.ID_COL, id);
        startActivityForResult(i, ACTIVITY_EDIT_PLAYER);
    }

    @Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		Player player = playerDao.getPlayer(((AdapterContextMenuInfo)menuInfo).id);
        menu.add(0, DELETE_PLAYER_MENU_ID, 0, getString(R.string.delete_player, player.getId(), player.getFirstName() + " " + player.getLastName()));
	}

    @Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
    	case DELETE_PLAYER_MENU_ID:
    		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    		playerDao.delete(info.id);
	        fillData();
	        return true;
		}
		return super.onContextItemSelected(item);
	}
	
}