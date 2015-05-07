package com.useunix.soccermanager.receiver;

import java.util.ArrayList;

import android.os.Vibrator;
import com.useunix.soccermanager.R;
import com.useunix.soccermanager.activity.GameShift;
import com.useunix.soccermanager.activity.SoccerManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
	private PowerManager.WakeLock mWakeLock = null;
	private NotificationManager mNotificationManager;
	private SharedPreferences mPrefs;
	private Context mContext;
	private String timerName;

	@Override
	public void onReceive(Context context, Intent intent) {
		// Hold a wake lock for 5 seconds, enough to give any
		// services we start time to take their own wake locks.
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, SoccerManager.TAG);
		mWakeLock.acquire(5000);

		mContext = context;
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		sendTimeIsOverNotification(intent);
	}

	private void sendTimeIsOverNotification(Intent intent) {
		int icon;
		String ns = Context.NOTIFICATION_SERVICE;
		mNotificationManager = (NotificationManager) mContext.getSystemService(ns);
		Long currentShift = intent.getLongExtra(SoccerManager.CURRENT_SHIFT, 0);

		Toast.makeText(mContext, "Time is up!  Shift #" + (currentShift + 1) + " is done!", Toast.LENGTH_LONG).show();
		// Vibrate the mobile phone
		Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(2000);

		Intent notificationIntent = new Intent(mContext, GameShift.class);
		notificationIntent.putExtra("GAME_ID", intent.getLongExtra("GAME_ID", 0));
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		notificationIntent.setAction(Long.toString(System.currentTimeMillis()));
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification.Builder builder = new Notification.Builder(mContext);
		builder.setContentTitle("Scheduled Notification");
		builder.setContentText("Time for a shift change!");
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setAutoCancel(true);
		builder.setContentIntent(pendingIntent);

		mNotificationManager.notify(0, builder.build());
	}

}
