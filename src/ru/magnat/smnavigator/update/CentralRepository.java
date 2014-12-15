package ru.magnat.smnavigator.update;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ru.magnat.smnavigator.R;
import android.content.Context;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;

public class CentralRepository {
	
	private Context mContext;
	
	private String mSessionToken;
	private String mPackageName;
	private RepositoryArtifactReader mArtifactReader;
	private List<Artifact> mArtifacts = new ArrayList<Artifact>();
	
	public CentralRepository(Context context, String sessionToken, String packageName) {
		mContext = context;
		
		mPackageName = packageName;
		mSessionToken = sessionToken;
		
		mArtifactReader = new RepositoryArtifactReader();
	}
	
	public void update() {
		mArtifacts = mArtifactReader.fetchRepository(mSessionToken, mPackageName);
	}
	
	public void clear() {
		mArtifacts.clear();
	}
	
	public Artifact find(String artifactId) {
		for (Artifact artifact : mArtifacts) {
			if (artifact.getArtifactId().equals(artifactId))
				return artifact;
		}
		
		return null;
	}
	
	public class RepositoryArtifactReader {
		
		private static final String TAG = "CENTRAL_REPOSITORY_READER";
		
		public List<Artifact> fetchRepository(String sessionToken, String packageName) {
			if (TextUtils.isEmpty(sessionToken) || TextUtils.isEmpty(packageName))
				throw new IllegalArgumentException();
			
			List<Artifact> artifacts = new ArrayList<Artifact>();
			
			try {
				URL url = new URL("http://" + mContext.getResources().getString(R.string.syncServer) + "/sm_checkUpdates?token=" + sessionToken + "&package=" + packageName);
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 

				artifacts = readJsonStream(urlConnection.getInputStream());

				urlConnection.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				
			}
			
			return artifacts;
		}
		
		public List<Artifact> readJsonStream(InputStream in) throws IOException {
		     JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		     
		     try {
		    	 return readArtifactArray(reader);
		     } finally {
		    	 reader.close();
		     }
		}

		public List<Artifact> readArtifactArray(JsonReader reader) throws IOException {
			List<Artifact> artifacts = new ArrayList<Artifact>();

		    reader.beginArray();
		    while (reader.hasNext()) {
		    	artifacts.add(readArtifact(reader));
		    }
		    reader.endArray();
		    
		    return artifacts;
		}

		public Artifact readArtifact(JsonReader reader) throws IOException {
			Artifact artifact = new Artifact();

		    reader.beginObject();
		    while (reader.hasNext()) {
		    	String name = reader.nextName();
		    	
		    	if (name.equals("artifactId")) {
		    		artifact.setArtifactId(reader.nextString());
		    	} else if (name.equals("artifactName")) {
		    		artifact.setArtifactName(reader.nextString());
		    	} else if (name.equals("package")) {
		    		artifact.setPackageName(reader.nextString());
		    	} else if (name.equals("description")) {
		    		artifact.setDescription(reader.nextString());
		    	} else if (name.equals("versionCode")) {
		    		artifact.setVersionCode(reader.nextString());
		    	} else if (name.equals("versionName")) {
		    		artifact.setVersionName(reader.nextString());
		    	} else {
		    		reader.skipValue();
		    	}
		    }
		    reader.endObject();

		    Log.d(TAG, artifact.toString());
		    
		    return artifact;
		}
		
	}
	
}
