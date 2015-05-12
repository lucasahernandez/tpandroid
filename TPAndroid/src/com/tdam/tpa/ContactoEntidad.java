package com.tdam.tpa;

import java.util.ArrayList;

public class ContactoEntidad {
	private String apellidoYNombre;
	private ArrayList<String> telefono;
	private String email;
	private long codigo;

	public ArrayList<String> getTelefono() {
		return telefono;
	}

	public void setTelefono(ArrayList<String> telefono) {
		this.telefono = telefono;
	}

	public String getApellidoYNombre() {
		return apellidoYNombre;
	}

	public void setApellidoYNombre(String apellidoYNombre) {
		this.apellidoYNombre = apellidoYNombre;
	}

	public ContactoEntidad() {
		super();

	}

	public ContactoEntidad(String apellidoYNombre, long codigo) {
		super();
		this.apellidoYNombre = apellidoYNombre;
		this.codigo = codigo;

	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getCodigo() {
		return codigo;
	}

	public void setCodigo(long codigo) {
		this.codigo = codigo;
	}

}
