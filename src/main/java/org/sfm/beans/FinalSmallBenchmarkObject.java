package org.sfm.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


public class FinalSmallBenchmarkObject {
	
	private final long id;
	
	private final int yearStarted;
	private final String name;
	private final String email;

    public FinalSmallBenchmarkObject(long id, int yearStarted, String name, String email) {
        this.id = id;
        this.yearStarted = yearStarted;
        this.name = name;
        this.email = email;
    }

    public long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getEmail() {
		return email;
	}

	public int getYearStarted() {
		return yearStarted;
	}

	@Override
	public String toString() {
		return "FinalSmallBenchmarkObject [id=" + id + ", yearStarted="
				+ yearStarted + ", name=" + name + ", email=" + email + "]";
	}
	
	
}
