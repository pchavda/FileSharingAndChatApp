package com.chat.mainclasses;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.util.Log;

import com.bigpex.gonechat.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;


public class WebServiceHandler {
	
	static SharedPreferences myPrefs;
	static AsyncHttpClient client;
	static ProgressDialog pDialog;

	static Logger log;
	static SimpleDateFormat format;
	static Date date;
	
	static {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}
	
	public static void WebServiceCall(final Context context, final String URL, String Method, StringEntity se,
			final String Content_Type, final boolean ShowProgressBar,
			final String ProgressMessage, String paginationID,
			final Callback callBack){
		
		 
		log = (Logger) LoggerFactory.getLogger(WebServiceHandler.class);
		format = new SimpleDateFormat("hh:mm:ss");
		date = new Date();

		Log.i("URL ", URL);
		client = new AsyncHttpClient();
		client.setTimeout(60000);
		
		if (ShowProgressBar){
			pDialog = new ProgressDialog(context);

		myPrefs = context.getSharedPreferences(
				context.getString(R.string.pref_file_name),
				Context.MODE_PRIVATE);
		
		if (!paginationID.equals("")) {
			client.addHeader("LAST_PAGE", paginationID);
			Log.i("LAST_PAGE ID : ", paginationID);
		}
	}else {
		client.addHeader("Content-Type", Content_Type);
		if (!paginationID.equals("")) {
			client.addHeader("LAST_PAGE", paginationID);
		}
	
	}
		
		if(Method.equals(AppConstants.POST)){
			
			client.post(context, URL, se, Content_Type, new AsyncHttpResponseHandler() {
				
		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			
			
			log.debug(URL + " start TIME : "
					+ format.format(date.getTime()));

			if (ShowProgressBar) {
				pDialog.setMessage(ProgressMessage);
				pDialog.setCancelable(false);
				pDialog.show();
			}
		}
				
				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					// TODO Auto-generated method stub
					
					if (ShowProgressBar) {
						pDialog.dismiss();
					}
					log.debug(URL + " END TIME : "
							+ format.format(date.getTime()));
					Log.d("JSON RESPONSE : ", new String(arg2));
					callBack.run(new String(arg2), arg0);
					
				}
				
				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					if (ShowProgressBar) {
						pDialog.dismiss();
					}
					Log.e("CODE : ", "" + arg0);

					((org.slf4j.Logger) log).debug(URL + " END TIME : "
							+ format.format(date.getTime()));
					
				}
			});
			
		} else if (Method.equals(AppConstants.GET)) {
			client.get(context, URL, new AsyncHttpResponseHandler() {
				
				
				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					super.onStart();
					
					log.debug(URL + " START TIME : "
							+ format.format(date.getTime()));

					if (ShowProgressBar) {
						pDialog.setMessage(ProgressMessage);
						pDialog.setCancelable(false);
						pDialog.show();
					}
				}
				
				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					// TODO Auto-generated method stub
					if (ShowProgressBar) {
						pDialog.dismiss();
					}
					log.debug(URL + " END TIME : "
							+ format.format(date.getTime()));

					Log.d("JSON RESPONSE : ", new String(arg2));
					callBack.run(new String(arg2), arg0);
				}
				
				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					// TODO Auto-generated method stub
					
					if (ShowProgressBar) {
						pDialog.dismiss();
					}

					// Check for device id not exist
					log.debug(URL + " END TIME : "
							+ format.format(date.getTime()));
					// response.............................
					//Log.d("JSON RESPONSE : ", new String(arg2));
					
				}
				
				public void onRetry(int retryNo) {
					// TODO Auto-generated method stub
					super.onRetry(retryNo);
					Log.i("RETRY NO : 	", "" + retryNo);
				}
			});
		}else {
			
		}	
}
}
