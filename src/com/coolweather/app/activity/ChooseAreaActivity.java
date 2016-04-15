package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;






import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	
	private List<Province> provinces;
	private List<City> cities;
	private List<County> counties;
	
	private Province selectedProvince;
	private City selectedCity;
	private int currentLevel;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView)this.findViewById(R.id.list_view);
		titleText = (TextView)this.findViewById(R.id.title_text);
		
		adapter  = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(currentLevel == LEVEL_PROVINCE){
					selectedProvince  = provinces.get(position);
					queryCities();
				}else if(currentLevel == LEVEL_CITY){
					selectedCity = cities.get(position);
					queryCounties();
				}
			}
			
		});
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		this.queryProvinces();
		
	}
	
	private void queryProvinces()
	{
		this.provinces = coolWeatherDB.loadProinces();
		if(this.provinces.size() > 0)
		{
			this.dataList.clear();
			for(Province province : provinces)
			{
				this.dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			this.listView.setSelection(0);
			this.titleText.setText("中国");
			this.currentLevel = LEVEL_PROVINCE;
		}else{
			this.queryFromServer(null, "province");
		}
	}
	
	private void queryCities()
	{
		this.cities = coolWeatherDB.loadCities(this.selectedProvince.getId());
		if(this.cities.size() > 0)
		{
			this.dataList.clear();
			for(City city : cities)
			{
				this.dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			this.listView.setSelection(0);
			this.titleText.setText(selectedProvince.getProvinceName());
			this.currentLevel = LEVEL_CITY;
		}else{
			this.queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	
	private void queryCounties()
	{
		this.counties = coolWeatherDB.loadCounties(this.selectedCity.getId());
		if(this.counties.size() > 0)
		{
			this.dataList.clear();
			for(County county : counties)
			{
				this.dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			this.listView.setSelection(0);
			this.titleText.setText(this.selectedCity.getCityName());
			this.currentLevel = LEVEL_COUNTY;
		}else{
			this.queryFromServer(this.selectedCity.getCityCode(), "county");
		}
	}
	
	private void queryFromServer(final String code,final String type)
	{
		String address;
		if(!TextUtils.isEmpty(code))
		{
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		//address = "http://www.sohu.com";
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result = false;
				if("province".equals(type))
				{
					result = Utility.handleProvinceResponse(coolWeatherDB, response);
				}else if("city".equals(type))
				{
					result = Utility.handleCityResponse(coolWeatherDB, response, selectedProvince.getId());
				}else if("county".equals(type))
				{
					result = Utility.handleCountyResponse(coolWeatherDB, response, selectedCity.getId());
				}
				if(result)
				{
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if("province".equals(type)){
								queryProvinces();
							}else if("city".equals(type))
							{
								queryCities();
							}else if("county".equals(type)){
								queryCounties();
							}
						}
						
					});
				}
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
					
				});
			}
			
		});
	}
	
	private void showProgressDialog()
	{
		if(progressDialog == null)
		{
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载中。。。");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	private void closeProgressDialog()
	{
		if(progressDialog != null)
		{
			progressDialog.dismiss();
		}
	}
	
	@Override
	public void onBackPressed(){
		if(this.currentLevel == LEVEL_COUNTY)
		{
			this.queryCities();
		}else if(this.currentLevel == LEVEL_CITY)
		{
			this.queryProvinces();
		}else{
			finish();
		}
	}
}
