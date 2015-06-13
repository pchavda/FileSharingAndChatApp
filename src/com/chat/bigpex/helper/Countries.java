package com.chat.bigpex.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Countries {

	private static final String TAG = "Countries";
	private ArrayList<String> countries;
	private String myCountry;
	private Context context;

	public Countries(Context context) {
		this.context = context;
		countries = new ArrayList<String>();
		getCountryCodeFromAvailableLocales();
	}

	// Method shud b made public. Change
	private void getCountryCodeFromAvailableLocales() {
		Locale[] locales = Locale.getAvailableLocales();
		for (Locale locale : locales) {
			String country = locale.getDisplayCountry();
			if (country.trim().length() > 0 && !countries.contains(country)) {
				countries.add(country);
				if (country.equals(Locale.getDefault().getDisplayCountry())) {
					myCountry = country;
					Log.e(TAG, myCountry);
				}
			}
		}
		sortCountries();
	}

	public String getJsonArrayCountryCodeFromFile(int jsonResource) {
		try {
			return ReadFiles.readRawFileAsString(context, jsonResource);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void sortCountries() {
		Collections.sort(countries);
	}

	public int getCount() {
		return countries.size();
	}

	public ArrayList<String> getCountries() {
		return countries;
	}

	public String getMyCountry() {
		return myCountry;
	}

	public static boolean isSimPresent(Context context) {
		// yet to be completed.

		PackageManager pm = context.getPackageManager();
		boolean hasTelephony = pm
				.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
		return hasTelephony;
	}

	public static String getCurrentCountry(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		String countryCode = tm.getSimCountryIso();
		return countryCode;
	}

}
