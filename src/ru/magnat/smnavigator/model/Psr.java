package ru.magnat.smnavigator.model;

import java.util.List;

import org.javaprotrepticon.android.androidutils.Text;

import ru.magnat.smnavigator.model.base.Mappable;
import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "psr")
public class Psr implements Parcelable, Mappable {
	
	@DatabaseField(columnName = "id", id = true)
	private Integer id;
	
	@DatabaseField(columnName = "name")
	private String name;
	
	@DatabaseField(columnName = "project")
	private String project;
	
	@DatabaseField(columnName = "email")
	private String email;
	
	@DatabaseField(columnName = "tel")
	private String tel;
	
	@DatabaseField(columnName = "branch", foreign = true, foreignAutoRefresh = true)
	private Branch branch;
	
	@DatabaseField(columnName = "department", foreign = true, foreignAutoRefresh = true)
	private Department department;
	
	@DatabaseField(columnName = "latitude")
	private Double latitude;
	
	@DatabaseField(columnName = "longitude")
	private Double longitude;
	
	@ForeignCollectionField(eager = false)
	private ForeignCollection<Route> routes;
	
	public Psr() {}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProject() {
		return Text.prepareAddress(project);
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}
	
	public List<Route> getPsrRoutes() {
		return null;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public ForeignCollection<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(ForeignCollection<Route> routes) {
		this.routes = routes;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [id=" + id + ", name=" + name + ", email=" + email + ", tel=" + tel + ", branch=" + branch + ", department=" + department + "]";
	}
	
	private Psr(Parcel parcel) {
		setId(parcel.readInt()); 
		setName(parcel.readString());  
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(getId()); 
		parcel.writeString(getName());
	} 
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<Psr> CREATOR = new Parcelable.Creator<Psr>() {
		public Psr createFromParcel(Parcel parcel) {
			return new Psr(parcel);
		}

		public Psr[] newArray(int size) {
			return new Psr[size];
		}
	};
	
}
