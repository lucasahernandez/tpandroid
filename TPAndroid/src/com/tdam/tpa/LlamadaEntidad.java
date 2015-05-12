package com.tdam.tpa;

public class LlamadaEntidad {
	private String numero;
	private long fecha;
	private long duracion;
	private int tipo;
	private long codigo;

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public long getFecha() {
		return fecha;
	}

	public void setFecha(long fecha) {
		this.fecha = fecha;
	}

	public long getDuracion() {
		return duracion;
	}

	public void setDuracion(long duracion) {
		this.duracion = duracion;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public long getCodigo() {
		return codigo;
	}

	public void setCodigo(long codigo) {
		this.codigo = codigo;
	}

	public LlamadaEntidad(String numero, long fecha, long duracion, int tipo,
			long codigo) {
		super();
		this.numero = numero;
		this.fecha = fecha;
		this.duracion = duracion;
		this.tipo = tipo;
		this.codigo = codigo;
	}

	public LlamadaEntidad() {
		super();
	}

}
