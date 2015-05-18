package com.useunix.soccermanager.domain;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SoccerManagerDataHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "soccermanager.db";
	private static final int DATABASE_VERSION = 10;

	public SoccerManagerDataHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PlayerDao.CREATE_TABLE);
        db.execSQL(GameDao.CREATE_TABLE);
        db.execSQL(TeamDao.CREATE_TABLE);
        db.execSQL(GamePlayerDao.CREATE_TABLE);
        db.execSQL(ShiftDao.CREATE_TABLE);
        db.execSQL(PlayerShiftDao.CREATE_TABLE);

        TeamDao teamDao = new TeamDao(db);

        Long GROWL_TEAM_ID = new Long(1);
        teamDao.create(new Team(GROWL_TEAM_ID, "Growl"));

        Long BLAST_TEAM_ID = new Long(2);
        teamDao.create(new Team(BLAST_TEAM_ID, "Blast"));

        PlayerDao playerDao = new PlayerDao(db);
        playerDao.create(new Player(GROWL_TEAM_ID, "Ronin", "Danek"));
        playerDao.create(new Player(GROWL_TEAM_ID, "Nels", "Hedman"));
        playerDao.create(new Player(GROWL_TEAM_ID, "Nathan", "Kaiser"));
        playerDao.create(new Player(GROWL_TEAM_ID, "Jonah", "Karch"));
        playerDao.create(new Player(GROWL_TEAM_ID, "Ethan", "LeFrancois"));
        playerDao.create(new Player(GROWL_TEAM_ID, "Whitaker", "Lund"));
        playerDao.create(new Player(GROWL_TEAM_ID, "Avery", "Nolin"));
        playerDao.create(new Player(GROWL_TEAM_ID, "Parker", "Reis"));
        playerDao.create(new Player(GROWL_TEAM_ID, "Talia", "Schein"));
        playerDao.create(new Player(GROWL_TEAM_ID, "Brody", "Tetzlaff"));
        playerDao.create(new Player(GROWL_TEAM_ID, "Sophie", "Tetzlaff"));
        playerDao.create(new Player(GROWL_TEAM_ID, "Evelyn", "Weigel"));


        playerDao.create(new Player(BLAST_TEAM_ID, "Matt", "McConley"));
        playerDao.create(new Player(BLAST_TEAM_ID, "Kate", "McConley"));
        playerDao.create(new Player(BLAST_TEAM_ID, "Declan", "Varley"));
        playerDao.create(new Player(BLAST_TEAM_ID, "Sampson", "Evans"));
        playerDao.create(new Player(BLAST_TEAM_ID, "Vivian", "Kinney"));
        playerDao.create(new Player(BLAST_TEAM_ID, "Ruby", "Johnson"));
        playerDao.create(new Player(BLAST_TEAM_ID, "Reese", "Jorensen"));
        playerDao.create(new Player(BLAST_TEAM_ID, "Milin", "Danek"));
        playerDao.create(new Player(BLAST_TEAM_ID, "Andie", "Gregax"));
        playerDao.create(new Player(BLAST_TEAM_ID, "Luke", "Arms"));
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("SoccerManager", "Upgrading database");
		db.execSQL("DROP TABLE IF EXISTS " + PlayerDao.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + GameDao.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + GamePlayerDao.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TeamDao.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ShiftDao.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PlayerShiftDao.TABLE_NAME);
        onCreate(db);
	}

}
