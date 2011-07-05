/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.google;

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
		String fn = aPerson.getName();
		setFirstName(fn);
		setLastName(fn);
		setINetAdress(aPerson.getEmail());
	}

	public static List<Person> findPerson(String aPerson) {
		return null;
	}

}
