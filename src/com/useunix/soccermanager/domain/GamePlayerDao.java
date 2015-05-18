package com.useunix.soccermanager.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GamePlayerDao {
	public static final String TABLE_NAME = "GamePlayer";
	public static final String ID_COL = "_id";
	public static final String GAME_ID_COL = "gameId";
	public static final String PLAYER_ID_COL = "playerId";
	public static final String[] ALL_COLUMNS = new String[] {
		ID_COL,
		GAME_ID_COL,
		PLAYER_ID_COL
	};
	public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +  " ("
		+ ID_COL + " integer primary key autoincrement"
		+ ", " + GAME_ID_COL + " integer not null"
		+ ", " + PLAYER_ID_COL + " integer"
		+ ")";
	
	private final SQLiteDatabase db;
	
	public GamePlayerDao(SQLiteDatabase db) {
		this.db = db;
	}

	public void create(Long gameId, Long playerId) {
		ContentValues values = new ContentValues();
		values.put(GAME_ID_COL, gameId);
		values.put(PLAYER_ID_COL, playerId);
		db.insert(TABLE_NAME, null, values);
	}

	public boolean delete(Long gameId, Long playerId) {
        return db.delete(TABLE_NAME, GAME_ID_COL + "= ? AND " + PLAYER_ID_COL + " = ?",  new String[]{gameId.toString(), playerId.toString()}) > 0;
    }

	public boolean deleteAll(Long gameId) {
		return db.delete(TABLE_NAME, GAME_ID_COL + "= ?",  new String[]{gameId.toString()}) > 0;
	}


}
