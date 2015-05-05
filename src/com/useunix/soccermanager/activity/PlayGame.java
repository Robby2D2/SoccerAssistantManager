package com.useunix.soccermanager.activity;

import java.util.Date;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.useunix.soccermanager.R;
import com.useunix.soccermanager.domain.Game;
import com.useunix.soccermanager.domain.GameDao;
import com.useunix.soccermanager.domain.GamePlayerDao;
import com.useunix.soccermanager.domain.Player;
import com.useunix.soccermanager.domain.PlayerDao;
import com.useunix.soccermanager.domain.SoccerManagerDataHelper;

public class PlayGame extends ListActivity {
	private static final String TAG = ListActivity.class.getName();
	
	private SoccerManagerDataHelper dataHelper;
    private PlayerDao playerDao;
    private GameDao gameDao;
    private GamePlayerDao gamePlayerDao;
    private TextView headerText;
    private Button seeFirstShiftButton;
    private ListView mainListView;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_game);
        
        headerText = (TextView) findViewById(R.id.play_game_header_text);
        headerText.setText(getString(R.string.play_game_header, new Date()));
        
        seeFirstShiftButton = (Button) findViewById(R.id.seeFirstShift);
        final Intent gameShiftIntent = new Intent(this, GameShift.class);
        seeFirstShiftButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
//        		mainListView.getAdapter()
        		Game game = createGameAndAssignPlayers();
        		Log.d(TAG, "Starting new game, going to first shift.");
        		gameShiftIntent.putExtra("GAME_ID", game.getId());
                startActivityForResult(gameShiftIntent, 2);
        	}
        });
        
        mainListView = getListView();
        mainListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);	
        dataHelper = new SoccerManagerDataHelper(this);
        playerDao = new PlayerDao(dataHelper.getWritableDatabase());
        gamePlayerDao = new GamePlayerDao(dataHelper.getWritableDatabase());
        gameDao = new GameDao(dataHelper.getWritableDatabase());
        fillData();
        //registerForContextMenu(getListView());
    }
    
    private Game createGameAndAssignPlayers() {
    	Game game = new Game();
    	game.setStartTime(new Date());
        game = gameDao.create(game);
        
    	for (int position = 0; position < mainListView.getAdapter().getCount(); position++) {
    		if (mainListView.isItemChecked(position)) {
    			Cursor cursor = (Cursor)mainListView.getItemAtPosition(position);
    			Player player = playerDao.getPlayer(cursor);
    			gamePlayerDao.create(game.getId(), player.getId());
    		}
    		
    	}
    	
    	return game;
    }

    private void fillData() {
    	Cursor playerCursor = playerDao.getAllCursor();
    	startManagingCursor(playerCursor);
    	setListAdapter(new PlayerListAdapter(this, playerCursor));
    }
    
	private class PlayerListAdapter extends CursorAdapter {
		private LayoutInflater layoutInflater;

		public PlayerListAdapter(Context context, Cursor c) {
			super(context, c);
			layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			Player player = playerDao.getPlayer(cursor);
			CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(R.id.checkedTextView);
			checkedTextView.setText(player.getFirstName() + " " + player.getLastName());
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return layoutInflater.inflate(R.layout.player_game_row, null);
		}
	}
}