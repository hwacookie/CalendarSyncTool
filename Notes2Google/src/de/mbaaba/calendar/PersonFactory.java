/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.calendar;

import java.util.List;

import de.mbaaba.google.GooglePerson;
import de.mbaaba.notes.NotesPerson;

public class PersonFactory {
	
	public enum CalendarType {
		Notes,
		Google
	}
	

	public static List<Person> findPerson(CalendarType aCalendarType, String aPerson) throws ItemNotFoundException {
		switch (aCalendarType) {
		case Notes: 
			return NotesPerson.findPerson(aPerson);
		case Google: 
			return GooglePerson.findPerson(aPerson);
			
		}
		return null;
	}
	

}
