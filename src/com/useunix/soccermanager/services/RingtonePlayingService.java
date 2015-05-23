/*
 * Copyright (c)  2015 Danek Consulting Company
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.useunix.soccermanager.services;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import com.useunix.soccermanager.R;

@TargetApi(Build.VERSION_CODES.ECLAIR)
public class RingtonePlayingService extends Service {
    private Ringtone ringtone;
    private Vibrator vibrator;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Uri ringtoneUri = Uri.parse(intent.getExtras().getString("ringtone-uri"));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String vibrateKey = getApplicationContext().getString(R.string.alarm_vibrate_key);
        boolean shouldVibrate = sharedPreferences.getBoolean(vibrateKey, false);
        String alarmSoundKey = getApplicationContext().getString(R.string.alarm_sound_key);
        boolean shouldAlarmSound = sharedPreferences.getBoolean(alarmSoundKey, false);

        if (shouldAlarmSound) {
            Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (ringtoneUri == null) {
                ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                if (ringtoneUri == null) {
                    ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                }
            }
            this.ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
            ringtone.play();
        }

        if (shouldVibrate) {
            vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(5000);

        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (ringtone != null) {
            ringtone.stop();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
    }
}
