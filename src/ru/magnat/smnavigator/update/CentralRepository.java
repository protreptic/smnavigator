package ru.magnat.smnavigator.update;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.javaprotrepticon.android.androidutils.Apps;

import ru.magnat.smnavigator.R;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public class CentralRepository {
	
	private Context mContext;
	
	private RepositoryArtifactReader mArtifactReader;
	private List<Artifact> mArtifacts = new ArrayList<Artifact>();
	
	public CentralRepository(Context context) {
		mContext = context;
		
		mArtifactReader = new RepositoryArtifactReader();
	}
	
	public Artifact update() {
		mArtifacts = mArtifactReader.fetchRepository();
		
		int versionCode = Apps.getVersionCode(mContext);
		
		for (Artifact artifact : mArtifacts) {
			int artifactVersionCode = Integer.valueOf(artifact.getVersionCode());
			
			if (versionCode < artifactVersionCode) {
				return artifact;
			}
		}
		
		return null;
	}
	
	public void downloadArtifact(Artifact artifact) {
		
	}
	
	public Artifact find(String artifactId) {
		for (Artifact artifact : mArtifacts) {
			if (artifact.getArtifactId().equals(artifactId))
				return artifact;
		}
		
		return null;
	}
	
	public class RepositoryArtifactReader {
		
		public List<Artifact> fetchRepository() {
			List<Artifact> artifacts = new ArrayList<Artifact>();
			
			try {
				URL url = new URL(mContext.getString(R.string.syncServer) + "/sm_checkUpdates?packageName=" + mContext.getPackageName());
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 
				
				artifacts = new Gson().fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Artifact>>() {}.getType());
				
				urlConnection.disconnect();
				urlConnection = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			for (Artifact artifact : artifacts) {
				Log.d("", artifact.toString());
			}
			
			return artifacts;
		}
		
	}
	
}
