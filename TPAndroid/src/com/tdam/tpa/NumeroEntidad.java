package com.tdam.tpa;

public class NumeroEntidad {

	public NumeroEntidad(String numero, String tipoDeNumero) {
		super();
		this.numero = numero;
		this.tipoDeNumero = tipoDeNumero;
	}

	private String numero;
	private String tipoDeNumero;

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getTipoDeNumero() {
		return tipoDeNumero;
	}

	public void setTipoDeNumero(String tipoDeNumero) {
		this.tipoDeNumero = tipoDeNumero;
	}

}
