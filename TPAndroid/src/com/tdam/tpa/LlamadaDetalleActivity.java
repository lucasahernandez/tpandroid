package com.tdam.tpa;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LlamadaDetalleActivity extends Activity {

	private long id;
	//private NumerosAdapter _adapter;
	//private ListView listaTelefonos, listaEmail;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		id = Long.parseLong(String.valueOf(getIntent().getExtras().getLong(
				MensajeListaActivity.PARAMETRO_ID)));
		//_adapter = new NumerosAdapter(id);

		// TODO usar el layout ejemplo_list_activity
		setContentView(R.layout.detallellamada);

		ImageView iconoLlamada = (ImageView) findViewById(R.id.iconoMensajeD);
		TextView persona = (TextView) findViewById(R.id.txtPersonaCallsDetalle);
		TextView fecha = (TextView) findViewById(R.id.txtFechaCallsDetalle);
		TextView duration = (TextView) findViewById(R.id.txtDuracionCallsDetalle);
		// Devuelve el intent con el cual esta actividad fue iniciada
		// (getIntent)
		persona.setText(getIntent().getExtras().getString(
				LlamadaListaActivity.PARAMETRO_NOMBRECALLS));
		fecha.setText(getIntent().getExtras().getString(
				LlamadaListaActivity.PARAMETRO_FECHACALLS));
		duration.setText(getIntent().getExtras().getString(
				LlamadaListaActivity.PARAMETRO_DURATIONCALLS));
		int tipo=getIntent().getExtras().getInt(
				LlamadaListaActivity.PARAMETRO_TIPOCALLS);
		if (tipo == 1) {
			iconoLlamada.setImageResource(R.drawable.viper32receive);
		}
		if (tipo == 2) {
			iconoLlamada.setImageResource(R.drawable.viper32);
		}
		if (tipo == 3) {
			iconoLlamada.setImageResource(R.drawable.viper32miss);
		}
		/*listaTelefonos = (ListView) findViewById(R.id.listT);
		listaTelefonos.setAdapter(_adapter);*/
	}

	/*private void onContactoSms(int position) {
		// Toast.makeText(this,""+_adapter.getItem(position),
		// Toast.LENGTH_SHORT).show();
		Numero num = (Numero) _adapter.getItem(position);
		String number = num.getNumero();
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms",
				number, null)));

	};

	private void onContactoCall(int position) {
		Numero num = (Numero) _adapter.getItem(position);
		String number = num.getNumero();
		String uri = "tel:" + number.trim();
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse(uri));
		startActivity(intent);
		// Toast.makeText(this,""+_adapter.getItem(position),
		// Toast.LENGTH_SHORT).show();
	};

	private void onContactoEmail(int position) {
		Numero num = (Numero) _adapter.getItem(position);
		String email_address = num.getNumero();

		String to = email_address;
		String subject = "";
		String message = "";

		Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
		email.putExtra(Intent.EXTRA_SUBJECT, subject);
		email.putExtra(Intent.EXTRA_TEXT, message);

		// need this to prompts email client only
		email.setType("message/rfc822");

		startActivity(Intent.createChooser(email, "Choose an Email client"));
		// Toast.makeText(this,""+_adapter.getItem(position),
		// Toast.LENGTH_SHORT).show();
	};

	@SuppressLint("NewApi")
	private Cursor getNumeros(long id) {
		Uri uri = Phone.CONTENT_URI;
		Cursor c = getContentResolver().query(uri,
				new String[] { Phone.NUMBER }, Phone.CONTACT_ID + "=" + id,
				null, null);
		startManagingCursor(c);
		return c;
	}

	@SuppressLint("NewApi")
	private Cursor getEmails(long id) {
		Uri uri = Email.CONTENT_URI;
		Cursor c = getContentResolver().query(uri, new String[] { Email.DATA },
				Email.CONTACT_ID + "=" + id, null, null);
		startManagingCursor(c);
		return c;
	}

	class Holder {
		public TextView txtNumero;
	}

	class NumerosAdapter extends BaseAdapter {
		ArrayList<Numero> numeros;
		private LayoutInflater _inflater;

		public NumerosAdapter(long idC) {
			numeros = new ArrayList<Numero>();
			Cursor cursor = getNumeros(idC);
			getColumnData(cursor, "telefono");
			Cursor cursor2 = getEmails(idC);
			getColumnData2(cursor2, "email");
			_inflater = LayoutInflater.from(MensajeDetalle.this);

		}

		private void getColumnData(Cursor cur, String typeNumber) {
			if (cur.moveToFirst()) {
				do {
					// Get the field values
					String tel = cur.getString(cur
							.getColumnIndexOrThrow(Phone.NUMBER));
					numeros.add(new Numero(tel, typeNumber));

				} while (cur.moveToNext());
			}
		}

		private void getColumnData2(Cursor cur, String typeNumber) {
			if (cur.moveToFirst()) {
				do {
					// Get the field values
					String tel = cur.getString(cur
							.getColumnIndexOrThrow(Email.DATA));
					numeros.add(new Numero(tel, typeNumber));

				} while (cur.moveToNext());
			}
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			return (numeros.get(position).getTipoDeNumero() == "email") ? 0 : 1;
		}

		@Override
		public int getCount() {
			return numeros.size();
		}

		@Override
		public Object getItem(int arg0) {
			return numeros.get(arg0);
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
			int type = getItemViewType(position);
			if (convertView == null) {
				// inflater = tomar layout e instanciarlo
				Numero num = numeros.get(position);
				if (type == 0) {
					convertView = _inflater.inflate(R.layout.itemnumero2, null);
					final int pos = position;
					ImageView iconoEmail = (ImageView) convertView
							.findViewById(R.id.iconoEmail);
					iconoEmail.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							onContactoEmail(pos);
						}
					});
				} else {
					convertView = _inflater.inflate(R.layout.itemnumero, null);
					final int pos = position;
					ImageView iconoSms = (ImageView) convertView
							.findViewById(R.id.iconoMsj);
					iconoSms.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							onContactoSms(pos);
						}
					});
					ImageView iconoLlamada = (ImageView) convertView
							.findViewById(R.id.iconoLlamada);
					iconoLlamada.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							onContactoCall(pos);
						}
					});
				}

				holder = new Holder();
				holder.txtNumero = (TextView) convertView
						.findViewById(R.id.txtNumero);

				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			String tel = numeros.get(position).getNumero();

			holder.txtNumero.setText(tel);

			return convertView;
		}
	}*/

}
