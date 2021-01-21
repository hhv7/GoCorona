package com.gocorona.model;

public class Record {
	
	public Volunteer getVolunteer() {
		return volunteer;
	}
	public void setVolunteer(Volunteer volunteer) {
		this.volunteer = volunteer;
	}
	public Vaccine getVaccine() {
		return vaccine;
	}
	public void setVaccine(Vaccine vaccine) {
		this.vaccine = vaccine;
	}
	public boolean isPositive() {
		return test_positive;
	}
	public void setPositive(boolean test_positive) {
		this.test_positive = test_positive;
	}
	
	public double getDose() {
		return dose;
	}
	public void setDose(double dose) {
		this.dose = dose;
	}

	boolean test_positive;	
	Volunteer volunteer;
	Vaccine vaccine;
	double dose;

}
