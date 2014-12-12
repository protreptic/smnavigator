package ru.magnat.smnavigator;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import ru.magnat.smnavigator.util.Device;
import ru.magnat.smnavigator.util.Network;

@ReportsCrashes(
		formKey = "", // This is required for backward compatibility but not used
		formUri = "http://mob1.magnat.ru:8081/ws_acra_submit_crash_report",
		mode = ReportingInteractionMode.TOAST,
	    resToastText = R.string.crash_message,
		socketTimeout = 5000
	)
public class Application extends android.app.Application {
	
    // Sync interval constants
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 45L;
    public static final long SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		initAcra();
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		
		// turn off periodic sync
		//ContentResolver.removePeriodicSync(sAccount, Application.AUTHORITY, new Bundle());
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
