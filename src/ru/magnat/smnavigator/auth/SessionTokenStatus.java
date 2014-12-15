package ru.magnat.smnavigator.auth;

public interface SessionTokenStatus {
	
	public static final int SESSION_TOKEN_REQUIRED = -101;
	public static final int SESSION_TOKEN_REJECTED = -103;
	public static final int SESSION_TOKEN_EXPIRED = -104;
	public static final int SESSION_TOKEN_ACCEPTED = 100;
	
}
