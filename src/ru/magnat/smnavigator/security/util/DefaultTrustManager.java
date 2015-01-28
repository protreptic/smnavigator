package ru.magnat.smnavigator.security.util;

import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class DefaultTrustManager implements X509TrustManager {

    public DefaultTrustManager(KeyStore keyStore) { 
    	
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