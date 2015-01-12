package ru.magnat.smnavigator.security;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import android.content.Context;
import android.util.Log;

public class KeyStoreManager {
	private static final String TAG = "key_store";
	
	private static KeyStoreManager sInstance;
	private static final String KEYSTORE_TYPE = KeyStore.getDefaultType();
	
	private KeyStore mKeyStore;
	
	public synchronized static KeyStoreManager getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new KeyStoreManager(context);
		}
		
		Log.d(TAG, "storage:instantiate->ok");
		
		return sInstance;
	}
	
	public KeyStoreManager(Context context) {
		try {
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			InputStream caInput = new BufferedInputStream(context.getAssets().open("server-certificate.pem"));
			Certificate certificate;
			try {
			    certificate = certificateFactory.generateCertificate(caInput);
			} finally {
			    caInput.close();
			}
			
			mKeyStore = KeyStore.getInstance(KEYSTORE_TYPE);
			mKeyStore.load(null, null);
			mKeyStore.setCertificateEntry("ca", certificate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public KeyStore getKeyStore() {
		return mKeyStore;
	}
	
}
