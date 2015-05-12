package com.tdam.tpa;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConectividadInfoEntidad {

	private Context context;

	public static final int NO_INTERNET_CONNECTION = 0;
	public static final int MOBILE_CONNECTION = 1;
	public static final int WIFI_CONNECTION = 2;

	protected ConectividadInfoEntidad(Context context) {
		this.context = context;
	}

	public boolean isInternetConnectionAvailable() {
		ConnectivityManager connectivity;
		NetworkInfo wifiInfo, mobileInfo;

		connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		wifiInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		mobileInfo = connectivity
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		return (wifiInfo.isConnected() || mobileInfo.isConnected());
	}

	public int verificarConexionInternet() {
		ConnectivityManager connectivity;
		NetworkInfo wifiInfo, mobileInfo;
		int connection;

		connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		wifiInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		mobileInfo = connectivity
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (wifiInfo.isConnected()) {
			connection = WIFI_CONNECTION;
		} else if (mobileInfo.isConnected()) {
			connection = MOBILE_CONNECTION;
		} else {
			connection = NO_INTERNET_CONNECTION;
		}

		return connection;
	}
}
