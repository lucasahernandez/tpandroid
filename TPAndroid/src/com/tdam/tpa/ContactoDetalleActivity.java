package com.tdam.tpa;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ContactoDetalleActivity extends Activity {

	private long id;
	private NumerosAdapter _adapter;
	private Dialog nuevoMensajeDialog,nuevoUsarioContactoDialog;
	private ListView listaTelefonos, listaEmail;
	private ServidorConexion server;
	protected String nombreContacto="";
	private Menu mMenu;
	private static int notificationId = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		id = Long.parseLong(String.valueOf(getIntent().getExtras().getLong(
				ContactoListaActivity.PARAMETRO_ID)));
		_adapter = new NumerosAdapter(id);
		server=UsuarioEntidad.server;
		// TODO usar el layout ejemplo_list_activity
		setContentView(R.layout.detallecontacto);

		TextView nombre = (TextView) findViewById(R.id.txtApellidoYNombreDetalle);
		// Devuelve el intent con el cual esta actividad fue iniciada
		// (getIntent)
		nombre.setText(getIntent().getExtras().getString(
				ContactoListaActivity.PARAMETRO_NOMBRE));
		//nombreContacto=(String)nombre.getText();
		listaTelefonos = (ListView) findViewById(R.id.listT);
		listaTelefonos.setAdapter(_adapter);
		
		nuevoMensajeDialog=new Dialog(ContactoDetalleActivity.this);
		nuevoMensajeDialog.setContentView(R.layout.nuevomensaje);
		nuevoMensajeDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);	    			
		Button btmSendMessage= (Button) nuevoMensajeDialog.findViewById(R.id.btmSendWebMessage);
		btmSendMessage.setOnClickListener(new OnClickListener() {   
			@Override
			public void onClick(View v) {
				EditText txtMessage=(EditText) nuevoMensajeDialog.findViewById(R.id.txtMessageWeb);
				String mensaje=txtMessage.getText().toString();
				txtMessage.setText("");
				onEnviarMensaje(mensaje);

			}
		});
		Button btmCancelMessage=(Button)nuevoMensajeDialog.findViewById(R.id.btmCancelMessage);
		btmCancelMessage.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((EditText) nuevoMensajeDialog.findViewById(R.id.txtMessageWeb)).setText("");
				nuevoMensajeDialog.dismiss();
			}
			
		});
		
		nuevoUsarioContactoDialog=new Dialog(ContactoDetalleActivity.this);
		nuevoUsarioContactoDialog.setContentView(R.layout.nuevousuariocontacto);
		nuevoUsarioContactoDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);	    			
		Button btmAssociate= (Button) nuevoUsarioContactoDialog.findViewById(R.id.btmCreateWebUser);
		btmAssociate.setOnClickListener(new OnClickListener() {   
			@Override
			public void onClick(View v) {
				EditText txtMessage=(EditText) nuevoUsarioContactoDialog.findViewById(R.id.txtUserName);
				String accountName=txtMessage.getText().toString();
				txtMessage.setText("");
				onNuevoUsuarioContacto(accountName);

			}
		});
		Button btmCancelAssociation=(Button)nuevoUsarioContactoDialog.findViewById(R.id.btmCancelAssociation);
		btmCancelAssociation.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				nuevoUsarioContactoDialog.dismiss();
			}
			
		});
		
	}

	private void onContactoSms(int position) {
		NumeroEntidad num = (NumeroEntidad) _adapter.getItem(position);
		String number = num.getNumero();
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms",
				number, null)));

	};

	private void onContactoCall(int position) {
		NumeroEntidad num = (NumeroEntidad) _adapter.getItem(position);
		String number = num.getNumero();
		String uri = "tel:" + number.trim();
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse(uri));
		startActivity(intent);
	};
	
	private void onContactoEmail(int position) {
		NumeroEntidad num = (NumeroEntidad) _adapter.getItem(position);
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
	
	public void onEnviarMensaje(String message)
	{
		if(message.compareTo("")!=0){
			if (ConectividadInfoSingleton
					.getInstance(ContactoDetalleActivity.this)
					.getConnectivityInformation().isInternetConnectionAvailable() && server.isConnectedToServer()){
				nuevoMensajeDialog.dismiss();
				Date timeStamp = new Date();
				timeStamp.setTime(timeStamp.getTime());
				SimpleDateFormat formater=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				formater.setTimeZone(TimeZone.getTimeZone("GMT-03"));
				String newDate=formater.format(timeStamp);
				Date timeWithTimeZone=null;
				SimpleDateFormat formater2=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				try {
					timeWithTimeZone=formater2.parse(newDate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					timeWithTimeZone=new Date();
					e.printStackTrace();
				}			
				MensajeWebEntidad messageSend=new MensajeWebEntidad(nombreContacto,
						message, timeWithTimeZone,false);
				boolean result=server.sendMessage(messageSend);
				NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				Notification messageStatus = new Notification(R.drawable.sms36,
						getString((result ? R.string.sendOk
								: R.string.sendFail)), new Date().getTime());
				messageStatus.flags = Notification.FLAG_AUTO_CANCEL;
				Context context = getApplicationContext();
				CharSequence contentTitle = "Envio de Mensaje Web";
				CharSequence contentText = getString((result ? R.string.sendOk
						: R.string.sendFail));
				contentText=contentText+" Para: "+nombreContacto;
				PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
						null, 0);
				messageStatus.setLatestEventInfo(context, contentTitle,
						contentText, contentIntent);
				notificationManager.notify(++notificationId, messageStatus);
				if(result)
				{
					ArrayList<MensajeWebEntidad> messages=new ArrayList<MensajeWebEntidad>();
					messages.add(messageSend);
					UsuarioEntidad.db.saveMessages(messages);
				}
			}
			else{
				boolean result=false;
				nuevoMensajeDialog.dismiss();
				NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				Notification messageStatus = new Notification(R.drawable.sms36,
						getString((result ? R.string.sendOk
								: R.string.sendFail)), new Date().getTime());
				messageStatus.flags = Notification.FLAG_AUTO_CANCEL;
				Context context = getApplicationContext();
				CharSequence contentTitle = "Envio de Mensaje Web";
				CharSequence contentText = getString((result ? R.string.sendOk
						: R.string.sendFail));
				contentText=contentText+" Para: "+nombreContacto;
				PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
						null, 0);
				messageStatus.setLatestEventInfo(context, contentTitle,
						contentText, contentIntent);
				notificationManager.notify(++notificationId, messageStatus);
			}
		}
		else{
			Toast.makeText(this,"Debe ingresar un mensaje", Toast.LENGTH_SHORT).show();
		}		
	}
	
	@SuppressLint("NewApi")
	public void onNuevoUsuarioContacto(String user)
	{
		nuevoUsarioContactoDialog.dismiss();
    	ContentValues nuevoUsuarioWeb=new ContentValues();
    	nuevoUsuarioWeb.put(ContactsContract.Data.RAW_CONTACT_ID, id);
    	nuevoUsuarioWeb.put(ContactsContract.Data.MIMETYPE,BDInterface.MIMETYPE_WEBMAIL);
    	nuevoUsuarioWeb.put(ContactsContract.Data.DATA1, user);
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, nuevoUsuarioWeb);    		
	}
	
	public void onSendMensajeWeb()
	{
        Cursor contactCursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI, null,
		ContactsContract.Data.RAW_CONTACT_ID + " = ? AND " + 
		ContactsContract.Data.MIMETYPE + " = ?",
		new String[]{String.valueOf(id),
		BDInterface.MIMETYPE_WEBMAIL}, null); 
        boolean esNuevoUsuarioContacto=true;
        if(contactCursor.moveToNext()) 
	    {
        	esNuevoUsuarioContacto=false;
        	nombreContacto=(contactCursor.getString
        			(contactCursor.getColumnIndex(ContactsContract.Data.DATA1)));
        	nuevoMensajeDialog.show();
	    } 
        contactCursor.close();
        if(esNuevoUsuarioContacto)
        {
        	nuevoUsarioContactoDialog.show();
        }
	    
	}

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
		ArrayList<NumeroEntidad> numeros;
		private LayoutInflater _inflater;

		public NumerosAdapter(long idC) {
			numeros = new ArrayList<NumeroEntidad>();
			Cursor cursor = getNumeros(idC);
			getColumnData(cursor, "telefono");
			Cursor cursor2 = getEmails(idC);
			getColumnData2(cursor2, "email");
			_inflater = LayoutInflater.from(ContactoDetalleActivity.this);

		}

		private void getColumnData(Cursor cur, String typeNumber) {
			if (cur.moveToFirst()) {
				do {
					// Get the field values
					String tel = cur.getString(cur
							.getColumnIndexOrThrow(Phone.NUMBER));
					numeros.add(new NumeroEntidad(tel, typeNumber));

				} while (cur.moveToNext());
			}
		}

		private void getColumnData2(Cursor cur, String typeNumber) {
			if (cur.moveToFirst()) {
				do {
					// Get the field values
					String tel = cur.getString(cur
							.getColumnIndexOrThrow(Email.DATA));
					numeros.add(new NumeroEntidad(tel, typeNumber));

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
				NumeroEntidad num = numeros.get(position);
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
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Hold on to this
		mMenu = menu;

		// Inflate the currently selected menu XML resource.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menudetallecontacto, menu);

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
		case R.id.sendMsg:
			//Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT)
			//.show();
			onSendMensajeWeb();
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
	
	private boolean isMyServiceRunning(Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

}
