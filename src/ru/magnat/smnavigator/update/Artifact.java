package ru.magnat.smnavigator.update;

import android.os.Parcel;
import android.os.Parcelable;

public class Artifact implements Parcelable {
	
	private String artifactId;
	private String artifactName;
	private String packageName;
	private String description;
	private String versionCode;
	private String versionName;
	
	public String getArtifactId() {
		return artifactId;
	}
	
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	
	public String getArtifactName() {
		return artifactName;
	}
	
	public void setArtifactName(String artifactName) {
		this.artifactName = artifactName;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getVersionCode() {
		return versionCode;
	}
	
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}
	
	public String getVersionName() {
		return versionName;
	}
	
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	
	public Version getVersion() {
		return Version.parseString(versionName);
	}
	
	@Override
	public String toString() {
		return "Artifact [artifactId=" + artifactId + ", artifactName=" + artifactName + ", packageName=" + packageName + ", description=" + description + ", versionCode=" + versionCode + ", versionName=" + versionName + "]";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(artifactId);
		parcel.writeString(artifactName);
		parcel.writeString(packageName);
		parcel.writeString(description);
		parcel.writeString(versionCode);
		parcel.writeString(versionName);
	}
	
	public static final Parcelable.Creator<Artifact> CREATOR = new Parcelable.Creator<Artifact>() {
		
		public Artifact createFromParcel(Parcel parcel) {
			Artifact artifact = new Artifact();
			artifact.setArtifactId(parcel.readString()); 
			artifact.setArtifactName(parcel.readString()); 
			artifact.setPackageName(parcel.readString()); 
			artifact.setDescription(parcel.readString()); 
			artifact.setVersionCode(parcel.readString());
			artifact.setVersionName(parcel.readString()); 
			
		    return artifact;
		}
		
		public Artifact[] newArray(int size) {
		    return new Artifact[size];
		}
		
	};
	
}
