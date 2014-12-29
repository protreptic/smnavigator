package ru.magnat.smnavigator.security;

import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class MyTrustManager implements X509TrustManager {

    public MyTrustManager(KeyStore localKeyStore) { 
    	
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    	
    }

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}
    
}