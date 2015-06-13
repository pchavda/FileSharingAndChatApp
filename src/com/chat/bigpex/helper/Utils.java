package com.chat.bigpex.helper;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Toast;

import com.bigpex.gonechat.R;
import com.chat.bigpex.internet.Urls;

@SuppressLint("SimpleDateFormat")
public class Utils {

	public static final void showNoInternetMessage(Context context) {
		Toast.makeText(context,
				context.getString(R.string.error_no_internet_connection),
				Toast.LENGTH_SHORT).show();
	}
	
	public static final void showTimeLineMessageSend(Context context) {
		Toast.makeText(context,
				context.getString(R.string.text_timeline_message_send),
				Toast.LENGTH_SHORT).show();
	}

	public static final String getBasePath() {
		return Environment.getExternalStorageDirectory() + "/"
				+ Constant.ROOT_FOLDER_NAME;
	}

	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public static final void showToast(String message, Context context) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static String getRealPathFromURI(Uri contentURI, Activity context) {
		String[] projection = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = context.managedQuery(contentURI, projection, null,
				null, null);
		if (cursor == null)
			return null;
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		if (cursor.moveToFirst()) {
			String s = cursor.getString(column_index);
			// cursor.close();
			return s;
		}
		// cursor.close();
		return null;
	}

	public static String getCurrentTimeInFormate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String newFormat = formatter.format(new Date());
		AppLog.Log("newFormat", "Formate ::" + newFormat);
		return newFormat;
	}

	// public static final String convertDateIntoMilli(String date) {
	// // 0000-00-00 00:00:00
	// SimpleDateFormat formatter = new SimpleDateFormat("yyy-MM-dd hh:mm:ss");
	// try {
	// Date expiry = formatter.parse(date);
	// Calendar calendar = Calendar.getInstance();
	// calendar.setTime(expiry);
	// return calendar.getTimeInMillis() + "";
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return System.currentTimeMillis() + "";
	// }

	public static final Bitmap getBitmapFromFile(String photoPath) {
		return BitmapFactory.decodeFile(photoPath);
	}

	public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
		int width = image.getWidth();
		int height = image.getHeight();

		float bitmapRatio = (float) width / (float) height;
		if (bitmapRatio > 0) {
			width = maxSize;
			height = (int) (width / bitmapRatio);
		} else {
			height = maxSize;
			width = (int) (height * bitmapRatio);
		}
		return Bitmap.createScaledBitmap(image, width, height, true);
	}

	public static final Bitmap resizeBitmap(int newWidth, int newHeight,
			Bitmap bm) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);
		bm.recycle();
		bm = null;
		return resizedBitmap;
	}

	public static final String encodeToBase64(Bitmap image) {
		Bitmap immagex = image;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
		return imageEncoded;
	}

	public static final Bitmap decodeBase64(String input) {
		byte[] decodedByte = Base64.decode(input, 0);
		return BitmapFactory
				.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

	public static final int generateRandom(int min, int max) {
		return (int) ((Math.random() * (max - min)) + min);
	}

	// public static final String requestForCamera(Activity activity,
	// int requestCode) {
	// File file = new File(Environment.getExternalStorageDirectory()
	// .getAbsolutePath()
	// + File.separator
	// + activity.getString(R.string.app_name));
	// if (!file.exists()) {
	// file.mkdir();
	// }
	//
	//
	//
	// return null;
	// }
	public static final String convertDateIntoMilli(String date) {
		// 0000-00-00 00:00:00
		SimpleDateFormat formatter = new SimpleDateFormat("yyy-MM-dd hh:mm:ss");
		try {
			Date expiry = formatter.parse(date);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(expiry);
			return calendar.getTimeInMillis() + "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return System.currentTimeMillis() + "";
	}

	public static final String convertDateIntoLocalMilli(String date) {
		// 0000-00-00 00:00:00
		SimpleDateFormat formatter = new SimpleDateFormat("yyy-MM-dd hh:mm:ss");
		try {
			Date expiry = formatter.parse(date);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(expiry);
			return calendar.getTimeInMillis() + "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return System.currentTimeMillis() + "";
	}

	
	
}