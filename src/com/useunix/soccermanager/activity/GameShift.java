package com.useunix.soccermanager.activity;

import java.text.SimpleDateFormat;
import java.util.*;

import android.annotation.TargetApi;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.useunix.soccermanager.R;
import com.useunix.soccermanager.domain.*;
import com.useunix.soccermanager.receiver.AlarmReceiver;
import com.useunix.soccermanager.services.ShiftManager;

@TargetApi(3)
public class GameShift extends Activity {
	private static final String CURRENT_SHIFT_ID_KEY = "CURRENT_SHIFT_ID_KEY";
	private static final String CURRENT_VIEW_SHIFT_ID_KEY = "CURRENT_VIEW_SHIFT_ID_KEY";
	private static final String SHIFT_MANAGER_KEY = "SHIFT_MANAGER_KEY";

	private static final int DEFAULT_SHIFT_LENGTH = 300;
	
	private SoccerManagerDataHelper dataHelper;
    private PlayerDao playerDao;
    private GameDao gameDao;
    private ShiftDao shiftDao;
    private PlayerShiftDao playerShiftDao;
    private TextView headerText;
	private TextView countDown;
	private Button nextButton;
	private Button currentButton;
	private Button previousButton;
	private Button startButton;
	private Button stopShiftButton;
	private Button setAsCurrentShiftButton;
	private CountDownTimer countDownTimer;
	private Long currentShift;
	private Long currentlyViewingShift;
	private ShiftManager shiftManager;
	
	private ListView attendingPlayerListView;
	private int shiftLength = DEFAULT_SHIFT_LENGTH;
	private Game game;
	private AlarmManager alarmManager;
    private PendingIntent alarmIntent;


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
        gameDao = new GameDao(dataHelper.getWritableDatabase());
        shiftDao = new ShiftDao(dataHelper.getWritableDatabase());
        playerShiftDao = new PlayerShiftDao(dataHelper.getWritableDatabase());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String shiftLengthKey = getString(R.string.shift_length_key);
		String shiftLengthValue = sharedPreferences.getString(shiftLengthKey,  String.valueOf(DEFAULT_SHIFT_LENGTH));
		shiftLength = Integer.valueOf(shiftLengthValue);
        
        initCurrentButton();
        initNextButton();
        initPreviousButton();
        initStartButton();
        initStopShiftButton();
		initSetAsCurrentShiftButton();

        attendingPlayerListView = (ListView)findViewById(R.id.attendingPlayers);

		loadFromExistingGame(getIntent().getExtras().getLong("GAME_ID"));
		
