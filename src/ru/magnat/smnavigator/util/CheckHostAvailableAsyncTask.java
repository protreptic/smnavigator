package ru.magnat.smnavigator.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.os.AsyncTask;

public class CheckHostAvailableAsyncTask extends AsyncTask<String, Void, Boolean> {

	@Override
	protected Boolean doInBackground(String... args) {
		boolean result = false;
		
		String host = args[0];
		int port = Integer.valueOf(args[1]);
		
		try {
			SocketAddress address = new InetSocketAddress(InetAddress.getByName(host), port);
			
			Socket socket = new Socket();
	    	socket.connect(address, 3500);
	    	
	    	result = socket.isConnected();
	    	
	    	socket.close();
	    	socket = null;
    	} catch(Exception e) {
    		return false;
    	} 
		
		return result;
	}
	
}