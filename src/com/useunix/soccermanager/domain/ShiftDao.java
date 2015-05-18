package com.useunix.soccermanager.domain;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShiftDao {
	public static final String TABLE_NAME = "Shift";
	public static final String ID_COL = "_id";
	public static final String START_TIME_COL = "startTime";
	public static final String GAME_ID_COL = "gameId";
	public static final String RANK_COL = "rank";
	public static final String[] ALL_COLUMNS = new String[] {
		ID_COL,
		START_TIME_COL,
		GAME_ID_COL,
		RANK_COL
	};
	public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +  " ("
		+ ID_COL + " integer primary key autoincrement"
		+ ", " + START_TIME_COL + " integer not null"
		+ ", " + GAME_ID_COL + " text"
		+ ", " + RANK_COL + " integer"
		+ ")";

	private final SQLiteDatabase db;

	public ShiftDao(SQLiteDatabase db) {
		this.db = db;
	}

	public Shift create(Shift shift) {
		ContentValues values = new ContentValues();
		values.put(START_TIME_COL, shift.getStartTime().getTime());
		values.put(GAME_ID_COL, shift.getGameId());
		values.put(RANK_COL, shift.getRank());
		long id = db.insert(TABLE_NAME, null, values);
		shift.setId(id);
		return shift;
	}
	
	public boolean update(Shift shift) {
        ContentValues args = new ContentValues();
        args.put(START_TIME_COL, shift.getStartTime().getTime());
        args.put(GAME_ID_COL, shift.getGameId());
        args.put(RANK_COL, shift.getRank());

        return db.update(TABLE_NAME, args, ID_COL + "=" + shift.getId(), null) > 0;
    }
	
	public List<Shift> hydrateAll(Cursor cursor) {
		List<Shift> list = new ArrayList<Shift>();
		if (cursor.moveToFirst()) {
			do {
				list.add(getShift(cursor));
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	public List<Shift> getAll() {
		return hydrateAll(getAllCursor());
	}

	public List<Shift> getAllForGame(long gameId) {
		return hydrateAll(getAllForGameCursor(gameId));
	}

	private Shift getShift(Cursor cursor) {
		if (cursor == null || cursor.getCount() <= 0) return null;

		Shift shift = new Shift(
			cursor.getLong(cursor.getColumnIndexOrThrow(ID_COL)),
			new Date(cursor.getLong(cursor.getColumnIndexOrThrow(START_TIME_COL))),
			cursor.getLong(cursor.getColumnIndexOrThrow(GAME_ID_COL)),
			cursor.getLong(cursor.getColumnIndexOrThrow(RANK_COL))
		);
		return shift;
	}

	public Cursor getAllCursor() {
		Cursor cursor = db.query(TABLE_NAME, ALL_COLUMNS,
				null, null, null, null, 
				RANK_COL + " asc");
		return cursor;
	}
	
	public Cursor getAllForGameCursor(long gameId) {
		return db.query(true, TABLE_NAME, ALL_COLUMNS, GAME_ID_COL + "=" + gameId, null, null,
					null, null, null);
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

	public Cursor getByGameIdAndRank(long gameId, long rank) {
		Cursor mCursor =
			db.query(true, TABLE_NAME, ALL_COLUMNS, GAME_ID_COL + "=" + gameId + " AND " + RANK_COL + "=" + rank, null, null,
					null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Shift getShift(long id) {
		return getShift(get(id));
	}

	public Shift getShiftByGameIdAndRank(long gameId, long rank) {
		return getShift(getByGameIdAndRank(gameId, rank));
	}
	
	public boolean delete(long id) {
        return db.delete(TABLE_NAME, ID_COL + "=" + id, null) > 0;
    }

}
