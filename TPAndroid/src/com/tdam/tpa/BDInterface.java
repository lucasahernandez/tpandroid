package com.tdam.tpa;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.tdam.tpa.Conectividad;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class BDInterface extends SQLiteOpenHelper {
	public static final String MIMETYPE_WEBMAIL = "vnd.android.cursor.item/webmail";
	private static final String DATABASE_NAME = "WebMessages.db";
	private static final int DATABASE_VERSION = 1;
	private static final String WEB_MESSAGES_TABLE = "messages";
	private static final String CONNECTIVITY_TABLE = "conectividad";
	private static final String USERS_TABLE = "users";
	private Resources _resources;
	private SimpleDateFormat formater;

	public BDInterface(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		formater = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		createDatabaseSchema(db);
	}

	public void createDatabaseSchema(SQLiteDatabase db) {
		// db.execSQL("drop table messages");
		StringBuilder createTableWebMessages = new StringBuilder();
		createTableWebMessages.append("CREATE  TABLE  IF NOT EXISTS ");
		createTableWebMessages.append(WEB_MESSAGES_TABLE);
		createTableWebMessages.append("(\"");
		createTableWebMessages.append(WebMessageCursor.COLUMN_ID);
		createTableWebMessages
				.append("\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"");
		createTableWebMessages.append(WebMessageCursor.COLUMN_USER_NAME);
		createTableWebMessages.append("\" TEXT NOT NULL, \"");
		createTableWebMessages.append(WebMessageCursor.COLUMN_FRIEND_NAME);
		createTableWebMessages.append("\" TEXT NOT NULL , \"");
		createTableWebMessages.append(WebMessageCursor.COLUMN_TIMESTAMP);
		createTableWebMessages.append("\" TEXT , \"");
		createTableWebMessages.append(WebMessageCursor.COLUMN_DIRECTION);
		createTableWebMessages.append("\" INTEGER , \"");
		createTableWebMessages.append(WebMessageCursor.COLUMN_MESSAGE);
		createTableWebMessages.append("\" TEXT )");
		db.execSQL(createTableWebMessages.toString());

		StringBuilder createTableUsers = new StringBuilder();
		createTableUsers.append("CREATE  TABLE  IF NOT EXISTS ");
		createTableUsers.append(USERS_TABLE);
		createTableUsers.append("(\"");
		createTableUsers.append(UserCursor.COLUMN_ID);
		createTableUsers
				.append("\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , \"");
		createTableUsers.append(UserCursor.COLUMN_USER_NAME);
		createTableUsers.append("\" TEXT , \"");
		createTableUsers.append(UserCursor.COLUMN_PASSWORD);
		createTableUsers.append("\" TEXT )");
		db.execSQL(createTableUsers.toString());
		
		StringBuilder createTableConnectivity = new StringBuilder();
		createTableConnectivity.append("CREATE  TABLE  IF NOT EXISTS ");
		createTableConnectivity.append(CONNECTIVITY_TABLE);
		createTableConnectivity.append("(\"");
		createTableConnectivity.append(ConnectivityCursor.COLUMN_ID);
		createTableConnectivity
				.append("\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , \"");
		createTableConnectivity.append(ConnectivityCursor.COLUMN_CONECTIVITY);
		createTableConnectivity.append("\" TEXT , \"");
		createTableConnectivity.append(ConnectivityCursor.COLUMN_STATUS);
		createTableConnectivity.append("\" TEXT , \"");
		createTableConnectivity.append(ConnectivityCursor.COLUMN_TIMESTAMP);
		createTableConnectivity.append("\" TEXT )");
		db.execSQL(createTableConnectivity.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public void registerUser(String user, String password) {
		ContentValues row;
		row = new ContentValues();
		row.put(UserCursor.COLUMN_USER_NAME, user);
		row.put(UserCursor.COLUMN_PASSWORD, password);
		getWritableDatabase().insert(USERS_TABLE, null, row);
	}

	public boolean confirmIdentity(String user, String password) {
		Cursor cursor = getReadableDatabase().query(
				USERS_TABLE,
				null,
				UserCursor.COLUMN_USER_NAME + "=? AND "
						+ UserCursor.COLUMN_PASSWORD + "=?",
				new String[] { user, password }, null, null, null);
		boolean response = false;
		if (!cursor.isClosed()) {
			response = cursor.moveToNext();
			cursor.close();
		}
		return response;
		// return true;
	}

	public ArrayList<MensajeWebEntidad> getMessages() {

		ArrayList<MensajeWebEntidad> messages = new ArrayList<MensajeWebEntidad>();
		Cursor cursor = getReadableDatabase().query(
				WEB_MESSAGES_TABLE,
				null,
				null,
				null, null, null,
				WebMessageCursor.COLUMN_TIMESTAMP + " ASC");
		try {
			while (cursor.moveToNext()) {
				MensajeWebEntidad message = new MensajeWebEntidad(
						cursor.getString(cursor
								.getColumnIndex(WebMessageCursor.COLUMN_FRIEND_NAME)),
						cursor.getString(cursor
								.getColumnIndex(WebMessageCursor.COLUMN_MESSAGE)),
						formater.parse(cursor.getString(cursor
								.getColumnIndex(WebMessageCursor.COLUMN_TIMESTAMP))),
						(cursor.getInt(cursor
								.getColumnIndex(WebMessageCursor.COLUMN_DIRECTION)) == 1) ? true
								: false);
				messages.add(message);
			}
		} catch (ParseException e) {

		}
		if (!cursor.isClosed())
			cursor.close();

		return messages;
	}

	public void deleteMessages(String friendUser) {
		getWritableDatabase().delete(
				WEB_MESSAGES_TABLE,
				WebMessageCursor.COLUMN_USER_NAME + "=? AND "
						+ WebMessageCursor.COLUMN_FRIEND_NAME + "=?",
				new String[] { UsuarioEntidad.name, friendUser });
	}

	public void deleteSingleMessage(MensajeWebEntidad message) {
		getWritableDatabase().delete(
				WEB_MESSAGES_TABLE,
				WebMessageCursor.COLUMN_USER_NAME + "=? AND " +
						 WebMessageCursor.COLUMN_FRIEND_NAME + "=? AND "
						+ WebMessageCursor.COLUMN_TIMESTAMP + "=?",
				new String[] { UsuarioEntidad.name, message.user,
						formater.format(message.timeStamp) });
	}

	public ArrayList<MensajeWebEntidad> getLastMessageForContacts() {

		ArrayList<MensajeWebEntidad> messages = new ArrayList<MensajeWebEntidad>();
		Cursor cursor = getReadableDatabase().query(
				WEB_MESSAGES_TABLE,
				null,
				null,null, null,
		null, WebMessageCursor.COLUMN_TIMESTAMP + " DESC");
		try {
			while (cursor.moveToNext()) {
				MensajeWebEntidad message = new MensajeWebEntidad(
						cursor.getString(cursor
								.getColumnIndex(WebMessageCursor.COLUMN_FRIEND_NAME)),
						cursor.getString(cursor
								.getColumnIndex(WebMessageCursor.COLUMN_MESSAGE)),
						formater.parse(cursor.getString(cursor
								.getColumnIndex(WebMessageCursor.COLUMN_TIMESTAMP))),
						(cursor.getInt(cursor
								.getColumnIndex(WebMessageCursor.COLUMN_DIRECTION)) == 1) ? true
								: false);
				messages.add(message);
			}
		} catch (ParseException e) {

		}
		if (!cursor.isClosed())
			cursor.close();

		return messages;
	}

	public void saveMessages(ArrayList<MensajeWebEntidad> messages) {

		ContentValues row;
		for (MensajeWebEntidad message : messages) {
			row = new ContentValues();
			row.put(WebMessageCursor.COLUMN_USER_NAME, UsuarioEntidad.name);
			row.put(WebMessageCursor.COLUMN_FRIEND_NAME, message.user);
			row.put(WebMessageCursor.COLUMN_TIMESTAMP,
					formater.format(message.timeStamp));
			row.put(WebMessageCursor.COLUMN_DIRECTION, (message.direction) ? 1
					: 0);
			row.put(WebMessageCursor.COLUMN_MESSAGE, message.message);
			getWritableDatabase().insert(WEB_MESSAGES_TABLE, null, row);
		}

	}
	
	// CONECTIVIDAD

		public void saveConnectivity(Conectividad con) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(ConnectivityCursor.COLUMN_CONECTIVITY, con.getConnection());
			cv.put(ConnectivityCursor.COLUMN_STATUS, con.getStatus());
			cv.put(ConnectivityCursor.COLUMN_TIMESTAMP, con.getTimeStamp());
			getWritableDatabase().insert(CONNECTIVITY_TABLE, null, cv);
		}
		
		public void deleteConnectivity(Conectividad conn) {
			getWritableDatabase().delete(
					CONNECTIVITY_TABLE,
					ConnectivityCursor.COLUMN_ID + " = ?",
					new String[] { String.valueOf(conn.get_id()) });
		}

		public ArrayList<Conectividad> getAllConnectivityInfo() {
			ArrayList<Conectividad> conn = new ArrayList<Conectividad>();
			Cursor cursor = getReadableDatabase().query(
					CONNECTIVITY_TABLE,
					null,
					null,null, null,
			null, ConnectivityCursor.COLUMN_TIMESTAMP + " DESC");
			while (cursor.moveToNext()) {
				Conectividad c = new Conectividad(
						cursor.getInt(cursor
								.getColumnIndex(ConnectivityCursor.COLUMN_ID)),
						cursor.getString(cursor
								.getColumnIndex(ConnectivityCursor.COLUMN_CONECTIVITY)),
						cursor.getString(cursor
								.getColumnIndex(ConnectivityCursor.COLUMN_STATUS)),
						cursor.getString(cursor
								.getColumnIndex(ConnectivityCursor.COLUMN_TIMESTAMP)));
				conn.add(c);
			}
			if (!cursor.isClosed())
				cursor.close();

			return conn;
		}

	class WebMessageCursor extends MatrixCursor {
		public static final String COLUMN_ID = android.provider.BaseColumns._ID;
		public static final String COLUMN_USER_NAME = "userName";
		public static final String COLUMN_FRIEND_NAME = "friendName";
		public static final String COLUMN_TIMESTAMP = "timestamp";
		public static final String COLUMN_DIRECTION = "direction";
		public static final String COLUMN_MESSAGE = "message";

		public WebMessageCursor() {
			super(new String[] { COLUMN_ID, COLUMN_USER_NAME,
					COLUMN_FRIEND_NAME, COLUMN_TIMESTAMP, COLUMN_DIRECTION,
					COLUMN_MESSAGE });
		}
	}
	
	class ConnectivityCursor extends MatrixCursor {
		public static final String COLUMN_ID = android.provider.BaseColumns._ID;
		public static final String COLUMN_CONECTIVITY = "conectivity";
		public static final String COLUMN_STATUS = "status";
		public static final String COLUMN_TIMESTAMP = "timestamp";

		public ConnectivityCursor() {
			super(new String[] { COLUMN_ID, COLUMN_CONECTIVITY,COLUMN_TIMESTAMP});
		}
	}

	class UserCursor extends MatrixCursor {
		public static final String COLUMN_ID = android.provider.BaseColumns._ID;
		public static final String COLUMN_USER_NAME = "userName";
		public static final String COLUMN_PASSWORD = "password";

		public UserCursor() {
			super(new String[] { COLUMN_ID, COLUMN_USER_NAME, COLUMN_PASSWORD });
		}
	}

}