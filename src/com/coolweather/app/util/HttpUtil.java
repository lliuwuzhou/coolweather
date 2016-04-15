package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;



public class HttpUtil {
	
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection connection  = null;
				try{
					URL url = new URL(address);
					Log.i("Http", address);
					connection = (HttpURLConnection)url.openConnection();
					Log.i("Http", "open httpconn");
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					Log.i("Http", "get input before");
					InputStream in = connection.getInputStream();
					Log.i("Http", "get input after");
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while((line = reader.readLine()) != null)
					{
						response.append(line);
					}
					Log.i("Http", "Http request finished");
					if(listener != null){
						listener.onFinish(response.toString());
					}
					Log.i("Http", "response ck");
					
				}catch(Exception e)
				{
					Log.i("HTTP",e.getMessage());
					if(listener != null)
					{
						listener.onError(e);
					}
				}finally{
					if(connection != null)
					{
						connection.disconnect();
					}
				}
			}
			
		}).start();
		
	}

}
