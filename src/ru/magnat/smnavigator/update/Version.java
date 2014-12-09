package ru.magnat.smnavigator.update;

public class Version implements Comparable<Version> {
	
	private int major;
	private int minor;
	private int patch;
	
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

	@Override
	public boolean equals(Object o) {
		if (o == null) throw new NullPointerException();
		if (!(o instanceof Version)) throw new IllegalArgumentException();  
		if (this == o) return true; 
		
		Version version = (Version) o;
		
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
		return this.major + "." + this.minor + "." + this.patch;
	}
	
}
