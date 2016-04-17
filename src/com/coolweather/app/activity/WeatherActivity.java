package com.coolweather.app.activity;


import com.coolweather.app.R;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
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
	private Button switchCity;
	private Button refreshWeather;
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
		String contryCode = this.getIntent().getStringExtra("county_code");
		if(!TextUtils.isEmpty(contryCode))
		{
			this.publishText.setText("同步中...");
			this.weatherInfoLayout.setVisibility(View.INVISIBLE);
			//
		}else{
			
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
	private void queryWeatherCode(String countyCode)
	{
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
	}
	
	private void queryWeatherInfor(String weatherCode)
	{
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		queryFromServer(address,"countyCode");
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
							queryWeatherInfor(weatherCode);
						}
					}
				}else if("weatherCode".equals(type))
				{
					Utility.handleWeatherResponse(WeatherActivity.this, response);
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
		
	}
}
