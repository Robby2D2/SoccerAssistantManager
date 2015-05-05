package com.useunix.soccermanager.domain;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PlayerDao {
	public static final String TABLE_NAME = "Player";
	public static final String ID_COL = "_id";
	public static final String FIRST_NAME_COL = "firstName";
	public static final String LAST_NAME_COL = "lastName";
	public static final String[] ALL_COLUMNS = new String[] {
		ID_COL,
		FIRST_NAME_COL,
		LAST_NAME_COL
	};
	public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +  " ("
		+ ID_COL + " integer primary key autoincrement"
		+ ", " + FIRST_NAME_COL + " text not null"
		+ ", " + LAST_NAME_COL + " text"
		+ ")";
	
	private static final String SQL_GET_ALL_ATTENDING_PLAYERS = 
			"SELECT p.* FROM " + TABLE_NAME + " p"
				+ " JOIN " + GamePlayerDao.TABLE_NAME + " gp on gp." + GamePlayerDao.PLAYER_ID_COL + " = p." + ID_COL
				+ " WHERE gp." + GamePlayerDao.GAME_ID_COL + " = ?";


	
	private final SQLiteDatabase db;
	
	public PlayerDao(SQLiteDatabase db) {
		this.db = db;
	}

	public Player create(Player player) {
		ContentValues values = new ContentValues();
		values.put(FIRST_NAME_COL, player.getFirstName());
		values.put(LAST_NAME_COL, player.getLastName());
		long id = db.insert(TABLE_NAME, null, values);
		player.setId(id);
		return player;
	}
	
	public boolean update(Player player) {
        ContentValues args = new ContentValues();
        args.put(FIRST_NAME_COL, player.getFirstName());
        args.put(LAST_NAME_COL, player.getLastName());

        return db.update(TABLE_NAME, args, ID_COL + "=" + player.getId(), null) > 0;
    }
	
	public List<Player> getAll() {
		List<Player> list = new ArrayList<Player>();
		Cursor cursor = getAllCursor();
		if (cursor.moveToFirst()) {
			do {
				list.add(getPlayer(cursor));
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}
	
	public List<Player> getAttendingPlayers(Long gameId) {
		List<Player> list = new ArrayList<Player>();
		Cursor cursor = db.rawQuery(SQL_GET_ALL_ATTENDING_PLAYERS, new String[]{ String.valueOf(gameId) });
		if (cursor.moveToFirst()) {
			do {
				list.add(getPlayer(cursor));
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	public Player getPlayer(Cursor cursor) {
		Player player = new Player(
			cursor.getLong(cursor.getColumnIndexOrThrow(ID_COL)),
			cursor.getString(cursor.getColumnIndexOrThrow(FIRST_NAME_COL)),
			cursor.getString(cursor.getColumnIndexOrThrow(LAST_NAME_COL))
		);
		return player;
	}

	public Cursor getAllCursor() {
		Cursor cursor = db.query(TABLE_NAME, ALL_COLUMNS,
				null, null, null, null, 
				LAST_NAME_COL + " asc, " + FIRST_NAME_COL + " asc");
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
	
	public Player getPlayer(long id) {
		return getPlayer(get(id));
	}
	
	public boolean delete(long id) {
        return db.delete(TABLE_NAME, ID_COL + "=" + id, null) > 0;
    }

}
