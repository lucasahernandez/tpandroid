package com.tdam.tpa;

public class UsuarioEntidad {
	protected static String name;
	protected static String password;
	protected static BDInterface db;
	protected static ServidorConexion server;

	private UsuarioEntidad() {
	}

	public static void setUser(String name, String password) {
		UsuarioEntidad.name = name;
		UsuarioEntidad.password = password;
	}
}
