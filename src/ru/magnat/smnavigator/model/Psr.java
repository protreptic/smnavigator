package ru.magnat.smnavigator.model;

import java.util.List;

import org.javaprotrepticon.android.androidutils.Text;

import ru.magnat.smnavigator.model.json.BranchDeserializer;
import ru.magnat.smnavigator.model.json.BranchSerializer;
import ru.magnat.smnavigator.model.json.DepartmentDeserializer;
import ru.magnat.smnavigator.model.json.DepartmentSerializer;
import ru.magnat.smnavigator.model.json.PsrDeserializer;
import ru.magnat.smnavigator.model.json.PsrSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "psr")
public class Psr implements Mappable {
	
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
		return getClass().getSimpleName() + " [id=" + id + ", name=" + name + ", project=" + project + ", email=" + email + ", tel=" + tel + ", branch=" + branch + ", department=" + department + "]";
	}
	
	public String toJson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Psr.class, new PsrSerializer()); 
		gsonBuilder.registerTypeAdapter(Psr.class, new PsrDeserializer());
		gsonBuilder.registerTypeAdapter(Branch.class, new BranchSerializer()); 
		gsonBuilder.registerTypeAdapter(Branch.class, new BranchDeserializer());
		gsonBuilder.registerTypeAdapter(Department.class, new DepartmentSerializer()); 
		gsonBuilder.registerTypeAdapter(Department.class, new DepartmentDeserializer()); 
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.create();
		
		return gson.toJson(this);
	}
	
	public static Psr fromJson(String jsonString) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Psr.class, new PsrSerializer()); 
		gsonBuilder.registerTypeAdapter(Psr.class, new PsrDeserializer());
		gsonBuilder.registerTypeAdapter(Branch.class, new BranchSerializer()); 
		gsonBuilder.registerTypeAdapter(Branch.class, new BranchDeserializer());
		gsonBuilder.registerTypeAdapter(Department.class, new DepartmentSerializer()); 
		gsonBuilder.registerTypeAdapter(Department.class, new DepartmentDeserializer()); 
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.create();
		
		return gson.fromJson(jsonString, Psr.class);
	}
	
}
