package org.sfm.beans;

import org.springframework.jdbc.roma.config.provider.annotation.RowMapperField;


public class SmallBenchmarkObject {
	
	@RowMapperField(columnName="id")
	private long id;
	@RowMapperField(columnName="year_started")
	private int yearStarted;
	@RowMapperField(columnName="name")
	private String name;
	@RowMapperField(columnName="email")
	private String email;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
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
	public int getYearStarted() {
		return yearStarted;
	}
	public void setYearStarted(int yearStarted) {
		this.yearStarted = yearStarted;
	}
}
