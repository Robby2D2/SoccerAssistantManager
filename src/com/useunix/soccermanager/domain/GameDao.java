package com.useunix.soccermanager.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GameDao {
	public static final String TABLE_NAME = "Game";
	public static final String ID_COL = "_id";
	public static final String START_TIME_COL = "startTime";
	public static final String OPPONENT_COL = "opponent";
	public static final String CURRENT_SHIFT_ID_COL = "currentShiftId";
	public static final String TEAM_ID_COL = "teamId";
	public static final String[] ALL_COLUMNS = new String[] {
		ID_COL,
		START_TIME_COL,
		OPPONENT_COL,
		CURRENT_SHIFT_ID_COL,
        TEAM_ID_COL
	};
	public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +  " ("
		+ ID_COL + " integer primary key autoincrement"
		+ ", " + START_TIME_COL + " integer not null"
		+ ", " + OPPONENT_COL + " text"
		+ ", " + CURRENT_SHIFT_ID_COL + " integer"
		+ ", " + TEAM_ID_COL + " integer"
		+ ")";
	
	private final SQLiteDatabase db;
	
	public GameDao(SQLiteDatabase db) {
		this.db = db;
	}

	public Game create(Game game) {
		ContentValues values = new ContentValues();
		values.put(START_TIME_COL, game.getStartTime().getTime());
		values.put(OPPONENT_COL, game.getOpponent());
		values.put(CURRENT_SHIFT_ID_COL, game.getCurrentShiftId());
		values.put(TEAM_ID_COL, game.getTeamId());
		long id = db.insert(TABLE_NAME, null, values);
		game.setId(id);
		return game;
	}
	
	public boolean update(Game game) {
        ContentValues args = new ContentValues();
        args.put(START_TIME_COL, game.getStartTime().getTime());
        args.put(OPPONENT_COL, game.getOpponent());
        args.put(CURRENT_SHIFT_ID_COL, game.getCurrentShiftId());
        args.put(TEAM_ID_COL, game.getTeamId());

        return db.update(TABLE_NAME, args, ID_COL + "=" + game.getId(), null) > 0;
    }
	
	public List<Game> getAll(Long teamId) {
		List<Game> list = new ArrayList<Game>();
		Cursor cursor = getAllCursor(teamId);
		if (cursor.moveToFirst()) {
			do {
				list.add(getGame(cursor));
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	private Game getGame(Cursor cursor) {
		Game game = new Game(
			cursor.getLong(cursor.getColumnIndexOrThrow(ID_COL)),
			new Date(cursor.getLong(cursor.getColumnIndexOrThrow(START_TIME_COL))),
			cursor.getString(cursor.getColumnIndexOrThrow(OPPONENT_COL)),
			cursor.getLong(cursor.getColumnIndexOrThrow(CURRENT_SHIFT_ID_COL)),
			cursor.getLong(cursor.getColumnIndexOrThrow(TEAM_ID_COL))
		);
		return game;
	}

	public Cursor getAllCursor(Long teamId) {
		Cursor cursor = db.query(TABLE_NAME, ALL_COLUMNS,
				TEAM_ID_COL + "=" + teamId, null, null, null,
				START_TIME_COL + " desc");
		return cursor;
	}
	
	public Cursor get(long id) {
		Cursor mCursor =
			db.query(true, TABLE_NAME, ALL_COLUMNS, ID_COL + "=" + id, null, null,
					null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public Game getGame(long id) {
		return getGame(get(id));
	}
	
	public boolean delete(long id) {
        return db.delete(TABLE_NAME, ID_COL + "=" + id, null) > 0;
    }
	
	public void savePlayers(List<Player> players) {
		
	}

}
