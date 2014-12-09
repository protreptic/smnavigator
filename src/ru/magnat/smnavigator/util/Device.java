package ru.magnat.smnavigator.util;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.telephony.TelephonyManager;

public class Device {
	
	public static String getDeviceId(Context context) {
		TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String result = tMgr.getDeviceId();
		return ((result == null) ? "Недоступно" : result);
	}

	public static String getDeviceTelNumber(Context context) {
		TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String result = tMgr.getLine1Number();
		result = tMgr.getSubscriberId();
		return ((result == null) ? "Недоступно" : result);
	}
	
	public static boolean isGpsProviderAvailable(final Context context) {
		boolean result = false;
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		List<String> list = locationManager.getProviders(true);
		for (String string : list) {
			if (string.equals(LocationManager.GPS_PROVIDER)) {
				result = true;
			}
		}
		list = null;
		locationManager = null;
		return result;
	}

	public static String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return Text.capitalize(model);
		} else {
			return Text.capitalize(manufacturer) + " " + model;
		}
	}
	
	public static int[] getBatteryState(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        
        return new int[] { batteryLevel, plugged, status };
	}

	/**
	 * NEED_A_COMMENT
	 *
	 * @param context
	 * @return
	 * 
	 * @author petr_bu
	 */
	public static String getOsVersion(Context context) {
		return android.os.Build.VERSION.RELEASE; 
	}
}
