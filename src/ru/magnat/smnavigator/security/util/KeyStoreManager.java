package ru.magnat.smnavigator.security.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import android.content.Context;

public class KeyStoreManager {
	
	private static KeyStoreManager sInstance;
	private static final String KEYSTORE_TYPE = KeyStore.getDefaultType();
	
	private KeyStore mKeyStore;
	
	public synchronized static KeyStoreManager getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new KeyStoreManager(context);
		}
		
		return sInstance;
	}
	
	public Certificate readCertificate(String path) {
		Certificate certificate = null;
		
		try {
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			InputStream caInput = new BufferedInputStream(mContext.getAssets().open(path));
			
			try {
			    certificate = certificateFactory.generateCertificate(caInput);
			} finally {
			    caInput.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return certificate;
	}
	
	public void addCertificateFromFile(String path) {
		try {
			mKeyStore.setCertificateEntry("ca", readCertificate(path));
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
	}
	
	private Context mContext;
	
	public KeyStoreManager(Context context) {
		mContext = context;
			
		try {
			mKeyStore = KeyStore.getInstance(KEYSTORE_TYPE);
			mKeyStore.load(null, null);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public KeyStore getKeyStore() {
		return mKeyStore;
	}
	
}
