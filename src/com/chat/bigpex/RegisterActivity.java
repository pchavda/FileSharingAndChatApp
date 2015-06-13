package com.chat.bigpex;

import java.io.File;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.bigpex.gonechat.R;
import com.chat.bigpex.fragments.RegisterFragment;
import com.chat.bigpex.fragments.RegisterStepThree;
import com.chat.bigpex.fragments.VerifyFragment;
import com.chat.bigpex.helper.AppLog;
import com.chat.bigpex.helper.Constant;
import com.chat.bigpex.helper.MyPrefrence;
import com.chat.bigpex.helper.Utils;
import com.chat.bigpex.helper.Constant.Pref;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class RegisterActivity extends FragmentActivity {

	private static final String TAG = "RegisterActivity";

	public String regid;
	public MyPrefrence prefrence;

	private GoogleCloudMessaging gcm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// BugSenseHandler.initAndStartSession(RegisterActivity.this,
		// "85ae07a2");

		prefrence = new MyPrefrence(this);

		gcm = GoogleCloudMessaging.getInstance(RegisterActivity.this);
		manageRegisterId();

		if (prefrence.isRegisterStepThreeClear()) {
			AppLog.Log(TAG, "");
			startActivity(new Intent(this, RecentChatList.class));
			finish();
		} else if (prefrence.isRegisterStepTwoClear()) {
			addFregment(new RegisterStepThree(), true);
		} else if (prefrence.isRegisterStepOneClear()) {
			addFregment(new VerifyFragment(), true);
		} else {
			addFregment(new RegisterFragment(), true);
		}
		
		
		File file = new File(Utils.getBasePath());
		if (!file.exists()) {
			file.mkdir();
		}
	}

	public void addFregment(Fragment fragment, boolean animate) {
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		if (animate) {
			ft.setCustomAnimations(R.anim.slide_in_right,
					R.anim.slide_out_left, R.anim.slide_in_left,
					R.anim.slide_out_right);

		}
		ft.replace(R.id.fregment_container, fragment);
		ft.commit();
	}

	/**
	 * responsible to get gcm reg id
	 */
	private void manageRegisterId() {
		regid = getRegistrationId(this);
		AppLog.Log(TAG, "manageRegisterId :: " + regid);
		if (regid.length() == 0) {
			registerInBackground();
		}
	}

	private void registerInBackground() {
		Void[] params = null;
		new RegisteGCMId().execute(params);
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String registrationId = prefs.getString(Pref.GCM_ID, "");
		if (registrationId.length() == 0) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		return registrationId;
	}

	class RegisteGCMId extends AsyncTask<Void, Void, Void> {

		private ProgressDialog pr;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pr = new ProgressDialog(RegisterActivity.this);
			pr.setMessage("Loading...");
			pr.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				regid = gcm.register(Constant.SENDER_ID);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pr.dismiss();

			if (regid.length() == 0) {
				registerInBackground();
			} else {
				final SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(RegisterActivity.this);
				Editor editor = prefs.edit();
				editor.putString(Pref.GCM_ID, regid);
				editor.commit();
			}
		}
	}

	@Override
	protected void onDestroy() {
		// BugSenseHandler.closeSession(this);
		super.onDestroy();
	}

}
