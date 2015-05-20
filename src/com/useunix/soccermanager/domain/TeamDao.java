package com.useunix.soccermanager.domain;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TeamDao {
	public static final String TABLE_NAME = "Team";
	public static final String ID_COL = "_id";
	public static final String NAME_COL = "name";
	public static final String[] ALL_COLUMNS = new String[] {
		ID_COL,
		NAME_COL
	};
	public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +  " ("
		+ ID_COL + " integer primary key autoincrement"
		+ ", " + NAME_COL + " text"
		+ ")";

	private final SQLiteDatabase db;

	public TeamDao(SQLiteDatabase db) {
		this.db = db;
	}

	public Team create(Team team) {
		ContentValues values = new ContentValues();
		values.put(NAME_COL, team.getName());
		long id = db.insert(TABLE_NAME, null, values);
		team.setId(id);
		return team;
	}
	
	public boolean update(Team team) {
        ContentValues args = new ContentValues();
        args.put(NAME_COL, team.getName());

        return db.update(TABLE_NAME, args, ID_COL + "=" + team.getId(), null) > 0;
    }
	
	public List<Team> getAll() {
		List<Team> list = new ArrayList<Team>();
		Cursor cursor = getAllCursor();
		if (cursor.moveToFirst()) {
			do {
				list.add(getTeam(cursor));
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	public Team getTeam(Cursor cursor) {
		Team team = new Team(
			cursor.getLong(cursor.getColumnIndexOrThrow(ID_COL)),
			cursor.getString(cursor.getColumnIndexOrThrow(NAME_COL))
		);
		return team;
	}

	public Cursor getAllCursor() {
		Cursor cursor = db.query(TABLE_NAME, ALL_COLUMNS,
				null, null, null, null, 
				NAME_COL + " asc");
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

	public Cursor findByName(String name) {
		Cursor mCursor =
			db.query(true, TABLE_NAME, ALL_COLUMNS, NAME_COL + "= '" + name + "'", null, null,
					null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public Team findTeamByName(String name) {
		return getTeam(findByName(name));
	}

	public Team getTeam(long id) {
		return getTeam(get(id));
	}

	public boolean delete(long id) {
        return db.delete(TABLE_NAME, ID_COL + "=" + id, null) > 0;
    }


}
