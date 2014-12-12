package ru.magnat.smnavigator.account;

public class AccountWrapper {
	
	// Constants
    // The authority for the sync adapter's content provider
    public static final String ACCOUNT_AUTHORITY = "ru.magnat.smnavigator.auth";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "ru.magnat.smnavigator";
	
	private Integer id;
	private String name;
	private String fullName;
	private String email;
	private String token;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getFullName() {
		return fullName;
	}
	
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}