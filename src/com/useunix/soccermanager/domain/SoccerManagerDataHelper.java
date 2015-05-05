package com.useunix.soccermanager.domain;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SoccerManagerDataHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "socermanager.db";
	private static final int DATABASE_VERSION = 3;

	public SoccerManagerDataHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PlayerDao.CREATE_TABLE);
        db.execSQL(GameDao.CREATE_TABLE);
        db.execSQL(GamePlayerDao.CREATE_TABLE);
        
        PlayerDao playerDao = new PlayerDao(db);
        playerDao.create(new Player("Ronin", "Danek"));
        playerDao.create(new Player("Nels", "Hedman"));
        playerDao.create(new Player("Nathan", "Kaiser"));
        playerDao.create(new Player("Jonah", "Karch"));
        playerDao.create(new Player("Ethan", "LeFrancois"));
        playerDao.create(new Player("Whitaker", "Lund"));
        playerDao.create(new Player("Avery", "Nolin"));
        playerDao.create(new Player("Parker", "Reis"));
        playerDao.create(new Player("Talia", "Schein"));
        playerDao.create(new Player("Brody", "Tetzlaff"));
        playerDao.create(new Player("Sophie", "Tetzlaff"));
        playerDao.create(new Player("Evelyn", "Weigel"));
        
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("SoccerManager", "Upgrading database");
		db.execSQL("DROP TABLE IF EXISTS " + PlayerDao.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + GameDao.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + GamePlayerDao.TABLE_NAME);
		onCreate(db);
	}

}
