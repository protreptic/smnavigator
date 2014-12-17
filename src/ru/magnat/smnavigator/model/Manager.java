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
	
	@DatabaseField(columnName = "branch")
	private Integer branch;
	
	@DatabaseField(columnName = "department")
	private Integer department;
	
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

	public Integer getBranch() {
		return branch;
	}

	public void setBranch(Integer branch) {
		this.branch = branch;
	}

	public Integer getDepartment() {
		return department;
	}

	public void setDepartment(Integer department) {
		this.department = department;
	}
	
	@Override
	public String toString() {
		return getClass().getName() + " [id=" + id + ", name=" + name + ", email=" + email + ", tel=" + tel + ", branch=" + branch + ", department=" + department + "]";
	}
	
}
