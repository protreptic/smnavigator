package ru.magnat.smnavigator.update;

import java.util.StringTokenizer;

import android.text.TextUtils;

public class Version implements Comparable<Version> {
	
	private int major;
	private int minor;
	private int patch;
	
	private String preRelease;
	private int preReleaseVersion;
	
	public int getMajor() {
		return major;
	}

	public void setMajor(int major) {
		this.major = major;
	}

	public int getMinor() {
		return minor;
	}

	public void setMinor(int minor) {
		this.minor = minor;
	}

	public int getPatch() {
		return patch;
	}

	public void setPatch(int patch) {
		this.patch = patch;
	}
	
	public String getPreRelease() {
		return preRelease;
	}

	public void setPreRelease(String preRelease) {
		this.preRelease = preRelease;
	}
	
	public int getPreReleaseVersion() {
		return preReleaseVersion;
	}

	public void setPreReleaseVersion(int preReleaseVersion) {
		this.preReleaseVersion = preReleaseVersion;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == null) throw new NullPointerException();
		if (!(object instanceof Version)) throw new IllegalArgumentException();  
		if (this == object) return true; 
		
		Version version = (Version) object;
		
		boolean result = false;
		
		if (this.major == version.major && this.minor == version.minor && this.patch == version.patch) { result = true; } else { result = false; };
				
		return result;
	}
	
	@Override
	public int compareTo(Version version) {
		if (version == null) return -1;
		
		if (this.major < version.major) return -1; if (this.major > version.major) return 1;
		if (this.minor < version.minor) return -1; if (this.minor > version.minor) return 1;
		if (this.patch < version.patch) return -1; if (this.patch > version.patch) return 1;
		
		return 0;
	}
	
	@Override
	public String toString() {
		return this.major + "." + this.minor + "." + this.patch + (TextUtils.isEmpty(preRelease) ? "" : "-" + preRelease + "." + preReleaseVersion);
	}
	
	public static Version parseString(String versionString) {
		Version version = new Version();
		
		String versionPart;
		String preReleasePart;
		
		StringTokenizer versionStringTokenizer = new StringTokenizer(versionString, "-");
				
		if (versionStringTokenizer.countTokens() > 0) {
			versionPart = versionStringTokenizer.nextToken();
			
			StringTokenizer versionPartTokenizer = new StringTokenizer(versionPart, ".");
			
			version.setMajor(Integer.valueOf(versionPartTokenizer.nextToken()));  
			version.setMinor(Integer.valueOf(versionPartTokenizer.nextToken()));
			version.setPatch(Integer.valueOf(versionPartTokenizer.nextToken()));
			
			if (versionStringTokenizer.hasMoreTokens()) {
				preReleasePart = versionStringTokenizer.nextToken();
				
				StringTokenizer preReleasePartTokenizer = new StringTokenizer(preReleasePart, ".");
				
				version.setPreRelease(preReleasePartTokenizer.nextToken());  
				version.setPreReleaseVersion(Integer.valueOf(preReleasePartTokenizer.nextToken()));
			}
		} else 
			throw new RuntimeException();
		
		return version;
	}
	
}
