package ru.magnat.smnavigator.security.account;

import android.accounts.Account;

public class MagnatAccount extends Account {

	private String token;
	private String expiration;
	
	public MagnatAccount(String name, String type) {
		super(name, type);
 	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getExpiration() {
		return expiration;
	}

	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}

}
