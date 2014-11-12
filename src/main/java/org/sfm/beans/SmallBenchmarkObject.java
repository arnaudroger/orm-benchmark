package org.sfm.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.jdbc.roma.config.provider.annotation.RowMapperField;

@Entity
@Table(name="test_small_benchmark_object")
public class SmallBenchmarkObject {
	
	@RowMapperField(columnName="id")
	@Id
	private long id;
	
	@RowMapperField(columnName="year_started")
	private int yearStarted;
	@RowMapperField(columnName="name")
	private String name;
	@RowMapperField(columnName="email")
	private String email;
	
	@Column(name="id")
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	@Column(name="name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(name="email")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column(name="year_started")
	public int getYearStarted() {
		return yearStarted;
	}
	public void setYearStarted(int yearStarted) {
		this.yearStarted = yearStarted;
	}
	@Override
	public String toString() {
		return "SmallBenchmarkObject [id=" + id + ", yearStarted="
				+ yearStarted + ", name=" + name + ", email=" + email + "]";
	}
	
	
}
