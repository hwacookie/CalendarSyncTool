/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.calendar;

import java.util.List;

import de.mbaaba.googleNew.GooglePerson;
import de.mbaaba.iCal.ICalPerson;
import de.mbaaba.notes.NotesPerson;

/**
 * A factory for finding Person objects.
 */
public abstract class PersonFactory {

	/** 
	 * No initialization.
	 */
	private PersonFactory() {

	}

	/**
	 * The different calendar types.
	 */
	public enum CalendarType {
		Notes, Google, iCal
	}

	public static List<Person> findPerson(CalendarType aCalendarType, String aPerson) throws ItemNotFoundException {
		switch (aCalendarType) {
		case Notes:
			return NotesPerson.findPerson(aPerson);
		case Google:
			return GooglePerson.findPerson(aPerson);
		case iCal:
			return ICalPerson.findPerson(aPerson);
		default:
			break;

		}
		return null;
	}

}
