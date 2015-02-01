package org.sfm.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="test_small_benchmark_object")
public class SmallBenchmarkObject {
	
	@Id
	private long id;
	
	private int yearStarted;
	private String name;
	private String email;
	
	@Column(name="ID")
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	@Column(name="NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(name="EMAIL")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column(name="YEAR_STARTED")
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
