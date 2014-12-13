package ru.magnat.smnavigator.update;

public class Artifact implements Comparable<Artifact> {
	
	private String artifactId;
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
	
	@Override
	public String toString() {
		return "Artifact [artifactId=" + artifactId + ", packageName=" + packageName + ", description=" + description + ", versionCode=" + versionCode + ", versionName=" + versionName + "]";
	}
	@Override
	public int compareTo(Artifact another) {
		// TODO Auto-generated method stub
		return 0;
	}
}
