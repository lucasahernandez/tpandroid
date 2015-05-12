package com.tdam.tpa;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.tdam.tpa.BDInterface;
import com.tdam.tpa.R.color;
import com.tdam.tpa.ServidorConexion;
import com.tdam.tpa.UsuarioEntidad;
import com.tdam.tpa.MensajeWebServicio.Notificable;

public class EstructuraTabActivity extends TabActivity implements Notificable {
	private MensajeWebServicio _service;
	private BDInterface db;
	private UsuarioEntidad user;
	private ServidorConexion server;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab);

		Resources res = getResources(); // Resource object to get Drawables
		final TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab
        
		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, ContactoListaActivity.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost
				.newTabSpec("contactos")
				.setIndicator("Contactos",
						res.getDrawable(R.layout.ic_tab_contactos))
				.setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, MensajeListaActivity.class);
		spec = tabHost
				.newTabSpec("mensajes")
				.setIndicator("Mensajes",
						res.getDrawable(R.layout.ic_tab_mensajes))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, LlamadaListaActivity.class);
		spec = tabHost
				.newTabSpec("llamadas")
				.setIndicator("Llamadas",
						res.getDrawable(R.layout.ic_tab_llamadas))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, MensajeWebListaActivity.class);
		spec = tabHost
				.newTabSpec("servidor")
				.setIndicator("Servidor Web",
						res.getDrawable(R.layout.ic_tab_mensajesweb))
				.setContent(intent);
		tabHost.addTab(spec);
		tabHost.setBackgroundResource(R.drawable.android3);
		tabHost.setCurrentTab(0);
		for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
	    {
		    if (i == 0) {
		    	tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#FF8800"));
		    	TextView tv = (TextView) tabHost.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
		    	tv.setTextColor(Color.parseColor("#FFFFFF"));	 
	        }
	
		    else tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.android3);
	    } 
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener(){
			@Override
			public void onTabChanged(String tabId) {
			    // TODO Auto-generated method stub
			     for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
		        {
			    	 tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.android3); // selected
		        }
			    tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#FF8800"));	
			    TextView tv = (TextView) tabHost.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
		        tv.setTextColor(Color.parseColor("#FFFFFF"));	       
			}
		});
		startService(new Intent(EstructuraTabActivity.this, MensajeWebServicio.class));
	}

	@Override
	public boolean notificar(int acccion, Bundle datos) {
		// TODO Auto-generated method stub
		return false;
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			_service = ((MensajeWebServicio.LocalBinder) service).getService();
			_service.bind(EstructuraTabActivity.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			_service = null;
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		bindService(new Intent(EstructuraTabActivity.this, MensajeWebServicio.class), mConnection, 0);

	}

	@Override
	protected void onPause() {
		super.onPause();
		_service.unBind(this);
		unbindService(mConnection);
	}

	
	private String displaySharedPreferences(String namePreferences) {
	   SharedPreferences prefs = PreferenceManager
	    .getDefaultSharedPreferences(EstructuraTabActivity.this);
	   String listPrefs = prefs.getString(namePreferences, "");
	   return listPrefs;
	}
}
