package com.chat.mainclasses;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkCheck {
	
	
	private Context _context;

	public NetWorkCheck() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NetWorkCheck(Context _context) {
		super();
		this._context = _context;
	}

	public boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) _context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (NetworkInfo element : info)
					if (element.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}

}
