/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.iCal;

import java.util.ArrayList;
import java.util.List;

import de.mbaaba.calendar.ItemNotFoundException;
import de.mbaaba.calendar.Person;
import lotus.domino.Session;

/**
 * The Class NotesPerson.
 */
public class ICalPerson extends Person {

	public static void init(@SuppressWarnings("unused") Session aSession) {
		// Nothing to configure
	}
	
	public static List<Person> findPerson(String aName) throws ItemNotFoundException {
		final List<Person> res = new ArrayList<Person>();
		ICalPerson person = new ICalPerson();
		person.setFirstName("");
		person.setLastName(aName);
		res.add(person);
		return res;
	}


}
