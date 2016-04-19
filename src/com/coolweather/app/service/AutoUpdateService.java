package com.coolweather.app.service;

import com.coolweather.app.receiver.AutoUpdateReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent,int flags,int startId)
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateWeather();
			}
			
		});
		AlarmManager manager = (AlarmManager)this.getSystemService(ALARM_SERVICE);
		int anHour = 8 *60 *60 *10000;
		long trigerAtTime = SystemClock.elapsedRealtime() + anHour;
		Intent i = new Intent(this,AutoUpdateReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, trigerAtTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}

	private void updateWeather()
	{
		
	}
}
