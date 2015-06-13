package com.chat.bigpex.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.chat.bigpex.helper.Constant.Pref;
import com.chat.bigpex.models.UserDetail;

public class MyPrefrence {

	private SharedPreferences preferences;

	public MyPrefrence(Context context) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public void saveDetail(UserDetail detail) {
		Editor editor = preferences.edit();
		editor.putInt(Pref.USER_ID, detail.getUserId());
		editor.putString(Pref.USER_NAME, detail.getName());
		editor.putString(Pref.USER_IMAGE, detail.getImage());
		editor.putString(Pref.USER_PHONE_NO, detail.getPhoneNo());
		editor.putString(Pref.USER_PHONE_CODE, detail.getPhoneCode());
		editor.putString(Pref.USER_STATUS, detail.getStatus());
		editor.putString(Pref.USER_EMAIL, detail.getEmail());
		editor.commit();
	}

	public UserDetail getDetail() {
		UserDetail detail = new UserDetail();
		detail.setUserId(preferences.getInt(Pref.USER_ID, -1));
		detail.setName(preferences.getString(Pref.USER_NAME, null));
		detail.setImage(preferences.getString(Pref.USER_IMAGE, null));
		detail.setStatus(preferences.getString(Pref.USER_STATUS, null));
		detail.setEmail(preferences.getString(Pref.USER_EMAIL, null));
		detail.setPhoneNo(preferences.getString(Pref.USER_PHONE_NO, null));
		detail.setPhoneCode(preferences.getString(Pref.USER_PHONE_CODE, null));
		return detail;
	}

	public boolean isRegisterStepOneClear() {
		return preferences.getBoolean(Pref.REGISTER_STEP_ONE, false);
	}

	public boolean isRegisterStepTwoClear() {
		return preferences.getBoolean(Pref.REGISTER_STEP_TWO, false);
	}

	public boolean isRegisterStepThreeClear() {
		return preferences.getBoolean(Pref.REGISTER_STEP_THREE, false);
	}

	// how google maps is working
	// [18:20:21] Voicu Ioan: and maybe we can implement same
	// [18:20:45] Voicu Ioan: let's suppose we have one Street, called Street
	// Patrick
	// [18:21:04] Voicu Ioan: this name street we have it ion 2 different cities
	// [18:21:14] Voicu Ioan: New York and Miami
	// [18:21:52] Voicu Ioan: if I am in Miami, and I saearch in Google Maps
	// "Street Patrick", google maps will display Street Patrick from Miami
	// [18:22:26] Voicu Ioan: in our app, I must insert all the time City,
	// otherwise, I will get not good result
	// [18:22:33] Voicu Ioan: can you get my point?

}
