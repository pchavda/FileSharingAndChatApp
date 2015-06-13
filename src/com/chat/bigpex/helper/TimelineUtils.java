package com.chat.bigpex.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import com.chat.bigpex.models.AdModel;
import com.chat.bigpex.models.GetTimelineData;
import com.chat.mainclasses.AndroidAppUtils;
import com.chat.mainclasses.AppConstants;
import com.chat.mainclasses.Callback;
import com.chat.mainclasses.WebServiceHandler;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.drive.internal.ad;

public class TimelineUtils extends Activity {
	private Activity context;
	ProgressDialog pDialog;
	public static List<AdModel> adModel;
	public static final int progress_bar_type = 0;
	AdModel adm;
	Bitmap bitmap;
	
	
	
	public TimelineUtils(Activity ctx) {
		this.context = ctx;
		
	}

	
	public int loadSqureImag() {

		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		
		int width = displayMetrics.widthPixels;
		//int height = displayMetrics.heightPixels;
		return width;
	}

	public String getFilePath(String path, String type) {
		String fileName = path.substring(path.lastIndexOf('/') + 1,
				path.length());
		if (type.equals("1")) {
			File filepath = Environment.getExternalStorageDirectory();
			String img = filepath.getAbsolutePath()
					+ "/Gonechat/Timeline Images/" + fileName;
			return img;
		} else {
			File filepath = Environment.getExternalStorageDirectory();
			String vdo = filepath.getAbsolutePath()
					+ "/Gonechat/Timeline Videos/" + fileName;
			return vdo;
		}

	}

	public boolean isFileExist(String path) {

		File f = new File(path);
		if (f.exists())
			return true;
		else
			return false;
	}
	
	

