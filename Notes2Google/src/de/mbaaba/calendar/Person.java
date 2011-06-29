/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */


package de.mbaaba.calendar;

public class Person {
	private String firstName;
	private String lastName;
	private String iNetAdress;
	private String phoneJob;
	private String phoneMobile;

	public Person() {

	}
	
	
	@Override
	public boolean equals(Object aObj) {
		if (aObj instanceof Person) {
			Person other = (Person) aObj;
			return CalendarEntry.myequals("firstName", firstName, other.firstName) &&
			CalendarEntry.myequals("lastName", lastName, other.lastName) &&
			CalendarEntry.myequals("iNetAdress", iNetAdress, other.iNetAdress) &&
			CalendarEntry.myequals("phoneJob", phoneJob, other.phoneJob) &&
			CalendarEntry.myequals("phoneMobile", phoneMobile, other.phoneMobile);
		}
		return false;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String aFirstName) {
		firstName = aFirstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String aLastName) {
		lastName = aLastName;
	}

	public String getINetAdress() {
		return iNetAdress;
	}

	public void setINetAdress(String aNetAdress) {
		iNetAdress = aNetAdress;
	}

	public String getPhoneJob() {
		return phoneJob;
	}

	public void setPhoneJob(String aPhoneJob) {
		phoneJob = aPhoneJob;
	}

	public String getPhoneMobile() {
		return phoneMobile;
	}

	public void setPhoneMobile(String aPhoneMobile) {
		phoneMobile = aPhoneMobile;
	}

	public String getPrettyMailAdress() {
		return ((getFirstName()!=null)?getFirstName() + " ":"") + 
			   ((getLastName()!=null)?getLastName()+" ":"") +
			   "<" + getINetAdress()
				+ ">";
	}

	public String getShortContactInfo() {
		return getINetAdress() + "," + getPhoneJob();
	}


	public String getURI() {
		return "mailto:" + getINetAdress();
	}

}
