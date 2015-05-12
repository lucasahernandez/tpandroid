package com.tdam.tpa;

import java.util.ArrayList;
import java.util.Collections;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.ContactsContract.Contacts;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class ContactoListaActivity extends ListActivity {

	private Menu mMenu;
	private ContactosAdapter _adapter;
	public static final String PARAMETRO_NOMBRE = "Nombre";
	public static final String PARAMETRO_ID = "Id";

	@SuppressLint("NewApi")
	protected void onResume(){
		super.onResume();
		_adapter = new ContactosAdapter();
		getListView().setAdapter(_adapter);
		EditText editTxt = (EditText) findViewById(R.id.edit);
		_adapter.filter(editTxt.getEditableText().toString());
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		_adapter = new ContactosAdapter();

		// TODO usar el layout ejemplo_list_activity
		setContentView(R.layout.list_contactos);

		getListView().setAdapter(_adapter);
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				onContactoSelected(position);
			}

		});
		getListView().setTextFilterEnabled(true);
		
		EditText editTxt = (EditText) findViewById(R.id.edit);

		editTxt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				//System.out.println("Text [" + s + "]");
				_adapter.filter(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}
	
	private String displaySharedPreferences(String namePreferences) {
	   SharedPreferences prefs = PreferenceManager
	    .getDefaultSharedPreferences(ContactoListaActivity.this);
	   String listPrefs = prefs.getString(namePreferences, "");
	   return listPrefs;
	}

	protected void onContactoSelected(int position) {
		ContactoEntidad conSeleccionado = (ContactoEntidad) _adapter.getItem(position);
		Intent intent = new Intent(ContactoListaActivity.this, ContactoDetalleActivity.class);
		intent.putExtra("Nombre", conSeleccionado.getApellidoYNombre());
		intent.putExtra("Id", conSeleccionado.getCodigo());
		startActivity(intent);
	}

	class Holder {
		public TextView txtApellidoYNombre;
		public ImageView iconoContacto;
	}

	@SuppressLint("NewApi")
	private Cursor getContacts() {
		// Run query
		Uri uri = Contacts.CONTENT_URI;
		String[] projection = new String[] { BaseColumns._ID,
				Contacts.DISPLAY_NAME
		};
		String[] selectionArgs = null;
		String sortOrder = Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
		Cursor c = getContentResolver().query(uri, projection, null,
				selectionArgs, sortOrder);
		startManagingCursor(c);
		return c;
	}

	class ContactosAdapter extends BaseAdapter {
		ArrayList<ContactoEntidad> contactos = null;
		ArrayList<ContactoEntidad> contactos_filtrados = null;
		private LayoutInflater _inflater;

		public ContactosAdapter() {
			contactos = new ArrayList<ContactoEntidad>();
			contactos_filtrados = new ArrayList<ContactoEntidad>();
			Cursor cursor = getContacts();
			getColumnData(cursor);
			_inflater = LayoutInflater.from(ContactoListaActivity.this);
		}

		private void getColumnData(Cursor cur) {
			if (cur.moveToFirst()) {
				do {
					// Get the field values
					long id = cur.getLong(cur
							.getColumnIndexOrThrow(BaseColumns._ID));
					String displayName = cur.getString(cur
							.getColumnIndexOrThrow(Contacts.DISPLAY_NAME));
					ContactoEntidad c = new ContactoEntidad(displayName, id);
					contactos.add(c);
					contactos_filtrados.add(c);

				} while (cur.moveToNext());
			}
		}

		@Override
		public int getCount() {
			return contactos_filtrados.size();
		}

		@Override
		public Object getItem(int arg0) {
			return contactos_filtrados.get(arg0);
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
				convertView = _inflater.inflate(R.layout.itemcontacto, null);
				holder = new Holder();

				holder.txtApellidoYNombre = (TextView) convertView
						.findViewById(R.id.txtApellidoYNombre);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			ContactoEntidad contacto = contactos_filtrados.get(position);

			holder.txtApellidoYNombre.setText(contacto.getApellidoYNombre());

			return convertView;
		}

		public void filter(String charText) {
			String lstContactsOrder= displaySharedPreferences("lstContactsOrder");
			charText = charText.toLowerCase();
			contactos_filtrados.clear();
			if (charText.length() == 0) {
				contactos_filtrados.addAll(contactos);
				if(lstContactsOrder.compareTo("")!=0){
					if(lstContactsOrder.compareTo("2")==0){
						Collections.reverse(contactos_filtrados);
					}
				}
			} else {
				for (ContactoEntidad pic : contactos) {
					if (pic.getApellidoYNombre().toLowerCase()
							.startsWith(charText)
							|| pic.getApellidoYNombre().toLowerCase()
									.contains(" " + charText)) {
						contactos_filtrados.add(pic);
					}
				}
				if(lstContactsOrder.compareTo("")!=0){
					if(lstContactsOrder.compareTo("2")==0){
						Collections.reverse(contactos_filtrados);
					}
				}
			}
			notifyDataSetChanged();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Hold on to this
		mMenu = menu;

		// Inflate the currently selected menu XML resource.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menucontactos, menu);

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
}
