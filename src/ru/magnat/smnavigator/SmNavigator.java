package ru.magnat.smnavigator;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import ru.magnat.smnavigator.util.Device;
import ru.magnat.smnavigator.util.Network;

@ReportsCrashes(
		formKey = "", 
		formUri = "http://mob1.magnat.ru:8081/ws_acra_submit_crash_report",
		mode = ReportingInteractionMode.TOAST,
	    resToastText = R.string.crash_message,
		socketTimeout = 5000
	)
public class SmNavigator extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		initAcra();
	}
	
	private void initAcra() {
        ACRA.init(this);
        ACRA.getErrorReporter().putCustomData("device_id", Device.getDeviceId(this));
        ACRA.getErrorReporter().putCustomData("mac_wlan0", Network.getMACAddress("wlan0"));
        ACRA.getErrorReporter().putCustomData("mac_eth0", Network.getMACAddress("eth0"));
        ACRA.getErrorReporter().putCustomData("ip_v4", Network.getIPAddress(true));
        ACRA.getErrorReporter().putCustomData("ip_v6", Network.getIPAddress(false));
	}
	
}
