package com.tdam.tpa;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.tdam.tpa.UsuarioPreferencias;
import com.tdam.tpa.BDInterface;
import com.tdam.tpa.UsuarioEntidad;
import com.tdam.tpa.MensajeWebEntidad;
import com.tdam.tpa.ServidorConexion;
import com.tdam.tpa.ContactoListaActivity.Holder;
import com.tdam.tpa.LlamadaListaActivity.LlamadasAdapter;
import com.tdam.tpa.MensajeListaActivity.MensajesAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.ContactsContract.Contacts;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MensajeWebListaActivity extends ListActivity {

	private ServidorConexion server;
	private WebMessageAdapter _adapter;
	private BDInterface db;
	private Menu mMenu;
	public static final String PARAMETRO_TIPOMSGWEB = "TipoMsgWeb";
	public static final String PARAMETRO_FECHAMSGWEB = "FechaMsgWeb";
	public static final String PARAMETRO_BODYMSGWEB = "BodyMsgWeb";
	public static final String PARAMETRO_NOMBREMSGWEB = "NombreMsgWeb";
	public static final String PARAMETRO_ID = "Id";

	@SuppressLint("NewApi")
	protected void onResume(){
		super.onResume();
		ArrayList<MensajeWebEntidad> messages = db.getMessages();
		_adapter = new WebMessageAdapter(messages);
		getListView().setAdapter(_adapter);
		_adapter.filter();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_mensajesweb);
		ArrayList<MensajeWebEntidad> messages = new ArrayList<MensajeWebEntidad>();
		db = UsuarioEntidad.db;
		server = UsuarioEntidad.server;
		messages = db.getMessages();
		_adapter = new WebMessageAdapter(messages);
		getListView().setAdapter(_adapter);

		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				onMensajeWebSelected(position);
			}

		});

	}
	
	protected void onMensajeWebSelected(int position) {
		final int pos=position;
		final MensajeWebEntidad msjWebSeleccionado = (MensajeWebEntidad) _adapter.getItem(position);
		CharSequence colors[] = new CharSequence[] { "Ver", "Borrar" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Seleccionar Acción");
		builder.setItems(colors, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					Intent intent = new Intent(MensajeWebListaActivity.this, MensajeWebDetalleActivity.class);
					intent.putExtra("NombreMsgWeb", msjWebSeleccionado.getUser());
					intent.putExtra("TipoMsgWeb", msjWebSeleccionado.getDirection());
					intent.putExtra("BodyMsgWeb", msjWebSeleccionado.getMessage());
					Date fecha = msjWebSeleccionado.getTimeStamp();
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					intent.putExtra("FechaMsgWeb",  sdf.format(fecha));
					intent.putExtra("Id", 0);
					startActivity(intent);
					/*Toast.makeText(ListMensajes.this, msjSeleccionado.getbody(), Toast.LENGTH_SHORT)
							.show();*/
				}
				if (which == 1) {
					//Toast.makeText(MensajeWebListaActivity.this, msjWebSeleccionado.getTimeStamp()+"",Toast.LENGTH_SHORT).show();
					db.deleteSingleMessage(msjWebSeleccionado);
					ArrayList<MensajeWebEntidad> messages = new ArrayList<MensajeWebEntidad>();
					messages = db.getMessages();
					_adapter = new WebMessageAdapter(messages);
					getListView().setAdapter(_adapter);
					_adapter.filter();
				}
			}
		});
		builder.show();
		/*
		 * Intent intent = new Intent(ListContactos.this,
		 * ContactoDetalle.class);
		 * intent.putExtra("Nombre",conSeleccionado.getApellidoYNombre());
		 * intent.putExtra("Id",conSeleccionado.getCodigo());
		 * startActivity(intent);
		 */
		// Toast.makeText(this, conSeleccionado.getApellidoYNombre(),
		// Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Hold on to this
		mMenu = menu;

		// Inflate the currently selected menu XML resource.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menuservidor, menu);

		return true;
	}
	
	private int getLastRenew() {
		int lastRenew=0;
		String lstMsgWebOrder= displaySharedPreferences("lstMsgWebOrder");
		if(lstMsgWebOrder.compareTo("")!=0){
			if(lstMsgWebOrder.compareTo("1")==0){
				lastRenew=_adapter.getCount()-1;
			}
		}
		else{
			lastRenew=_adapter.getCount()-1;
		}
		return lastRenew;
	}
	
	private void onRenew() {
		MensajeWebEntidad lastelement = null;
		Date lastRenew = null;
		try {
			lastelement = ((MensajeWebEntidad) _adapter.getItem(getLastRenew()));
			lastRenew = lastelement.getTimeStamp();
		} catch (IndexOutOfBoundsException e) {
			lastRenew = new Date(0);
		}
		ArrayList<MensajeWebEntidad> messages = server.getMessages(lastRenew);
		db.saveMessages(messages);
		messages = db.getMessages();
		_adapter = new WebMessageAdapter(messages);
		getListView().setAdapter(_adapter);
		_adapter.filter();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// case R.id.email_visibility:
		// The reply item is part of the email group
		// final boolean shouldShowEmail =
		// !mMenu.findItem(R.id.reply).isVisible();
		// mMenu.setGroupVisible(R.id.email, shouldShowEmail);
		// break;
		case R.id.refreshmsgweb:
			onRenew();
			return true;
		case R.id.estadisticas:
			Intent estadisticasActivity = new Intent(this,EstadisticasActivity.class);
			startActivity(estadisticasActivity);
			return true;
		case R.id.conectividad:
			Intent conectividadActivity = new Intent(this,ConectividadListaActivity.class);
			startActivity(conectividadActivity);
			return true;
		case R.id.opciones:
    		Intent settingsActivity = new Intent(this,UsuarioPreferencias.class);
			startActivity(settingsActivity);
			return true;
		// Generic catch all for all the other menu resources
		default:
			// Don't toast text when a submenu is clicked
			if (!item.hasSubMenu()) {
				Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT)
						.show();
				return true;
			}
			break;
		}
		return false;
	}
	
	class Holder {
		public TextView webbody;
		public TextView fechawebsms;
		public TextView webperson;
		public ImageView iconoWebMensaje;
	}
	
	class WebMessageAdapter extends BaseAdapter {
		ArrayList<MensajeWebEntidad> messagesweb;
		ArrayList<MensajeWebEntidad> messagesweb_filtrados = null;
		private LayoutInflater _inflater;

		public WebMessageAdapter(ArrayList<MensajeWebEntidad> messages) {
			messagesweb = new ArrayList<MensajeWebEntidad>();
			messagesweb_filtrados= new ArrayList<MensajeWebEntidad>();
			messagesweb.addAll(messages);
			messagesweb_filtrados.addAll(messages);
			_inflater = LayoutInflater.from(MensajeWebListaActivity.this);
		}

		@Override
		public int getCount() {
			return messagesweb_filtrados.size();
		}

		@Override
		public Object getItem(int arg0) {
			return messagesweb_filtrados.get(arg0);
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
				convertView = _inflater.inflate(R.layout.itemmensajeweb, null);
				holder = new Holder();
				holder.iconoWebMensaje = (ImageView) convertView
						.findViewById(R.id.iconoWebMensaje);
				holder.webbody = (TextView) convertView.findViewById(R.id.webbody);
				holder.fechawebsms = (TextView) convertView
						.findViewById(R.id.fechawebsms);
				holder.webperson = (TextView) convertView
						.findViewById(R.id.webperson);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			MensajeWebEntidad msg = messagesweb_filtrados.get(position);
			holder.webbody.setText(msg.getMessage());
			Date fecha = new Date(msg.getTimeStamp().toString());
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			holder.fechawebsms.setText("(" + sdf.format(fecha) + ")");
			holder.webperson.setText(msg.getUser());
			if (msg.getDirection()==false) {
				holder.iconoWebMensaje.setImageResource(R.drawable.identi32sent);
			} else {
				holder.iconoWebMensaje.setImageResource(R.drawable.identi32);
			}

			return convertView;
		}
		
		
			
			public void filter() {
				String lstMsgWebOrder= displaySharedPreferences("lstMsgWebOrder");
				String lstMsgWebFilter= displaySharedPreferences("lstMsgWebFilter");
				messagesweb_filtrados.clear();
				if (lstMsgWebFilter.compareTo("") == 0||lstMsgWebFilter.compareTo("1")==0) {
					messagesweb_filtrados.addAll(messagesweb);
					if(lstMsgWebOrder.compareTo("")!=0){
						if(lstMsgWebOrder.compareTo("2")==0){
							Collections.reverse(messagesweb_filtrados);
						}
					}
				} else {
					boolean dir=false;
					if(lstMsgWebFilter.compareTo("3")==0){
						dir=true;
					}
					for (MensajeWebEntidad pic : messagesweb) {
						if (pic.getDirection()==dir) {
							messagesweb_filtrados.add(pic);
						}
					}
					if(lstMsgWebOrder.compareTo("")!=0){
						if(lstMsgWebOrder.compareTo("2")==0){
							Collections.reverse(messagesweb_filtrados);
						}
					}
				}
				notifyDataSetChanged();
			}
	}
	private String displaySharedPreferences(String namePreferences) {
		   SharedPreferences prefs = PreferenceManager
		    .getDefaultSharedPreferences(MensajeWebListaActivity.this);
		   String listPrefs = prefs.getString(namePreferences, "");
		   return listPrefs;
		}

}
