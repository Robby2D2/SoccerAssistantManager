package com.useunix.soccermanager.activity;

import java.util.Date;
import java.util.List;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.useunix.soccermanager.domain.*;

@TargetApi(11)
public class PlayGame extends ListActivity {
	private static final String TAG = ListActivity.class.getName();
	
	private SoccerManagerDataHelper dataHelper;
    private PlayerDao playerDao;
    private GameDao gameDao;
    private TeamDao teamDao;
    private GamePlayerDao gamePlayerDao;
    private TextView headerText;
    private Button seeFirstShiftButton;
    private ListView mainListView;
	private Long gameId;
	private Team team;
	private Date gameDate;
    private List<Player> allGamePlayers;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_game);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String teamNameKey = getString(R.string.team_name_key);
		String teamName = sharedPreferences.getString(teamNameKey, "Growl");

        headerText = (TextView) findViewById(R.id.play_game_header_text);
		gameDate = new Date();
		headerText.setText(getString(R.string.play_game_header, gameDate));
        
        seeFirstShiftButton = (Button) findViewById(R.id.seeFirstShift);
        final Intent gameShiftIntent = new Intent(this, GameShift.class);
        seeFirstShiftButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Game game = createGameAndAssignPlayers();
				Log.d(TAG, "Starting new game, going to first shift.");
				gameShiftIntent.putExtra("GAME_ID", game.getId());
				gameShiftIntent.putExtra("finishOnBack", true);
				startActivityForResult(gameShiftIntent, 2);
			}
		});

        Bundle intentExtras = getIntent().getExtras();
        if (intentExtras != null) {
            gameId = intentExtras.getLong("GAME_ID");
        }

        mainListView = getListView();
        mainListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        dataHelper = new SoccerManagerDataHelper(this);
        playerDao = new PlayerDao(dataHelper.getWritableDatabase());
        gamePlayerDao = new GamePlayerDao(dataHelper.getWritableDatabase());
        gameDao = new GameDao(dataHelper.getWritableDatabase());
		teamDao = new TeamDao(dataHelper.getWritableDatabase());
		team = teamDao.findTeamByName(teamName);
        fillData();

        allGamePlayers = playerDao.getAttendingPlayers(gameId);

        for (int count = 0; count < mainListView.getCount(); count++) {
            Cursor playerCursor = (Cursor)mainListView.getItemAtPosition(count);
            Player player = playerDao.getPlayer(playerCursor);
            boolean isPlayer = isPlayer(player.getId());
            mainListView.setItemChecked(count, isPlayer);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SoccerManager.updateTitle(this);
    }

    private Game createGameAndAssignPlayers() {
		Game game;
		if (gameId != null) {
			gamePlayerDao.deleteAll(gameId);
			game = gameDao.getGame(gameId);
		} else {
			game = new Game();
			game.setStartTime(new Date());
            game.setTeamId(team.getId());
			game = gameDao.create(game);
			gameId = game.getId();
		}

		for (int position = 0; position < mainListView.getAdapter().getCount(); position++) {
			if (mainListView.isItemChecked(position)) {
				Cursor cursor = (Cursor) mainListView.getItemAtPosition(position);
				Player player = playerDao.getPlayer(cursor);
				gamePlayerDao.create(game.getId(), player.getId());
			}
		}

    	return game;
    }

    private void fillData() {
        Cursor playerCursor = playerDao.getAllCursor(team.getId());
    	startManagingCursor(playerCursor);
    	setListAdapter(new PlayerListAdapter(this, playerCursor, 0));
    }

    private boolean isPlayer(Long playerId) {
        for (Player player : allGamePlayers) {
            if (player.getId() == playerId) {
                return true;
            }
        }
        return false;
    }

	private class PlayerListAdapter extends CursorAdapter {
		private LayoutInflater layoutInflater;

		public PlayerListAdapter(Context context, Cursor c, int flags) {
			super(context, c, flags);
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