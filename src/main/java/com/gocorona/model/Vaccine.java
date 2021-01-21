package com.gocorona.model;

enum VaccineType{
	
	vaccine,
	placebo
	
}

public class Vaccine {
	
	public VaccineType getType() {
		return type;
	}
	public void setType(VaccineType type) {
		this.type = type;
	}
	
	public String getVaccineID() {
		return vaccineID;
	}
	public void setVaccineID(String vaccineID) {
		this.vaccineID = vaccineID;
	}

	String vaccineID;
	VaccineType type;

}
