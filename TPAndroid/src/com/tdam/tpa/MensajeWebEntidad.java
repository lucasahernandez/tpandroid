package com.tdam.tpa;

import java.util.Date;

public class MensajeWebEntidad {
	protected String message;
	protected String user;
	protected Date timeStamp;
	protected boolean direction;

	public MensajeWebEntidad() {
	}

	public MensajeWebEntidad(String user, String message, Date timeStamp,
			boolean direction) {
		this.message = message;
		this.user = user;
		this.timeStamp = (Date) timeStamp.clone();
		this.direction = direction;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	public boolean getDirection() {
		return direction;
	}

	public void setDirection(boolean direction) {
		this.direction = direction;
	}
	
	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

}
