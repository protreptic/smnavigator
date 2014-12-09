package ru.magnat.smnavigator.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class Files {

	public static boolean isMemorySizeAvailableAndroid(long download_bytes, boolean isExternalMemory) {
	    boolean isMemoryAvailable = false;
	    long freeSpace = 0;

	    // if isExternalMemory get true to calculate external SD card available size
	    if(isExternalMemory){
	        try {
	            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
	            freeSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
	            if(freeSpace > download_bytes){
	                isMemoryAvailable = true;
	            }else{
	                isMemoryAvailable = false;
	            }
	        } catch (Exception e) {e.printStackTrace(); isMemoryAvailable = false;}
	    }else{
	        // find phone available size
	        try {
	            StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
	            freeSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
	            if(freeSpace > download_bytes){
	                isMemoryAvailable = true;
	            }else{
	                isMemoryAvailable = false;
	            }
	        } catch (Exception e) {e.printStackTrace(); isMemoryAvailable = false;}
	    }

	    return isMemoryAvailable;
	}
	
	public static void copyFile(final String source, final String destination) {
		try {
			File file = new File(source);
			String fileName = file.getName();
			file = null;
			
			File dest = new File(destination);
			dest.mkdirs();
			dest = null;
			
			String path = 
					destination
					+ File.separator
					+ fileName;
			
			FileInputStream fis = new FileInputStream(new File(source));
			FileOutputStream fos = new FileOutputStream(new File(path));
			
			int byteCount = 0;
			byte[] buffer = new byte[1024 * 4];
	
			while ((byteCount = fis.read(buffer)) != -1) {
				
				fos.write(buffer, 0, byteCount);
			}
			
			fis.close();
			fos.close();
			buffer = null;
		} catch (IOException e) {
			Log.e("", "IOException: ", e);
		}
		
	}

	public static void copyFile2(final String source, final String destination) {
		
		try {
	
			File file = new File(source);
			String fileName = file.getName();
			file = null;
			
			File dest = new File(destination);
			dest.mkdirs();
			dest = null;
			
			String path = 
					destination
					+ File.separator
					+ fileName;
			
			FileInputStream fis = new FileInputStream(new File(source));
			FileOutputStream fos = new FileOutputStream(new File(path));
			
			int byteCount = 0;
			byte[] buffer = new byte[1024 * 4];
	
			while ((byteCount = fis.read(buffer)) != -1) {
				
				fos.write(buffer, 0, byteCount);
			}
			
			fis.close();
			fos.close();
			buffer = null;
		} catch (IOException e) {
			
			Log.e("", "IOException: ", e);
		}
		
	}

	/**
	 * Рекурсивно удаляет папку на диске при помощи команды "rm -r <путь к папке>"
	 * @param folder - полный путь папки для удаления
	 */
	public static void deleteFolder(final String folder) {
		
	    File file = new File(folder);
	
	    if (file.exists()) {
	    	
	        try {
	        	
	        	Runtime.getRuntime().exec("rm -r " + folder);
	        } catch (IOException e) {
	        	
	        	e.printStackTrace();
	        }
	    }
	}

	public static void createZip() {}

	public static void makeDirectories(final String... folders) {
		for (String string : folders) {
			File file = new File(string);
			if (!file.exists()) file.mkdirs();
			file = null;
		}
	}

	public static void makeDirectories(final List<String> folders) {
		for (String string : folders) {
			if (string != null) {
				File file = new File(string);
				if (!file.exists()) file.mkdirs();
				file = null;
			}
		}
	}

	public static boolean copyFile(final File source, final File destination) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		byte[] buffer = new byte[1024 * 16];
		int lenght = 0;
		try {
			bis = new BufferedInputStream(new FileInputStream(source));
			bos = new BufferedOutputStream(new FileOutputStream(destination));
			while ((lenght = bis.read(buffer)) != -1) {
				bos.write(buffer, 0, lenght);
			}
			bis.close();
			bos.close();
			buffer = null;
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean copyFile3(final String source, final String destination) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		byte[] buffer = new byte[1024 * 16];
		int lenght = 0;
		try {
			bis = new BufferedInputStream(new FileInputStream(new File(source)));
			bos = new BufferedOutputStream(new FileOutputStream(new File(destination)));
			while ((lenght = bis.read(buffer)) != -1) {
				bos.write(buffer, 0, lenght);
			}
			bis.close();
			bos.close();
			buffer = null;
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
