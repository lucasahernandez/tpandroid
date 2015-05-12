package com.tdam.tpa;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.tdam.tpa.MensajeWebListaActivity.WebMessageAdapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.PhoneLookup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ConectividadListaActivity extends ListActivity {

	private Menu mMenu;
	private ServidorConexion server;
	private ConectividadAdapter _adapter;
	private BDInterface db;

	@SuppressLint("NewApi")
	protected void onResume(){
		super.onResume();
		ArrayList<Conectividad> conecciones = new ArrayList<Conectividad>();
		db = UsuarioEntidad.db;
		server = UsuarioEntidad.server;
		conecciones = db.getAllConnectivityInfo();
		_adapter = new ConectividadAdapter(conecciones);
		getListView().setAdapter(_adapter);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ArrayList<Conectividad> conecciones = new ArrayList<Conectividad>();
		db = UsuarioEntidad.db;
		server = UsuarioEntidad.server;
		conecciones = db.getAllConnectivityInfo();
		_adapter = new ConectividadAdapter(conecciones);

		// TODO usar el layout ejemplo_list_activity
		setContentView(R.layout.list_conectividad);

		getListView().setAdapter(_adapter);

		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				//onConectividadSelected(position);
			}

		});
	}


	class Holder {
		public TextView connection;
		public TextView status;
		public TextView timeStamp;
	}

	class ConectividadAdapter extends BaseAdapter {
		ArrayList<Conectividad> conecciones;
		private LayoutInflater _inflater;

		public ConectividadAdapter(ArrayList<Conectividad> con) {
			conecciones = new ArrayList<Conectividad>();
			conecciones.addAll(con);
			_inflater = LayoutInflater.from(ConectividadListaActivity.this);
		}

		@Override
		public int getCount() {
			return conecciones.size();
		}

		@Override
		public Object getItem(int arg0) {
			return conecciones.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		// position = posicion del elemento que se esta queriendo tomar
		// convertView = componente vacío para almacenar los datos
		public View getView(int position, View convertView, ViewGroup parent) {
			// Holder = contiene todos los punteros a los controles
			Holder holder = null;
			if (convertView == null) {
				// inflater = tomar layout e instanciarlo
				convertView = _inflater.inflate(R.layout.itemconectividad, null);
				holder = new Holder();
				holder.connection = (TextView) convertView.findViewById(R.id.connectionCon);
				holder.status = (TextView) convertView
						.findViewById(R.id.statusCon);
				holder.timeStamp = (TextView) convertView
						.findViewById(R.id.timeStampCon);

				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			Conectividad conectividad = conecciones.get(position);
			//Date fecha = new Date(conectividad.getTimeStamp());
			//SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			holder.connection.setText(conectividad.getConnection());
			holder.status.setText(conectividad.getStatus());
			holder.timeStamp.setText(conectividad.getTimeStamp());

			return convertView;
		}
	}
}
