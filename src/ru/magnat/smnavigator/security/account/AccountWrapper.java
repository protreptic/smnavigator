package ru.magnat.smnavigator.security.account;

public class AccountWrapper {
	
    public static final String ACCOUNT_AUTHORITY = "ru.magnat.smnavigator.auth";
    public static final String ACCOUNT_TYPE = "ru.magnat.smnavigator";
	
    public static String sessionToken;
    
	private String token;
	private String expiration;
	
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
	
	@Override
	public String toString() {
		return "AccountWrapper [token=" + token + ", expiration=" + expiration + "]";
	}
	
}