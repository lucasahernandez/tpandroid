package com.tdam.tpa;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class MensajeWebServicio extends Service {
	private final IBinder conection = new LocalBinder();
	private final long INTERVAL = 60000;
	private ArrayList<Notificable> clients;
	private ArrayList<MensajeWebEntidad> messages;
	private Timer timer;
	private final int NOTIFICATION_ID = 0;
	private SimpleDateFormat formater;
	private SimpleDateFormat formater2;
	private static boolean isRunning = false;

	@Override
	public void onCreate() {
		super.onCreate();
		clients = new ArrayList<Notificable>();
		timer = new Timer();
		messages = new ArrayList<MensajeWebEntidad>();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				ArrayList<MensajeWebEntidad> newMessages= new ArrayList<MensajeWebEntidad>();
				int messagesCount =0;
				if(UsuarioEntidad.server.isConnectedToServer()){
					Date timeStamp = new Date();
					timeStamp.setTime(timeStamp.getTime() - INTERVAL);
					formater=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
					formater.setTimeZone(TimeZone.getTimeZone("GMT-03"));
					String newDate=formater.format(timeStamp);
					Date timeWithTimeZone=null;
					formater2=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
					try {
						timeWithTimeZone=formater2.parse(newDate);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						timeWithTimeZone=new Date();
						e.printStackTrace();
					}			
					newMessages = UsuarioEntidad.server.getMessages(timeWithTimeZone);
					messagesCount = newMessages.size();
				}	
				if (messagesCount > 0) {
					messages.addAll(newMessages);
					NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					Notification messageStatus = new Notification(
							R.drawable.sms36, "("
									+ messagesCount
									+ ")"
									+ MensajeWebServicio.this
											.getString(R.string.newMessages),
							System.currentTimeMillis());
					messageStatus.flags = Notification.FLAG_AUTO_CANCEL;
					Context context = getApplicationContext();
					CharSequence contentTitle = MensajeWebServicio.this
							.getString(R.string.app_name);
					CharSequence contentText = MensajeWebServicio.this
							.getString(R.string.newMessagesTitle);
					Intent notificationIntent = new Intent(
							MensajeWebServicio.this, EstructuraTabActivity.class);
					notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					PendingIntent contentIntent = PendingIntent.getActivity(
							MensajeWebServicio.this, 0, notificationIntent, 0);
					messageStatus.setLatestEventInfo(context, contentTitle,
							contentText, contentIntent);
					notificationManager.notify(NOTIFICATION_ID, messageStatus);
				}
			}

		}, 0L, INTERVAL);
		Log.i("MensajeWebServicio", "Service Running.");
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return conection;
	}

	public void bind(Notificable client) {
		clients.add(client);
	}

	public void unBind(Notificable client) {
		clients.remove(client);
	}

	public class LocalBinder extends Binder {
		MensajeWebServicio getService() {
			return MensajeWebServicio.this;
		}
	}
	
	public static boolean isRunning()
    {
        return isRunning;
    }

	public interface Notificable {

		boolean notificar(int acccion, Bundle datos);
	}

	public ArrayList<MensajeWebEntidad> getMessages() {
		ArrayList<MensajeWebEntidad> newMessages = messages;
		messages = new ArrayList<MensajeWebEntidad>();
		return newMessages;
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("MensajeWebServicio", "Service Stop.");
    }

}
