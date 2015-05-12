package com.tdam.tpa;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

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

public class MensajeListaActivity extends ListActivity {

	private Menu mMenu;
	private MensajesAdapter _adapter;
	public static final String PARAMETRO_TIPOSMS = "TipoSms";
	public static final String PARAMETRO_FECHASMS = "FechaSms";
	public static final String PARAMETRO_BODYSMS = "BodySms";
	public static final String PARAMETRO_NOMBRESMS = "NombreSms";
	public static final String PARAMETRO_ID = "Id";

	@SuppressLint("NewApi")
	protected void onResume(){
		super.onResume();
		_adapter = new MensajesAdapter();
		getListView().setAdapter(_adapter);
		_adapter.filter();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// _adapter = new ContactosAdapter();
		_adapter = new MensajesAdapter();

		// TODO usar el layout ejemplo_list_activity
		setContentView(R.layout.list_mensajes);

		getListView().setAdapter(_adapter);

		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				onMensajeSelected(position);
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Hold on to this
		mMenu = menu;

		// Inflate the currently selected menu XML resource.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menufiltros, menu);

		return true;
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
	
			// Generic catch all for all the other menu resources
			/*case R.id.refresh:
				_adapter = new MensajesAdapter();
				getListView().setAdapter(_adapter);
				return true;*/
			case R.id.opciones:
	    		Intent settingsActivity = new Intent(this,UsuarioPreferencias.class);
				startActivity(settingsActivity);
				return true;
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

	protected void onMensajeSelected(int position) {
		final MensajeEntidad msjSeleccionado = (MensajeEntidad) _adapter.getItem(position);
		CharSequence colors[] = new CharSequence[] { "Ver", "Borrar" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Seleccionar Acción");
		builder.setItems(colors, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					Intent intent = new Intent(MensajeListaActivity.this, MensajeDetalleActivity.class);
					intent.putExtra("NombreSms", _adapter.getContactName(MensajeListaActivity.this, msjSeleccionado.getperson()));
					intent.putExtra("TipoSms", msjSeleccionado.gettype());
					intent.putExtra("BodySms", msjSeleccionado.getbody());
					Date fecha = new Date(msjSeleccionado.getdate());
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					sdf.setTimeZone(TimeZone.getTimeZone("GMT-03"));
					String newDate=sdf.format(fecha);
					intent.putExtra("FechaSms",  newDate);
					intent.putExtra("Id", msjSeleccionado.getid());
					startActivity(intent);
					/*Toast.makeText(ListMensajes.this, msjSeleccionado.getbody(), Toast.LENGTH_SHORT)
							.show();*/
				}
				if (which == 1) {
					// Toast.makeText(ListMensajes.this, "Borrar",
					// Toast.LENGTH_SHORT).show();
					deleteSMS(MensajeListaActivity.this, msjSeleccionado.getid());
				}
				// the user clicked on colors[which]
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

	public void deleteSMS(Context context, long id) {
		try {
			context.getContentResolver().delete(
					Uri.parse("content://sms/" + id), null, null);
			_adapter = new MensajesAdapter();
			getListView().setAdapter(_adapter);
		} catch (Exception e) {
			// mLogger.logError("Could not delete SMS from inbox: " +
			// e.getMessage());
		}
	}

	class Holder {
		public TextView body;
		public TextView fechasms;
		public TextView person;
		public ImageView iconoMensajes;
	}

	private Cursor getMensajes() {
		// Run query
		Uri uri = Uri.parse("content://sms");
		Cursor c = getContentResolver().query(
				uri,
				new String[] { "_id", "date", "address", "body", "person",
						"type" }, null, null, null);
		startManagingCursor(c);
		// People.CONTENT_URI;
		return c;
	}

	class MensajesAdapter extends BaseAdapter {
		ArrayList<MensajeEntidad> mensajes;
		ArrayList<MensajeEntidad> mensajes_filtrados;
		private LayoutInflater _inflater;

		public MensajesAdapter() {
			mensajes = new ArrayList<MensajeEntidad>();
			mensajes_filtrados=new ArrayList<MensajeEntidad>();
			Cursor cursor = getMensajes();
			// Toast.makeText(ListMensajes.this, cursor.getCount()+"",
			// Toast.LENGTH_SHORT).show();
			if (cursor != null) {
				getColumnData(cursor);
			}
			_inflater = LayoutInflater.from(MensajeListaActivity.this);

		}

		private void getColumnData(Cursor cur) {
			if (cur.moveToFirst()) {
				do {
					// Get the field values
					long id = cur.getLong((cur.getColumnIndexOrThrow("_id")));
					long date = cur.getLong(cur.getColumnIndexOrThrow("date"));
					String address = cur.getString(cur
							.getColumnIndexOrThrow("address"));
					String body = cur.getString(cur.getColumnIndex("body"));
					String type = cur.getString(cur.getColumnIndex("type"));
					// long person=cur.getLong(cur.getColumnIndex("person"));
					String person = getContactName(getApplicationContext(),
							cur.getString(cur.getColumnIndexOrThrow("address")));
					MensajeEntidad c = new MensajeEntidad(date, address, body, person,
							type, id);
					mensajes.add(c);
					mensajes_filtrados.add(c);
				} while (cur.moveToNext());
			}
		}

		@Override
		public int getCount() {
			return mensajes_filtrados.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mensajes_filtrados.get(arg0);
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
				convertView = _inflater.inflate(R.layout.itemmensaje, null);
				holder = new Holder();
				holder.iconoMensajes = (ImageView) convertView
						.findViewById(R.id.iconoMensaje);
				holder.body = (TextView) convertView.findViewById(R.id.body);
				holder.fechasms = (TextView) convertView
						.findViewById(R.id.fechasms);
				holder.person = (TextView) convertView
						.findViewById(R.id.person);

				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			MensajeEntidad mensaje = mensajes_filtrados.get(position);
			Date fecha = new Date(mensaje.getdate());
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT-03"));
			String newDate=sdf.format(fecha);
			holder.person.setText(mensaje.getperson());
			holder.body.setText(mensaje.getbody());// +" "+mensaje.gettype());
			holder.fechasms.setText("(" + newDate + ")");
			if (mensaje.gettype().compareTo("2") == 0) {
				holder.iconoMensajes.setImageResource(R.drawable.identi32sent);
			} else {
				holder.iconoMensajes.setImageResource(R.drawable.identi32);
			}

			return convertView;
		}

		@SuppressLint("NewApi")
		public String getContactName(Context context, String phoneNumber) {
			ContentResolver cr = context.getContentResolver();
			Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
					Uri.encode(phoneNumber));
			Cursor cursor = cr
					.query(uri, new String[] { PhoneLookup.DISPLAY_NAME },
							null, null, null);
			if (cursor == null) {
				return null;
			}
			String contactName = phoneNumber;
			if (cursor.moveToFirst()) {
				contactName = cursor.getString(cursor
						.getColumnIndex(PhoneLookup.DISPLAY_NAME));
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			return contactName;
		}
		
		private String displaySharedPreferences(String namePreferences) {
			   SharedPreferences prefs = PreferenceManager
			    .getDefaultSharedPreferences(MensajeListaActivity.this);
			   String listPrefs = prefs.getString(namePreferences, "");
			   return listPrefs;
			}
			
			public void filter() {
				String lstMsgOrder= displaySharedPreferences("lstMsgOrder");
				String lstMsgFilter= displaySharedPreferences("lstMsgFilter");
				mensajes_filtrados.clear();
				if (lstMsgFilter.compareTo("") == 0||lstMsgFilter.compareTo("1")==0) {
					mensajes_filtrados.addAll(mensajes);
					if(lstMsgOrder.compareTo("")!=0){
						if(lstMsgOrder.compareTo("2")==0){
							Collections.reverse(mensajes_filtrados);
						}
					}
				} else {
					int tipo=Integer.parseInt(lstMsgFilter)-1;
					
					for (MensajeEntidad pic : mensajes) {
						if (pic.gettype().compareTo(String.valueOf(tipo))==0) {
							mensajes_filtrados.add(pic);
						}
					}
					if(lstMsgOrder.compareTo("")!=0){
						if(lstMsgOrder.compareTo("2")==0){
							Collections.reverse(mensajes_filtrados);
						}
					}
				}
				notifyDataSetChanged();
			}
	}
}
