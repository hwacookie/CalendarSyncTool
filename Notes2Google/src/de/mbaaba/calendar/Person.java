/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.calendar;

import org.apache.commons.lang.builder.HashCodeBuilder;

import de.mbaaba.util.ObjectUtil;

/**
 * The Class Person encapsulates all data of a person.
 */
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
			final Person other = (Person) aObj;
			return ObjectUtil.objectEquals(firstName, other.firstName) && ObjectUtil.objectEquals(lastName, other.lastName)
					&& ObjectUtil.objectEquals(iNetAdress, other.iNetAdress) && ObjectUtil.objectEquals(phoneJob, other.phoneJob)
					&& ObjectUtil.objectEquals(phoneMobile, other.phoneMobile);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
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
		if ((getFirstName()!=null) || (getLastName()!=null)) {
			return ((getFirstName() != null) ? getFirstName() + " " : "") + ((getLastName() != null) ? getLastName() + " " : "")
					+ "<" + getINetAdress() + ">";
		} else {
			if (getINetAdress()!=null) {
				return getINetAdress().substring(0,getINetAdress().indexOf("@"))+ " <" + getINetAdress() + ">";
			} else {
				return "";
			}
		}
	}

	public String getShortContactInfo() {
		return getINetAdress() + "," + getPhoneJob();
	}

	public String getURI() {
		return "mailto:" + getINetAdress();
	}

}
