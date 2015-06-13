package com.chat.bigpex.helper;

/**
 * @author imtiyaz
 * 
 *         class used for the remove logs time of deployment
 * 
 *         make IS_DEGUG to false to hide all Logs
 * 
 */

public class AppLog {

	public static final boolean IS_DEGUG = true;

	public static final void Log(String tag, String messge) {
		if (IS_DEGUG)
			android.util.Log.i(tag, messge);
	}
}
