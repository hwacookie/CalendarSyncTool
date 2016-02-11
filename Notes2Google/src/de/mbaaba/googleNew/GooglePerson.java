/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.googleNew;

import java.util.List;

import com.google.gdata.data.calendar.EventWho;

import de.mbaaba.calendar.Person;

/**
 * The Class GooglePerson.
 */
public class GooglePerson extends Person {

	public GooglePerson(EventWho aEventWho) {
		//TODO: check how to set the rest of the fields.
		setINetAdress(aEventWho.getEmail());
	}

	public GooglePerson(com.google.gdata.data.Person aPerson) {
		final String fn = aPerson.getName();
		setFirstName(fn);
		setLastName(fn);
		setINetAdress(aPerson.getEmail());
	}

	/**
	 * 
	 * Returns list of Persons found for the given name 
	 *
	 * @param aPerson name of the Person to find
	 * 
	 * @return list with Person found for the given name 
	 */
	public static List<Person> findPerson(String aPerson) {
		return null;
	}

}
