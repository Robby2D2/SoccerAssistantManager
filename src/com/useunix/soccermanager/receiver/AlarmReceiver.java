package com.useunix.soccermanager.receiver;

import java.util.ArrayList;

import com.useunix.soccermanager.R;
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
		
		sendTimeIsOverNotification();

		int numSeconds = mPrefs.getInt(mContext.getString(R.string.shift_length_key), 300);
		
//		if (mPrefs.getBoolean(mContext.getString(R.string.shift_length_key), false)){
//			SharedPreferences.Editor editor = mPrefs.edit();
//			editor.putString(Constants.PREF_TIMERS_NAMES[timer], mContext.getString(R.string.timer) + " " + timer);
//			editor.commit();
//		}
//
//		if (mPrefs.getBoolean(mContext.getString(R.string.pref_show_tips_key), true)){
//			Toast toast = Toast.makeText(mContext, mContext.getString(R.string.tip1), Toast.LENGTH_LONG);
//			toast.setGravity(Gravity.TOP, 0, 0);
//			toast.show();
//		}

		Intent mIntent = new Intent(mContext, SoccerManager.class);
		mIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

		mContext.startActivity(mIntent);
	}

	private void sendTimeIsOverNotification() {
		int icon;
		String ns = Context.NOTIFICATION_SERVICE;
		mNotificationManager = (NotificationManager) mContext.getSystemService(ns);

//		icon = android.R.drawable.notify_alarm_default;
//		CharSequence mTickerText = timerName + " - " + mContext.getResources().getString(R.string.app_name);
//		long when = System.currentTimeMillis();
//		Notification notification = new Notification(icon, mTickerText, when);
//		notification.number = timer + 1;
//
//		CharSequence mContentTitle = timerName;
//		CharSequence mContentText = mContext.getResources().getString(R.string.countdown_ended);
//
//		Intent clickIntent = new Intent(mContext, MainActivity.class);
//		clickIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP
//				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, clickIntent,
//				PendingIntent.FLAG_UPDATE_CURRENT);
//		notification.setLatestEventInfo(mContext, mContentTitle, mContentText, contentIntent);
//
//		String defaultNotification = "android.resource://com.leinardi.kitchentimer/" + R.raw.mynotification;
//		if (mPrefs.getBoolean(mContext.getString(R.string.pref_notification_sound_key), true)) {
//			if (mPrefs.getBoolean(mContext.getString(R.string.pref_notification_custom_sound_key), false)) {
//				String customNotification = mPrefs.getString(mContext
//						.getString(R.string.pref_notification_ringtone_key), defaultNotification);
//				if (!customNotification.equals(defaultNotification)) {
//					notification.sound = Uri.parse(customNotification);
//				}
//			} else {
//				notification.sound = Uri.parse(defaultNotification);
//			}
//		}
//		if (mPrefs.getBoolean(mContext.getString(R.string.pref_notification_insistent_key), true))
//			notification.flags |= Notification.FLAG_INSISTENT;
//		if (mPrefs.getBoolean(mContext.getString(R.string.pref_notification_vibrate_key), true)) {
//			String mVibratePattern = mPrefs.getString(mContext
//					.getString(R.string.pref_notification_vibrate_pattern_key), "");
//			if (!mVibratePattern.equals("")) {
//				notification.vibrate = parseVibratePattern(mVibratePattern);
//			} else {
//				notification.defaults |= Notification.DEFAULT_VIBRATE;
//			}
//		}
//		if (mPrefs.getBoolean(mContext.getString(R.string.pref_notification_led_key), true)) {
//			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
//			notification.ledARGB = Color.parseColor(mPrefs.getString(mContext
//					.getString(R.string.pref_notification_led_color_key), "red"));
//			int mLedBlinkRate = Integer.parseInt(mPrefs.getString(mContext
//					.getString(R.string.pref_notification_led_blink_rate_key), "2"));
//			notification.ledOnMS = 500;
//			notification.ledOffMS = mLedBlinkRate * 1000;
//		}
//
//		notification.flags |= Notification.FLAG_AUTO_CANCEL;
//
//		mNotificationManager.notify(timer + 10, notification);
	}

	/**
	 * Parse the user provided custom vibrate pattern into a long[] Borrowed
	 * from SMSPopup
	 */
	public static long[] parseVibratePattern(String stringPattern) {
		ArrayList<Long> arrayListPattern = new ArrayList<Long>();
		Long l;
		String[] splitPattern = stringPattern.split(",");
		int VIBRATE_PATTERN_MAX_SECONDS = 60000;
		int VIBRATE_PATTERN_MAX_PATTERN = 100;

		for (int i = 0; i < splitPattern.length; i++) {
			try {
				l = Long.parseLong(splitPattern[i].trim());
			} catch (NumberFormatException e) {
				return null;
			}
			if (l > VIBRATE_PATTERN_MAX_SECONDS) {
				return null;
			}
			arrayListPattern.add(l);
		}

		int size = arrayListPattern.size();
		if (size > 0 && size < VIBRATE_PATTERN_MAX_PATTERN) {
			long[] pattern = new long[size];
			for (int i = 0; i < pattern.length; i++) {
				pattern[i] = arrayListPattern.get(i);
			}
			return pattern;
		}

		return null;
	}
}
