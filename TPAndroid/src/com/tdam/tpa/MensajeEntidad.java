package com.tdam.tpa;

public class MensajeEntidad {
	private long id;
	private String address;
	private String body;
	private String person;
	private String type;
	private long date;

	public long getid() {
		return id;
	}

	public void setid(long id) {
		this.id = id;
	}

	public String gettype() {
		return type;
	}

	public void settype(String type) {
		this.type = type;
	}

	public String getaddress() {
		return address;
	}

	public void setaddress(String address) {
		this.address = address;
	}

	public String getbody() {
		return body;
	}

	public void setbody(String body) {
		this.body = body;
	}

	public String getperson() {
		return person;
	}

	public void setperson(String person) {
		this.person = person;
	}

	public long getdate() {
		return date;
	}

	public void setdate(long date) {
		this.date = date;
	}

	public MensajeEntidad(long date, String address, String body, String person,
			String type, long id) {
		super();
		this.address = address;
		this.body = body;
		this.person = person;
		this.date = date;
		this.type = type;
		this.id = id;
	}

	public MensajeEntidad() {
		super();
	}

}
