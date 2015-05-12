package com.tdam.tpa;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class UsuarioPreferencias extends PreferenceActivity {
	String contactsOrder;
	String msgOrder;
	String msgFilter;
	String callsOrder;
	String callsFilter;
	String msgWebOrder;
	String msgWebFilter;

	// String ringtonePreference;
	// String secondEditTextPreference;
	// String customPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.usuariopreferencias);

	}

	public String getContactsOrder() {
		return contactsOrder;
	}

	public String getMsgFilter() {
		return msgFilter;
	}
	
	public String getMsgOrder() {
		return msgOrder;
	}

	public String getCallFilter() {
		return callsFilter;
	}
	
	public String getCallOrder() {
		return callsOrder;
	}

	public String getMsgWebFilter() {
		return msgWebFilter;
	}
	
	public String getMsgWebOrder() {
		return msgWebOrder;
	}

}
