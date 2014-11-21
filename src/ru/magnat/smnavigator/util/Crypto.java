package ru.magnat.smnavigator.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Crypto {

	public static byte[] getMd5Hash(final String file) {
		
		File temp = new File(file);
	
		if (temp.exists() && !temp.isDirectory()) {
			
			try {
				
				FileInputStream fis = new FileInputStream(temp);
				byte[] buffer = new byte[1024 * 64];
				byte[] digest = new byte[16];
				int byteCount = 0;
				
				MessageDigest messageDigest = MessageDigest.getInstance("md5");
				
				while ((byteCount = fis.read(buffer)) != -1) {
				
					messageDigest.update(buffer, 0, byteCount);
				}
				
				digest = messageDigest.digest();
				
				fis.close();
				buffer = null;
				
				return digest;
			} catch (FileNotFoundException e) {
				
			} catch (IOException e) {
	
			} catch (NoSuchAlgorithmException e) {
	
			}
		}
		
		return new byte[0];
	}

	public static String getMd5HashString(final byte[] digest) {
	    StringBuffer hexString = new StringBuffer();
	    for (int i = 0; i < digest.length; i++) {
	        if ((0xff & digest[i]) < 0x10) {
	            hexString.append("0" + Integer.toHexString((0xFF & digest[i])));
	        } else {
	        	
	            hexString.append(Integer.toHexString(0xFF & digest[i]));
	        }
	    }
	    return hexString.toString();
	}

	public static String getMd5HashString(final String file) {
		
		File temp = new File(file);
	
		if (temp.exists() && !temp.isDirectory()) {
			
			try {
				
				FileInputStream fis = new FileInputStream(temp);
				byte[] buffer = new byte[1024 * 64];
				byte[] digest = new byte[16];
				int byteCount = 0;
				
				MessageDigest messageDigest = MessageDigest.getInstance("md5");
				
				while ((byteCount = fis.read(buffer)) != -1) {
				
					messageDigest.update(buffer, 0, byteCount);
				}
				
				digest = messageDigest.digest();
				
				String string = getMd5HashString(digest);
				
				fis.close();
				buffer = null;
				
				return string;
			} catch (FileNotFoundException e) {
				
			} catch (IOException e) {
	
			} catch (NoSuchAlgorithmException e) {
	
			}
		}
		
		return null;
	}

}
