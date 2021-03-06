package com.coolweather.app.activity;


import com.coolweather.app.R;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener {

	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;
	private TextView publishText;
	private TextView weatherDespText;
	private TextView temp1Text;
	private TextView temp2Text;
	private TextView currentDateText;
	private Button switchButton;
	private Button refreshButton;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.weather_layout);
		
		this.weatherInfoLayout = (LinearLayout)this.findViewById(R.id.weather_info_layout);
		this.cityNameText = (TextView)this.findViewById(R.id.city_name);
		this.publishText = (TextView)this.findViewById(R.id.publish_text);
		this.weatherDespText = (TextView)this.findViewById(R.id.weather_desp);
		this.temp1Text = (TextView)this.findViewById(R.id.temp1);
		this.temp2Text = (TextView)this.findViewById(R.id.temp2);
		this.currentDateText = (TextView)this.findViewById(R.id.current_date);
		//this.switchCity = this.findViewById(R.id.s)
		String countyCode = this.getIntent().getStringExtra("county_code");
		if(!TextUtils.isEmpty(countyCode))
		{
			this.publishText.setText("同步中...");
			this.weatherInfoLayout.setVisibility(View.INVISIBLE);
			this.cityNameText.setVisibility(View.INVISIBLE);
			Log.i("Weather County code", countyCode);
			queryWeatherCode(countyCode);
			//
		}else{
			showWeather();
		}
		
		this.switchButton =(Button)this.findViewById(R.id.switch_city);
		this.refreshButton =(Button)this.findViewById(R.id.refresh_weather);
		
		this.switchButton.setOnClickListener(this);
		this.refreshButton.setOnClickListener(this);
		
		Intent intent = new Intent(this,AutoUpdateService.class);
		this.startService(intent);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.switch_city:
			Intent intent = new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			this.publishText.setText("同步中");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode))
			{
				queryWeatherInfor(weatherCode);
			}
			break;
		default:
			break;
		}
	}
	private void queryWeatherCode(String countyCode)
	{
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		this.queryFromServer(address, "countyCode");
	}
	
	private void queryWeatherInfor(String weatherCode)
	{
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		queryFromServer(address,"weatherCode");
	}
	
	private void queryFromServer(final String address,final String type)
	{
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				if("countyCode".equals(type))
				{
					if(!TextUtils.isEmpty(response))
					{
						String[] array = response.split("\\|");
						if(array!=null && array.length == 2)
						{
							String weatherCode = array[1];
							//Log.i("Weather WeatherCode", weatherCode);
							queryWeatherInfor(weatherCode);
						}
					}
				}else if("weatherCode".equals(type))
				{
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					//Log.i("Weather Weather xinxi", response);
					
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}});
				}
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						publishText.setText("同步失败");
					}});
			}
			
		});
	}

	private void showWeather()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.cityNameText.setText(prefs.getString("city_name", ""));
		this.temp1Text.setText(prefs.getString("temp1", ""));
		this.temp2Text.setText(prefs.getString("temp2", ""));
		this.weatherDespText.setText(prefs.getString("weather_desp", ""));
		this.publishText.setText(prefs.getString("publish_time", "") + "发布");
		this.currentDateText.setText(prefs.getString("current_date", ""));
		this.weatherInfoLayout.setVisibility(View.VISIBLE);
		this.cityNameText.setVisibility(View.VISIBLE);
	}
}
