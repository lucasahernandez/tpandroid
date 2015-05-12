package com.tdam.tpa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class InicioActivity extends Activity 
{
	private BDInterface db;
	private ServidorConexion server;
	private EditText txtUser;
	private EditText txtPassword;
	private Dialog dialog;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inicio);
		UsuarioEntidad.db=new BDInterface(this);
		db=UsuarioEntidad.db;
		UsuarioEntidad.server=new ServidorConexion();
		server=UsuarioEntidad.server;
		txtUser=(EditText)findViewById(R.id.txtUser);
		txtPassword=(EditText)findViewById(R.id.txtPassword);
		Button btmLogin=(Button)findViewById(R.id.btmLogin);
		btmLogin.setOnClickListener(new OnClickListener() {   
			@Override
			public void onClick(View v) {
				onLogin();
			}
		});
		Button btmRegister=(Button)findViewById(R.id.btmRegister);
		btmRegister.setOnClickListener(new OnClickListener() {   
			@Override
			public void onClick(View v) {
				onRegister();
			}
		});	

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.nuevousuario);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		Button btmRegisterNewUser = (Button) dialog.findViewById(R.id.btmRegisterNewUser);
		btmRegisterNewUser.setOnClickListener(new OnClickListener() {   
			@Override
			public void onClick(View v) {
				EditText txtConfirmPassword=(EditText) dialog.findViewById(R.id.txtConfirmPassword);
				String secondPassword=txtConfirmPassword.getText().toString();
				txtConfirmPassword.setText("");
				onRegister(secondPassword);
			}
		});
		Button btmCancelRegistering=(Button)dialog.findViewById(R.id.btmCancelRegistering);
		btmCancelRegistering.setOnClickListener(new OnClickListener() {   
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});		
	}

	private void  onRegister(String secondPassword)
	{
		dialog.dismiss();
		String user=new String(txtUser.getText().toString());
		String password=new String(txtPassword.getText().toString());		
		if(password.compareTo(secondPassword)==0)
		{				
			db.registerUser(user, password);
			initApp(user,password);				
		}
		else this.showDialog(3);
		
	}
	
	private void onLogin()
	{
		String user=new String(txtUser.getText().toString());
		String password=new String(txtPassword.getText().toString());
		//initApp(user,password);
		if(db.confirmIdentity(user,password)){
			initApp(user,password);
		}
		else
			this.showDialog(0);
	}
	
	private void onRegister()
	{
		String user=new String(txtUser.getText().toString());
		String password=new String(txtPassword.getText().toString());
		if(user.length()<5 || password.length()<4)	this.showDialog(1);
		else{ 
			if(server.registerUser(user, password))
			{
				dialog.show();
			}
			else this.showDialog(2);
		};
	}
	
	private void initApp(String user,String password)
	{
		UsuarioEntidad.name=user;
		UsuarioEntidad.password=password;
		Intent intent = new Intent(this, EstructuraTabActivity.class);
		startActivity(intent);		
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		// TODO Auto-generated method stub
		super.onPrepareDialog(id, dialog);
	}	
	
	@Override
	protected Dialog onCreateDialog(int id) 
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id)
		
		{
		case 0:
			builder.setMessage(R.string.mensajeDatosIncorrectos)
			       .setCancelable(false)
			       .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                InicioActivity.this.txtPassword.setText("");
			           }
			       });
			break;
		case 1:
			builder.setMessage(R.string.mensajeDatosIncompletos)
		       .setCancelable(false)
		       .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                InicioActivity.this.txtPassword.setText("");
		           }
		       });
			break;
		case 2:
			builder.setMessage(R.string.mensajeDatosDuplicados)
		       .setCancelable(false)
		       .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               InicioActivity.this.txtUser.setText("");
		               InicioActivity.this.txtPassword.setText("");
		           }
		       });
			break;	
		case 3:
			builder.setMessage(R.string.mensajeDatosNoConcuerdan)
		       .setCancelable(false)
		       .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   InicioActivity.this.txtPassword.setText("");
		           }
		       });
			break;
		}
		AlertDialog alert = builder.create();
		return alert;
	}
}
