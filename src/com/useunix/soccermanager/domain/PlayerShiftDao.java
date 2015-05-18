package com.useunix.soccermanager.domain;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlayerShiftDao {
	public static final String TABLE_NAME = "PlayerShift";
	public static final String ID_COL = "_id";
	public static final String SHIFT_ID_COL = "gameId";
	public static final String PLAYER_ID_COL = "playerId";
	public static final String POSITION_COL = "position";
	public static final String[] ALL_COLUMNS = new String[] {
		ID_COL,
		SHIFT_ID_COL,
		PLAYER_ID_COL,
		POSITION_COL
	};
	public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +  " ("
		+ ID_COL + " integer primary key autoincrement"
		+ ", " + SHIFT_ID_COL + " integer not null"
		+ ", " + PLAYER_ID_COL + " integer not null"
		+ ", " + POSITION_COL + " text"
		+ ")";

	private final SQLiteDatabase db;

	public PlayerShiftDao(SQLiteDatabase db) {
		this.db = db;
	}

	public PlayerShift create(PlayerShift playerShift) {
		ContentValues values = new ContentValues();
		values.put(SHIFT_ID_COL, playerShift.getShiftId());
		values.put(PLAYER_ID_COL, playerShift.getPlayerId());
		values.put(POSITION_COL, playerShift.getPosition());
		long id = db.insert(TABLE_NAME, null, values);
		playerShift.setId(id);
		return playerShift;
	}
	
	public boolean update(Shift shift) {
        ContentValues args = new ContentValues();
        args.put(SHIFT_ID_COL, shift.getStartTime().getTime());
        args.put(PLAYER_ID_COL, shift.getGameId());
        args.put(POSITION_COL, shift.getRank());

        return db.update(TABLE_NAME, args, ID_COL + "=" + shift.getId(), null) > 0;
    }

	public List<PlayerShift> hydrateAll(Cursor cursor) {
		List<PlayerShift> list = new ArrayList<PlayerShift>();
		if (cursor.moveToFirst()) {
			do {
				list.add(getPlayerShift(cursor));
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	public List<PlayerShift> getAll() {
		return hydrateAll(getAllCursor());
	}

	private PlayerShift getPlayerShift(Cursor cursor) {
		PlayerShift playerShift = new PlayerShift(
			cursor.getLong(cursor.getColumnIndexOrThrow(ID_COL)),
			cursor.getLong(cursor.getColumnIndexOrThrow(SHIFT_ID_COL)),
			cursor.getLong(cursor.getColumnIndexOrThrow(PLAYER_ID_COL)),
			cursor.getString(cursor.getColumnIndexOrThrow(POSITION_COL))
		);
		return playerShift;
	}

	public Cursor getAllCursor() {
		Cursor cursor = db.query(TABLE_NAME, ALL_COLUMNS,
				null, null, null, null, 
				ID_COL + " desc");
		return cursor;
	}

	public Cursor getAllForShiftCursor(long shiftId) {
		return db.query(true, TABLE_NAME, ALL_COLUMNS, SHIFT_ID_COL + "=" + shiftId, null, null,
						null, null, null);
	}

	public List<PlayerShift> getAllForShift(long shiftId) {
		return hydrateAll(getAllForShiftCursor(shiftId));
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
	
	public PlayerShift getPlayerShift(long id) {
		return getPlayerShift(get(id));
	}
	
	public boolean delete(long id) {
        return db.delete(TABLE_NAME, ID_COL + "=" + id, null) > 0;
    }

	public int deleteAllForShift(Long shiftId) {
		return db.delete(TABLE_NAME, SHIFT_ID_COL + "=" + shiftId, null);
	}
}
