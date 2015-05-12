package com.tdam.tpa;

import android.content.Context;

public class ConectividadInfoSingleton {

	private ConectividadInfoEntidad connectivityInfo = null;
	private static ConectividadInfoSingleton connectivityInfoSingleton = null;

	private ConectividadInfoSingleton(Context context) {
		connectivityInfo = new ConectividadInfoEntidad(context.getApplicationContext());
	}

	private static void createInstance(Context context) {
		if (connectivityInfoSingleton == null)
			connectivityInfoSingleton = new ConectividadInfoSingleton(context);
	}

	public static ConectividadInfoSingleton getInstance(Context context) {
		if (connectivityInfoSingleton == null)
			createInstance(context);

		return connectivityInfoSingleton;
	}

	public ConectividadInfoEntidad getConnectivityInformation() {
		return connectivityInfo;
	}
}
