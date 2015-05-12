package com.tdam.tpa;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


@SuppressLint("NewApi")
public class ConectividadReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(UsuarioEntidad.db!=null){
			BDInterface db = UsuarioEntidad.db;
			Conectividad conn = new Conectividad();
			String action = intent.getAction();
			
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				NetworkInfo network = intent
						.getParcelableExtra((ConnectivityManager.EXTRA_NETWORK_INFO));
				
				conn.setConnection(network.getTypeName() + " " + network.getSubtypeName());
				conn.setStatus(network.getState().name());
	
				Date timeStamp = new Date();
				timeStamp.setTime(timeStamp.getTime());
				SimpleDateFormat formater=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				formater.setTimeZone(TimeZone.getTimeZone("GMT-03"));
				String newDate=formater.format(timeStamp);
				
				conn.setTimeStamp(newDate);
	
				db.saveConnectivity(conn);
		}
		}
	}
}
