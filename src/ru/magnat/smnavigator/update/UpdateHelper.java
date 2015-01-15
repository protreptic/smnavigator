package ru.magnat.smnavigator.update;

import ru.magnat.smnavigator.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Notification.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class UpdateHelper {
	
	private static final String DEBUG_TAG = UpdateHelper.class.getSimpleName();
	
	private static UpdateHelper sInstance;
	
	public synchronized static UpdateHelper get(Context context) {
		if (sInstance == null) {
			sInstance = new UpdateHelper(context);
		}
		
		Log.d(DEBUG_TAG, "storage:instantiate->ok");
		
		return sInstance;
	}
	
	public synchronized static void release() {
		if (sInstance != null) {
			sInstance = null;
		}
	}
	
	private Context mContext;
	private NotificationManager mNotificationManager;
	
	public UpdateHelper(Context context) {
		mContext = context;
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	public void update() {
		new AsyncTask<Void, Void, Artifact>() {

			private static final int NOTIFICATION_ID = 101; 
			
			private CentralRepository centralRepository;
			
			protected void onPreExecute() {
				centralRepository = new CentralRepository(mContext); 
				
				Notification.Builder builder = new Builder(mContext);
				builder.setSmallIcon(R.drawable.book_bookmark);
				builder.setContentTitle(mContext.getString(R.string.app_name));
				builder.setContentText(mContext.getString(R.string.update_check)); 
				builder.setProgress(0, 0, true);
				builder.setAutoCancel(false);
				builder.setOngoing(true);
				
				mNotificationManager.notify(NOTIFICATION_ID, builder.build()); 
			};
			
			@Override
			protected Artifact doInBackground(Void... params) {
				return centralRepository.update();
			}
			
			protected void onPostExecute(Artifact artifact) {
				mNotificationManager.cancel(NOTIFICATION_ID);
				
				if (artifact != null) {
					Intent intent = new Intent(mContext, DownloadArtifactActivity.class);
					intent.putExtra("artifact", artifact);
					
					TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
					stackBuilder.addParentStack(DownloadArtifactActivity.class);
					stackBuilder.addNextIntent(intent);
					
					PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
					
					long[] pattern = {3, 1000, 1000};
					
					Notification.Builder builder = new Builder(mContext);
					builder.setSmallIcon(R.drawable.book_bookmark);
					builder.setContentTitle(mContext.getString(R.string.app_name));
					builder.setContentText(String.format(mContext.getString(R.string.update_update_available), artifact.getVersionName())); 
					builder.setVibrate(pattern);					
					builder.setContentIntent(pendingIntent);
					
					mNotificationManager.notify(NOTIFICATION_ID, builder.build()); 
				} else {
					Notification.Builder builder = new Builder(mContext);
					builder.setSmallIcon(R.drawable.book_bookmark);
					builder.setContentTitle(mContext.getString(R.string.app_name));
					builder.setContentText(mContext.getString(R.string.update_update_unavailable)); 
					builder.setAutoCancel(false);
					
					mNotificationManager.notify(NOTIFICATION_ID, builder.build()); 
				}
			};
			
		}.execute(); 
	}
	
}
