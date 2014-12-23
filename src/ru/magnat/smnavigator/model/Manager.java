package ru.magnat.smnavigator.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "manager")
public class Manager {
	
	@DatabaseField(columnName = "id", id = true)
	private Integer id;
	 
	@DatabaseField(columnName = "name")
	private String name;

	@DatabaseField(columnName = "email")
	private String email;
	
	@DatabaseField(columnName = "tel")
	private String tel;
	
	@DatabaseField(columnName = "branch", foreign = true, foreignAutoRefresh = true)
	private Branch branch;
	
	@DatabaseField(columnName = "department", foreign = true, foreignAutoRefresh = true)
	private Department department;
	
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [id=" + id + ", name=" + name + ", email=" + email + ", tel=" + tel + ", branch=" + branch + ", department=" + department + "]";
	}
	
}
