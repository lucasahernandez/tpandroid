package com.tdam.tpa;

public class Conectividad {

	private int _id;
	private String connection;
	private String status;
	private String timeStamp;
	
	public static final String STATUS_CONNECTED = "CONNECTED";
	public static final String STATUS_DISCONNECTED = "DISCONNECTED";

	public Conectividad() {

	}
	
	public Conectividad(int _id, String connection, String status, String timeStamp) {
		this._id=_id;
		this.connection=connection;
		this.status=status;
		this.timeStamp=timeStamp;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

}
