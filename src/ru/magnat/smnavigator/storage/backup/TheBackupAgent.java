package ru.magnat.smnavigator.storage.backup;

import java.io.IOException;

import org.javaprotrepticon.android.androidutils.Apps;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class TheBackupAgent extends BackupAgentHelper {

	private Context mContext;
	
	@Override
	public void onCreate() {
		Log.d("TheBackupAgent", "BackupAgent->onCreate");
		
		mContext = getBaseContext();
		
		String path = mContext.getDir("data", Context.MODE_PRIVATE).getPath() + "/" + "petr_bu" + "/";
		String name = mContext.getPackageName() + "-" + Apps.getVersionName(mContext) + ".h2.db"; 

		FileBackupHelper backupHelper = new FileBackupHelper(getBaseContext(), path + name);
		
		addHelper("accountData", backupHelper);
	}
	
	@Override
	public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
		Log.d("TheBackupAgent", "BackupAgent->onBackup");
	}

	@Override
	public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
		Log.d("TheBackupAgent", "BackupAgent->onRestore");
	}

}
