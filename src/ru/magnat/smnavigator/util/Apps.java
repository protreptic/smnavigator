package ru.magnat.smnavigator.util;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;

public class Apps {

	public static void installApk(final Context context, final String file) {
		
		Intent intentInstallApk = new Intent(Intent.ACTION_VIEW);
	    intentInstallApk.setDataAndType(Uri.fromFile(new File(file)), "application/vnd.android.package-archive");
	    intentInstallApk.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    intentInstallApk.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
	    context.startActivity(intentInstallApk);
	}

	public static String getVersionName(Context context) {
		String versionName = null;
		try {
			versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (final NameNotFoundException e) {
			Log.e(context.getClass().getSimpleName(), "Could not get version from manifest.");
		}
		if (versionName == null) {
			versionName = "unknown";
		}
		return versionName;
	}
	
}