	public void DownloadFile(String fileURL, String fileName) {
		try {
			pDialog = new ProgressDialog(context);
			pDialog.setMessage("Downloding");
			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pDialog.setCancelable(false);
			pDialog.show();

			String RootDir = Environment.getExternalStorageDirectory()
					+ File.separator + "Gonechat/Timeline Videos";
			File RootFile = new File(RootDir);
			RootFile.mkdir();
			// File root = Environment.getExternalStorageDirectory();
			URL u = new URL(fileURL);
			HttpURLConnection c = (HttpURLConnection) u.openConnection();
			c.setRequestMethod("GET");
			c.setDoOutput(true);
			c.connect();
			FileOutputStream f = new FileOutputStream(new File(RootFile,
					fileName));
			InputStream in = c.getInputStream();
			byte[] buffer = new byte[1024];
			int len1 = 0;

			while ((len1 = in.read(buffer)) > 0) {
				f.write(buffer, 0, len1);
			}
			f.close();

			pDialog.dismiss();

		} catch (Exception e) {

			Log.d("Error....", e.toString());
		}

	}
	
	
	public String storeImage(Bitmap imageData, String filename) {
		File filepath = Environment.getExternalStorageDirectory();
		OutputStream output;
		// Create a new folder in SD Card
		File dir = new File(filepath.getAbsolutePath()
				+ "/Gonechat/Timeline Images/");
		if(!dir.exists()){
			dir.mkdirs();
		}
		// Create a name for the saved image
		File file = new File(dir, filename);
		String path = filepath.getAbsolutePath()
				+ "/Gonechat/Timeline Images/";
		// Show a toast message on successful save
		/*
		 * Toast.makeText(ctx, "Image Saved to SD Card",
		 * Toast.LENGTH_SHORT).show();
		 */try {
			output = new FileOutputStream(file);
			Log.d("Bitmap Image", "" + imageData);
			
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(imageData, 500, 500, true);
			scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, output);
			output.flush();
			output.close();
		}

		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return path + filename;
	}

	
	public void downlodImages(final String path) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.e("Downloading Image path", path);

				String strImageUrl = path;
				Log.d("ImageUrl", path);
				String fileName = strImageUrl.substring(
						strImageUrl.lastIndexOf('/') + 1, strImageUrl.length());
				Log.d("ImageName", fileName);
				File filepath = Environment.getExternalStorageDirectory();
				String img = filepath.getAbsolutePath()
						+ "/Gonechat/Timeline Images/" + fileName;
				if (isFileExist(img)) {
					// do noting;
				} else {

					try {
						InputStream in = new java.net.URL(path).openStream();
						bitmap = BitmapFactory.decodeStream(in);
						storeImage(bitmap, fileName);
						Log.e("Strore Image", "Image stored");
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		});

	}


	public String convertDateToTime(String Date) {
		DateTime d;
		DateTime curruntDate;

		// Calendar c = Calendar.getInstance();
		// DateTimeFormatter sdf = ISODateTimeFormat.dateTime();
		DateTimeFormatter fmt = DateTimeFormat
				.forPattern("yyyy-MM-dd HH:mm:ss");
		// curruntDate = fmt.ge
		curruntDate = DateTime.now(DateTimeZone.getDefault());
		d = fmt.parseDateTime(Date);
		// curruntDate = sdf.parseDateTime(curruntDateTime);
		Log.e("d", "" + d);
		Log.e("curruntDate", "" + curruntDate);

		// Get year
		int yr = Years.yearsBetween(d, curruntDate).getYears();
		Log.e("yr", "" + yr);
		// Get Months
		if (yr > 0) {
			if (yr == 1)
				return "" + yr + " Year ago";
			else
				return "" + yr + " Years ago";
		}
		int monthsr = Months.monthsBetween(d, curruntDate).getMonths();
		if (monthsr > 0 && monthsr < 12) {
			Log.e("monthsr", "" + monthsr);
			if (monthsr == 1)
				return "" + monthsr + " Month ago";
			else
				return "" + monthsr + " Months ago";
		}

		// Get Weeks
		// int weeks = Weeks.weeksBetween(d, curruntDate).getWeeks();
		// Log.e("weeks", ""+weeks);
		// Get Days
		int days = Days.daysBetween(d, curruntDate).getDays();
		Log.e("days", "" + days);
		if (days > 0 && days < 31) {
			if (days == 1)
				return "" + days + " day ago";
			else
				return "" + days + " days ago";
		}
		// Get hours
		int hrs = Hours.hoursBetween(d, curruntDate).getHours();
		Log.e("hrs", "" + hrs);
		if (hrs > 0 && hrs < 24) {
			if (hrs == 1)
				return "" + hrs + " hour ago";
			else
				return "" + hrs + " hours ago";
		}
		// Get Minutes
		int minutes = Minutes.minutesBetween(d, curruntDate).getMinutes();
		Log.e("minutes", "" + minutes);
		if (minutes > 0 && minutes < 60) {
			if (minutes == 1)
				return "" + minutes + " minute ago";
			else
				return "" + minutes + " minutes ago";
		}
		// Get seconds
		long seconds = TimeUnit.MILLISECONDS.toSeconds(curruntDate.getMillis() - d.getMillis());
		Log.e("seconds in millis", "" + seconds);
		long timeSeconds = seconds / 1000000;
		Log.e("timeSeconds", "" + timeSeconds);
		if (timeSeconds == 1)
			return "" + timeSeconds + " second ago";
		else
			return "" + timeSeconds + " seconds ago";
	}

	public String getCurruntDateTime(){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datetime = df.format(c.getTime());
		Log.d("datetimeMAIN", datetime);
		return datetime;
	}
	
	
	

	
	
	////////////////////////////////////// new Methods ////////////////////////////////////////////
	
	public Bitmap downloadBitmap(String url) {
	    final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
	    final HttpGet getRequest = new HttpGet(url);
		String fileName = url.substring(
				url.lastIndexOf('/') + 1, url.length());

	    try {
	        HttpResponse response = client.execute(getRequest);
	        final int statusCode = response.getStatusLine().getStatusCode();
	        if (statusCode != HttpStatus.SC_OK) { 
	            Log.w("ImageDownloader", "Error " + statusCode + 
	               " while retrieving bitmap from " + url); 
	            return null;
	        }

	        final HttpEntity entity = response.getEntity();
	        if (entity != null) {
	            InputStream inputStream = null;
	            try {
	                inputStream = entity.getContent(); 
	                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
	                storeImage(bitmap, fileName);
	                return bitmap;
	            } finally {
	                if (inputStream != null) {
	                    inputStream.close();  
	                }
	                entity.consumeContent();
	            }
	        }
	    } catch (Exception e) {
	        // Could provide a more explicit error message for IOException or
	        // IllegalStateException
	        getRequest.abort();
	        Log.e("ImageDownloader", "Error while retrieving bitmap from " + url +
	           e.toString());
	    } finally {
	        if (client != null) {
	            client.close();
	        }
	    }
	    return null;
	}
	
	
	public AdModel getAddURL(){
		String URL = "http://janmeshjani.com/gonechat/get_ad.php";
		
		WebServiceHandler.WebServiceCall(context, URL, AppConstants.GET,null, AppConstants.content_json, true, "Fetching", "", new Callback() {
			
			@Override
			public void run(Object result, int statusCode) {
				// TODO Auto-generated method stub
				
				ObjectMapper mapper = AndroidAppUtils.getJsonMapper();
				try {
					adModel = mapper.readValue((String) result,
							new TypeReference<List<AdModel>>() {
						
							});
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					Log.e("AddJsondata",adModel.get(0).getId());
					 adm = new AdModel();
					adm.setId(adModel.get(0).getId());
					adm.setAd_image(adModel.get(0).getAd_image());
					adm.setAd_url(adModel.get(0).getAd_url());
		
			}
		});
		//Log.e("AddJsondata",adModel.get(0).getId());
		return adm;
		
	}
	////////////////////////////////////////////////////////////////////////////////////////////////
}
