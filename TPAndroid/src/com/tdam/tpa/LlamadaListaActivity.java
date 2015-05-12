package com.tdam.tpa;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

import com.tdam.tpa.MensajeListaActivity.MensajesAdapter;

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
import android.provider.BaseColumns;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract.PhoneLookup;
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

public class LlamadaListaActivity extends ListActivity {

	private Menu mMenu;
	private LlamadasAdapter _adapter;
	public static final String PARAMETRO_FECHACALLS = "FechaCalls";
	public static final String PARAMETRO_DURATIONCALLS = "DurationCalls";
	public static final String PARAMETRO_TIPOCALLS = "TipoCalls";
	public static final String PARAMETRO_NOMBRECALLS = "NombreCalls";
	public static final String PARAMETRO_ID = "Id";

	@SuppressLint("NewApi")
	protected void onResume(){
		super.onResume();
		_adapter = new LlamadasAdapter();
		getListView().setAdapter(_adapter);
		_adapter.filter();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		_adapter = new LlamadasAdapter();

		// TODO usar el layout ejemplo_list_activity
		setContentView(R.layout.list_llamadas);

		getListView().setAdapter(_adapter);
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				onLlamadaSelected(position);
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

	protected void onLlamadaSelected(int position) {
		final LlamadaEntidad llamadaSeleccionada = (LlamadaEntidad) _adapter
				.getItem(position);
		CharSequence colors[] = new CharSequence[] { "Ver", "Borrar" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Seleccionar Acción");
		builder.setItems(colors, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					Intent intent = new Intent(LlamadaListaActivity.this, LlamadaDetalleActivity.class);
					intent.putExtra("NombreCalls", _adapter.getContactName(LlamadaListaActivity.this, llamadaSeleccionada.getNumero()));
					intent.putExtra("TipoCalls", llamadaSeleccionada.getTipo());
					intent.putExtra("DurationCalls", _adapter.displayDuration(llamadaSeleccionada.getDuracion()));
					Date fecha = new Date(llamadaSeleccionada.getFecha());
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					sdf.setTimeZone(TimeZone.getTimeZone("GMT-03"));
					String newDate=sdf.format(fecha);
					intent.putExtra("FechaCalls",  newDate);
					intent.putExtra("Id", llamadaSeleccionada.getCodigo());
					startActivity(intent);
					/*Toast.makeText(ListMensajes.this, msjSeleccionado.getbody(), Toast.LENGTH_SHORT)
							.show();*/
				}
				if (which == 1) {
					// Toast.makeText(ListMensajes.this, "Borrar",
					// Toast.LENGTH_SHORT).show();
					deleteCalls(LlamadaListaActivity.this,
							llamadaSeleccionada.getCodigo());
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

	public void deleteCalls(Context context, long id) {
		try {
			context.getContentResolver().delete(Calls.CONTENT_URI,
					BaseColumns._ID + "= ? ",
					new String[] { String.valueOf(id) });
			_adapter = new LlamadasAdapter();
			getListView().setAdapter(_adapter);
		} catch (Exception e) {
			// mLogger.logError("Could not delete SMS from inbox: " +
			// e.getMessage());
		}
	}

	class Holder {
		public TextView duracion;
		public ImageView iconoLlamada;
		public TextView fechaLlamada;
		public TextView person;
	}

	private Cursor getLlamadas() {
		// Run query
		Uri uri = Calls.CONTENT_URI;
		// People.CONTENT_URI;
		String[] projection = new String[] { BaseColumns._ID, Calls.NUMBER,
				Calls.DATE, Calls.DURATION, Calls.TYPE
		// People.NAME,
		// People._ID
		};
		String[] selectionArgs = null;
		String sortOrder = Calls.DATE + " COLLATE LOCALIZED ASC";
		// People.NAME + " COLLATE LOCALIZED ASC";
		Cursor c = getContentResolver().query(uri, projection, null,
				selectionArgs, sortOrder);
		startManagingCursor(c);
		return c;
	}

	class LlamadasAdapter extends BaseAdapter {
		ArrayList<LlamadaEntidad> llamadas;
		ArrayList<LlamadaEntidad> llamadas_filtradas;
		private LayoutInflater _inflater;

		public LlamadasAdapter() {
			llamadas = new ArrayList<LlamadaEntidad>();
			llamadas_filtradas= new ArrayList<LlamadaEntidad>();
			Cursor cursor = getLlamadas();
			getColumnData(cursor);
			_inflater = LayoutInflater.from(LlamadaListaActivity.this);

		}

		private void getColumnData(Cursor cur) {
			if (cur.moveToFirst()) {
				do {
					// Get the field values
					long codigo = cur.getLong(cur
							.getColumnIndexOrThrow(BaseColumns._ID));
					String numero = cur.getString(cur
							.getColumnIndexOrThrow(Calls.NUMBER));
					long fecha = cur.getLong(cur
							.getColumnIndexOrThrow(Calls.DATE));
					long duracion = cur.getLong(cur
							.getColumnIndexOrThrow(Calls.DURATION));
					int tipo = cur
							.getInt(cur.getColumnIndexOrThrow(Calls.TYPE));
					LlamadaEntidad c = new LlamadaEntidad(numero, fecha, duracion, tipo,
							codigo);
					llamadas.add(c);
					llamadas_filtradas.add(c);
				} while (cur.moveToNext());
			}
		}

		@Override
		public int getCount() {
			return llamadas_filtradas.size();
		}

		@Override
		public Object getItem(int arg0) {
			return llamadas_filtradas.get(arg0);
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
				convertView = _inflater.inflate(R.layout.itemllamada, null);
				holder = new Holder();

				holder.duracion = (TextView) convertView
						.findViewById(R.id.duracion);
				holder.person = (TextView) convertView
						.findViewById(R.id.person);
				holder.fechaLlamada = (TextView) convertView
						.findViewById(R.id.fechallamada);
				holder.iconoLlamada = (ImageView) convertView
						.findViewById(R.id.iconoLlamada);

				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			LlamadaEntidad llamada = llamadas_filtradas.get(position);
			Date fecha = new Date(llamada.getFecha());
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT-03"));
			String newDate=sdf.format(fecha);
			holder.person.setText(getContactName(LlamadaListaActivity.this,llamada.getNumero()));
			holder.fechaLlamada.setText("("
					+ newDate + ")");
			holder.duracion.setText(displayDuration(llamada.getDuracion()));
			if (llamada.getTipo() == 1) {
				holder.iconoLlamada.setImageResource(R.drawable.viper32receive);
			}
			if (llamada.getTipo() == 2) {
				holder.iconoLlamada.setImageResource(R.drawable.viper32);
			}
			if (llamada.getTipo() == 3) {
				holder.iconoLlamada.setImageResource(R.drawable.viper32miss);
			}

			return convertView;
		}
		
		private String displayDuration(long seconds){
		  int hr = (int) (seconds/3600);
		  int rem = (int) (seconds%3600);
		  int mn = rem/60;
		  int sec = rem%60;
		  String hrStr = (hr<10 ? "0" : "")+hr;
		  String mnStr = (mn<10 ? "0" : "")+mn;
		  String secStr = (sec<10 ? "0" : "")+sec;
		  String duration=hrStr+ ":"+mnStr+ ":"+secStr;
		  return duration;
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
		    .getDefaultSharedPreferences(LlamadaListaActivity.this);
		   String listPrefs = prefs.getString(namePreferences, "");
		   return listPrefs;
		}
		
		public void filter() {
			String lstCallsOrder= displaySharedPreferences("lstCallsOrder");
			String lstCallsFilter= displaySharedPreferences("lstCallsFilter");
			llamadas_filtradas.clear();
			if (lstCallsFilter.compareTo("") == 0||lstCallsFilter.compareTo("1")==0) {
				llamadas_filtradas.addAll(llamadas);
				if(lstCallsOrder.compareTo("")!=0){
					if(lstCallsOrder.compareTo("2")==0){
						Collections.reverse(llamadas_filtradas);
					}
				}
			} else {
				int tipo=Integer.parseInt(lstCallsFilter)-1;
				for (LlamadaEntidad pic : llamadas) {
					if (pic.getTipo()==tipo) {
						llamadas_filtradas.add(pic);
					}
				}
				if(lstCallsOrder.compareTo("")!=0){
					if(lstCallsOrder.compareTo("2")==0){
						Collections.reverse(llamadas_filtradas);
					}
				}
			}
			notifyDataSetChanged();
		}

	}
}
