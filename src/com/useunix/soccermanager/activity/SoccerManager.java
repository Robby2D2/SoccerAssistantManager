package com.useunix.soccermanager.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;

//import com.leinardi.kitchentimer.misc.Constants;
import com.useunix.soccermanager.R;

@TargetApi(3)
public class SoccerManager extends Activity {

    private static final int ACTIVITY_PREFERENCES=0;
    private static final int ACTIVITY_PLAYER_LIST=1;
    private static final int ACTIVITY_PLAY_GAME=2;
    public static final String TAG = SoccerManager.class.getSimpleName();
    public static final String INTENT_SHIFT_TIMER_ENDED = SoccerManager.class.getName() + ".INTENT_SHIFT_TIMER_ENDED";

	private Button playerListButton;
	private Button playGameButton;
	private Button settingsButton;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        addPreferencesFromResource(preferencesResId);
        
        Intent intent = new Intent("com.useunix.soccermanager.TIMER_ENDED");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
				SystemClock.elapsedRealtime() + 5 * 60 * 1000, pendingIntent);
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
		wakeLock.setReferenceCounted(false);
		
		
//        SoccerManagerDataHelper dataHelper = new SoccerManagerDataHelper(this);
//        PlayerDao playerDao = new PlayerDao(dataHelper.getWritableDatabase());
//        
        
        playerListButton = (Button) findViewById(R.id.player_list_button);

        final Intent playerListIntent = new Intent(this, PlayerList.class);
        playerListButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
                startActivityForResult(playerListIntent, ACTIVITY_PLAYER_LIST);
        	}
        });
        
       
        
        playGameButton = (Button) findViewById(R.id.play_game_button);
        final Intent playGameIntent = new Intent(this, PlayGame.class);
        playGameButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
                startActivityForResult(playGameIntent, ACTIVITY_PLAY_GAME);
        	}
        });
        
        settingsButton = (Button) findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		showPreferences();
        	}
        });

    }

    public void showPreferences() {
    	final Intent intent = new Intent(this, SoccerManagerPreferencesActivity.class);
        startActivityForResult(intent, ACTIVITY_PREFERENCES);
    }
}
