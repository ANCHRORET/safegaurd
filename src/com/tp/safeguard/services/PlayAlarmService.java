package com.tp.safeguard.services;

import com.tp.safeguard.R;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class PlayAlarmService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		MediaPlayer mediaPlayer=MediaPlayer.create(this, R.raw.alarm);
		mediaPlayer.setVolume(1.0f, 1.0f);
		mediaPlayer.setLooping(true);
		mediaPlayer.start();
	}
}
