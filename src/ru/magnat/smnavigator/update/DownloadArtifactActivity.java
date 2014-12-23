package ru.magnat.smnavigator.update;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.util.Apps;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Notification.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

public class DownloadArtifactActivity extends Activity {
	
	private Artifact artifact;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		
		artifact = bundle.getParcelable("artifact");
		
		if (artifact != null) { 
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(artifact.toString());
			builder.setPositiveButton(getString(R.string.download), new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					new AsyncTask<Void, Integer, String>() {

						private static final int NOTIFICATION_ID = 101; 
						
						private NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
						
						private int contentLength;
						
						protected void onPreExecute() {
							Notification.Builder builder = new Builder(DownloadArtifactActivity.this);
							builder.setSmallIcon(R.drawable.logotype_small_icon);
							builder.setContentTitle(getString(R.string.app_name)); 
							builder.setContentText(getString(R.string.update_loading));
							builder.setAutoCancel(false);
							builder.setOngoing(true);
							
							notificationManager.notify(NOTIFICATION_ID, builder.build()); 
							
							Toast.makeText(getBaseContext(), getString(R.string.update_loading), Toast.LENGTH_LONG).show();
						};
						
						@Override
						protected String doInBackground(Void... params) {
							InputStream input = null;
					        OutputStream output = null;
					        HttpURLConnection connection = null;
					        
					        String fileName = null;
					        
					        try {
								URL url = new URL(getString(R.string.syncServer) + "/sm_downloadUpdate?artifactId=" + artifact.getArtifactId());
					            connection = (HttpURLConnection) url.openConnection();
					            connection.connect();

					            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					            	
					            }

					            contentLength = connection.getContentLength();
					            
					            fileName = connection.getHeaderField("Filename");
					            
					            // download the file
					            input = connection.getInputStream();
					            output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + fileName);

					            byte buffer[] = new byte[1024 * 256];
					            long total = 0;
					            int count;
					            while ((count = input.read(buffer)) != -1) {
					                // allow canceling with back button
					                if (isCancelled()) {
					                    input.close();
					                    return null;
					                }
					                total += count;
					                // publishing the progress....
					                if (contentLength > 0) // only if total length is known
					                    publishProgress(new Integer[] { (int) total });
					                output.write(buffer, 0, count);
					            } 
					        } catch (Exception e) {
					        	e.printStackTrace();
					        	cancel(true);
					        } finally {
					            try {
					                if (output != null)
					                    output.close();
					                if (input != null)
					                    input.close();
					            } catch (IOException ignored) {
					            }

					            if (connection != null)
					                connection.disconnect();
					        }
							
							return fileName;
						}
						
						protected void onProgressUpdate(Integer... progress) {
							Notification.Builder builder = new Builder(DownloadArtifactActivity.this);
							builder.setSmallIcon(R.drawable.logotype_small_icon);
							builder.setContentTitle(getString(R.string.app_name));
							builder.setContentText(getString(R.string.update_loading));
							builder.setAutoCancel(false);
							builder.setOngoing(true);
							builder.setProgress(contentLength, progress[0], false);
							
							notificationManager.notify(NOTIFICATION_ID, builder.build()); 
						};
						
						protected void onPostExecute(final String artifact) {
							notificationManager.cancel(NOTIFICATION_ID);
							 
							if (artifact != null) {
								Notification.Builder builder = new Builder(DownloadArtifactActivity.this);
								builder.setSmallIcon(R.drawable.logotype_small_icon);
								builder.setContentTitle(getString(R.string.app_name));
								builder.setContentText(getString(R.string.update_loading_complete));
								builder.setAutoCancel(false);
								
								notificationManager.notify(NOTIFICATION_ID, builder.build()); 
								
								AlertDialog.Builder builder2 = new AlertDialog.Builder(DownloadArtifactActivity.this);
								builder2.setMessage(getString(R.string.update_loading_complete) + ". " + getString(R.string.update_install));
								builder2.setPositiveButton(getString(R.string.install), new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										Apps.installApk(getBaseContext(), artifact); 										
									}
									
								});
								builder2.setNegativeButton(getString(R.string.cancel), new OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										finish();
									}
								});
								
								builder2.show();
							}
							
							Toast.makeText(getBaseContext(), getString(R.string.update_loading_complete), Toast.LENGTH_LONG).show();
						};
						
					}.execute();
				}
			});
			builder.setNegativeButton(getString(R.string.cancel), new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			
			builder.show();
		} else {
			Toast.makeText(getBaseContext(), "artifact = null", Toast.LENGTH_LONG).show();
		}
	}
	
}
