package com.useunix.soccermanager.services;

import android.os.CountDownTimer;

public abstract class StopWatch extends CountDownTimer {

    public StopWatch(long millisInFuture, long countDownInterval) {
    	super(millisInFuture, countDownInterval);
    }
    
    public void stop() {
        cancel();
    }

    public void reset() {

    }    
    
   
}