        if (game.getCurrentShiftId() == null) {
        	game.setCurrentShiftId(0l);
        }
        if (currentlyViewingShift == null) {
        	currentlyViewingShift = getCurrentShift();
        }
        updateAttendingPlayerList(currentlyViewingShift);
    }

	private void loadFromExistingGame(Long gameId) {
		if (gameId <= 0 ) { throw new RuntimeException("Could not start shift with no game specified."); }

		Game game = gameDao.getGame(gameId);
		if (game == null) { throw new RuntimeException("Could not find game with ID : " + gameId + "."); }

		this.game = game;
		shiftManager = createShiftManager(game);
		currentlyViewingShift = getCurrentShift();
	}

	private ShiftManager createShiftManager(Game game) {
		List<Player> attendingPlayers = playerDao.getAttendingPlayers(game.getId());
		shiftManager = new ShiftManager(game.getId(), attendingPlayers);

		List<Shift> shiftsForGame = shiftDao.getAllForGame(game.getId());
		HashMap<Long, Date> shiftStartTimes = new HashMap<Long, Date>();
		for (Shift shift : shiftsForGame) {
			List<PlayerShift> playerShifts = playerShiftDao.getAllForShift(shift.getId());
			List<PlayerMetric> playerMetricsForShift = new ArrayList<PlayerMetric>();
			for (PlayerShift playerShift : playerShifts) {
				playerMetricsForShift.add(new PlayerMetric(shift.getRank(), Position.valueOf(playerShift.getPosition()), findPlayer(attendingPlayers, playerShift.getPlayerId())));
			}
			shiftManager.updateMetrics(shift.getRank(), playerMetricsForShift);
			shiftStartTimes.put(shift.getRank(), shift.getStartTime());
		}
		shiftManager.setShiftStartTimes(shiftStartTimes);
		return shiftManager;
	}

	// Return from current list of players or DB
	private Player findPlayer(List<Player> players, Long playerId) {
		for (Player player : players) {
			if (player.getId() == playerId) {
				return player;
			}
		}

		return playerDao.getPlayer(playerId);
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong(CURRENT_SHIFT_ID_KEY, getCurrentShift());
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
        SoccerManager.updateTitle(this);
        AlarmReceiver.stopAlarm(this);
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
				updateAttendingPlayerList(getCurrentShift());
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
				startShift();
			}
		});
	}

	private void initStopShiftButton() {
        final Activity mContext = this;
		stopShiftButton = (Button) findViewById(R.id.stopShiftButton);
		stopShiftButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
                stopCurrentTimer();
                AlarmReceiver.stopAlarm(mContext);
            }
		});
	}

	private void initSetAsCurrentShiftButton() {
		setAsCurrentShiftButton = (Button) findViewById(R.id.setAsCurrentShiftButton);
		setAsCurrentShiftButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopCurrentTimer();
                setCurrentShift(currentlyViewingShift);
            }
        });
	}

    private void setCurrentShift(Long shiftId) {
        game.setCurrentShiftId(shiftId);
        gameDao.update(game);
        updateAttendingPlayerList(getCurrentShift());
    }

    private void stopCurrentTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        cancelNotification();
    }

    private void startShift() {
        stopCurrentTimer();
		shiftManager.startShift(getCurrentShift());
		createNewCountdownTimer();
		Shift shift = shiftDao.getShiftByGameIdAndRank(game.getId(), getCurrentShift());
		if (shift == null) {
			shift = shiftDao.create(new Shift(new Date(), game.getId(), getCurrentShift()));
		}
		shiftDao.update(shift);

		playerShiftDao.deleteAllForShift(shift.getId());
		List<PlayerMetric> playersForShift = shiftManager.getPlayersForShift(getCurrentShift());
		for (PlayerMetric playerMetric : playersForShift) {
			PlayerShift playerShift = new PlayerShift(shift.getId(), playerMetric.getPlayer().getId(),  playerMetric.getPosition().name());
			playerShiftDao.create(playerShift);
		}
		updateShiftTimerHeaderText();
	}

	private void updateAttendingPlayerList(Long shiftId) {
		currentlyViewingShift = shiftId;
		updateShiftTimerHeaderText();
		ArrayList<String> items = new ArrayList<String>();
		
		List<PlayerMetric> playersForShift = shiftManager.getPlayersForShift(shiftId);
		for (PlayerMetric playerMetric : playersForShift) {
			Player player = playerMetric.getPlayer();
			items.add(player.getFirstName() + " " + player.getLastName() + "\n\t\t" + playerMetric.getPosition().getName());
		}
        
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.game_shift_row, items.toArray(new String[]{})) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (position % 2 == 1) {
                    view.setBackgroundColor(0xFF111111);
                } else {
                    view.setBackgroundColor(0xFF222222);
                }
                return view;
            }
        };
        attendingPlayerListView.setAdapter(adapter);
	}

	private void updateShiftTimerHeaderText() {
		final SimpleDateFormat sdf = new SimpleDateFormat("hh:mma");

		Date startTimeForShift = shiftManager.getStartTimeForShift(currentlyViewingShift);
		String startTimeForShiftString = startTimeForShift == null ? "" : " started at " + sdf.format(startTimeForShift);

		headerText.setText("Current shift is #" + (getCurrentShift() + 1) + "\nViewing shift #" + (currentlyViewingShift + 1) + startTimeForShiftString);
	}


	private void createNewCountdownTimer() {
        final Time time = new Time();

        Date startTimeForShift = shiftManager.getStartTimeForShift(getCurrentShift());
        long timeLeftInShift = (shiftLength * 1000) - (System.currentTimeMillis() - startTimeForShift.getTime());
        countDownTimer = new CountDownTimer(timeLeftInShift, 1000) {
            public void onTick(long millisUntilFinished) {
                time.set(millisUntilFinished);
                countDown.setText(time.format("%M:%S"));
            }

            public void onFinish() {
                countDown.setText("Time's up!");
                setCurrentShift(getCurrentShift() + 1);
                updateAttendingPlayerList(getCurrentShift());
            }
        };
        countDownTimer.start();
        scheduleNotification("Shift #" + (getCurrentShift() + 1) + " timer", timeLeftInShift);
    }

    private Long getCurrentShift() {
        return game != null ? game.getCurrentShiftId() : 0l;
    }

    private void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);

        if (alarmIntent != null) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(alarmIntent);
        }
    }

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void scheduleNotification(String content, long delay) {
		Intent notificationIntent = new Intent(this, GameShift.class);
		notificationIntent.putExtra("GAME_ID", game.getId());
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		notificationIntent.setAction(Long.toString(System.currentTimeMillis()));
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification.Builder builder = new Notification.Builder(this);
		builder.setContentTitle("Scheduled Notification");
		builder.setContentText(content);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setAutoCancel(true);
		builder.setContentIntent(pendingIntent);

		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0, builder.build());

		Intent intent = new Intent(SoccerManager.INTENT_SHIFT_TIMER_ENDED);
		intent.putExtra(SoccerManager.CURRENT_SHIFT, getCurrentShift());
		intent.putExtra("GAME_ID", game.getId());
        alarmIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
		long futureInMillis = SystemClock.elapsedRealtime() + delay;
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, alarmIntent);
//		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);


	}

}