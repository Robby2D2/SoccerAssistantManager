package com.useunix.soccermanager.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.useunix.soccermanager.R;
import com.useunix.soccermanager.domain.Player;
import com.useunix.soccermanager.domain.PlayerDao;
import com.useunix.soccermanager.domain.PlayerMetric;
import com.useunix.soccermanager.domain.SoccerManagerDataHelper;
import com.useunix.soccermanager.services.ShiftManager;

@TargetApi(3)
public class GameShift extends Activity {
	private static final String CURRENT_SHIFT_ID_KEY = "CURRENT_SHIFT_ID_KEY";
	private static final String CURRENT_VIEW_SHIFT_ID_KEY = "CURRENT_VIEW_SHIFT_ID_KEY";
	private static final String SHIFT_MANAGER_KEY = "SHIFT_MANAGER_KEY";

	private static final int DEFAULT_SHIFT_LENGTH = 300;
	
	private SoccerManagerDataHelper dataHelper;
    private PlayerDao playerDao;
    private TextView headerText;
	private TextView countDown;
	private Button nextButton;
	private Button currentButton;
	private Button previousButton;
	private Button startButton;
	private CountDownTimer countDownTimer;
	private Long currentShift;
	private Long currentlyViewingShift;
	private ShiftManager shiftManager;
	
	private ListView attendingPlayerListView;
	private int shiftLength = DEFAULT_SHIFT_LENGTH;
	private Long gameId;
	private AlarmManager alarmManager;
	
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_shift);
        
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        
        headerText = (TextView) findViewById(R.id.play_game_header_text);
        
        countDown = (TextView) findViewById(R.id.countDown);
        
        dataHelper = new SoccerManagerDataHelper(this);
        playerDao = new PlayerDao(dataHelper.getWritableDatabase());
     
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String shiftLengthKey = getString(R.string.shift_length_key);
		String shiftLengthValue = sharedPreferences.getString(shiftLengthKey,  String.valueOf(DEFAULT_SHIFT_LENGTH));
		shiftLength = Integer.valueOf(shiftLengthValue);
        
        initCurrentButton();
        initNextButton();
        initPreviousButton();
        initStartButton();
        
        attendingPlayerListView = (ListView)findViewById(R.id.attendingPlayers);
        
    	Intent intent = getIntent();
		gameId = intent.getExtras().getLong("GAME_ID");
		if (gameId <= 0 ) { throw new RuntimeException("Could not start shift with no game specified."); }

		if (savedInstanceState != null) {
			shiftManager = (ShiftManager) savedInstanceState.getSerializable(SHIFT_MANAGER_KEY);
			currentShift = savedInstanceState.getLong(CURRENT_SHIFT_ID_KEY);
			currentlyViewingShift = savedInstanceState.getLong(CURRENT_VIEW_SHIFT_ID_KEY);
		}

		if (shiftManager == null) {
			List<Player> attendingPlayers = playerDao.getAttendingPlayers(gameId);
			shiftManager = new ShiftManager(attendingPlayers);
		}
		
        if (currentShift == null) {
        	currentShift = 0l;
        }
        if (currentlyViewingShift == null) {
        	currentlyViewingShift = currentShift;
        }
        updateAttendingPlayerList(currentlyViewingShift);
    }


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong(CURRENT_SHIFT_ID_KEY, currentShift);
		outState.putLong(CURRENT_VIEW_SHIFT_ID_KEY, currentlyViewingShift);
		outState.putParcelable(SHIFT_MANAGER_KEY, shiftManager);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initPreviousButton() {
		previousButton = (Button) findViewById(R.id.seePreviousShift);
        previousButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (currentlyViewingShift > 0) {
					updateAttendingPlayerList(currentlyViewingShift - 1);
				}
			}
		});
	}
	
	private void initCurrentButton() {
		currentButton = (Button) findViewById(R.id.seeCurrentShift);
		currentButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				updateAttendingPlayerList(currentShift);
			}
		});
	}
	
	private void initNextButton() {
		nextButton = (Button) findViewById(R.id.seeNextShift);
		nextButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				updateAttendingPlayerList(currentlyViewingShift + 1);
			}
		});
	}
	
	private void initStartButton() {
		startButton = (Button) findViewById(R.id.startShiftTimerButton);
		startButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (countDownTimer != null) {
					countDownTimer.cancel();
				}
				createNewCountdownTimer();
			}
		});
	}

	private void updateAttendingPlayerList(Long shiftId) {
		currentlyViewingShift = shiftId;
		headerText.setText("Currently viewing shift #" + (currentlyViewingShift + 1));
		ArrayList<String> items = new ArrayList<String>();
		
		List<PlayerMetric> playersForShift = shiftManager.getPlayersForShift(shiftId);
		for (PlayerMetric playerMetric : playersForShift) {
			Player player = playerMetric.getPlayer();
			items.add(player.getFirstName() + " " + player.getLastName() + " - " + playerMetric.getPosition().getName());
		}
        
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, items.toArray(new String[]{}));
        attendingPlayerListView.setAdapter(adapter);
	}
    

	private void createNewCountdownTimer() {
		final Time time = new Time();
		final Date date = new Date();

        countDownTimer = new CountDownTimer(shiftLength * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
            	time.set(millisUntilFinished);
            	countDown.setText(time.format("%M:%S") + " started at " + date.toString());
            }
            public void onFinish() {
            	countDown.setText("Time's up!");
				currentShift++;
				updateAttendingPlayerList(currentShift);
            }
         };
         countDownTimer.start();
         
         Intent intent = new Intent(SoccerManager.INTENT_SHIFT_TIMER_ENDED);
//			intent.putExtra(Constants.TIMER, timer);
			// intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
         PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
			
         alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock
					.elapsedRealtime()
					+ shiftLength * 1000, pendingIntent);
	}

	
	
}