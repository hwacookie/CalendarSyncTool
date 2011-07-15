/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.notes;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import lotus.domino.DateTime;
import lotus.domino.Item;
import lotus.domino.NotesException;
import de.mbaaba.calendar.CalendarEntry;
import de.mbaaba.calendar.ICalendarEntry;
import de.mbaaba.calendar.ItemNotFoundException;
import de.mbaaba.calendar.Person;
import de.mbaaba.calendar.PersonFactory;
import de.mbaaba.calendar.PersonFactory.CalendarType;

/**
 * The Class NotesCalendarEntry.
 */
public class NotesCalendarEntry extends CalendarEntry {

	private boolean confidential;

	public NotesCalendarEntry(ICalendarEntry aCalendarEntry) {
		super(aCalendarEntry);
	}

	public NotesCalendarEntry() {
	}

	public void mapItem(Item aItem) throws NotesException {
		final String itemName = aItem.getName().toLowerCase();
		// System.out.println(itemName + " = " + aItem.getDateTimeValue() + " "
		// + aItem.getValueString());
		if (itemName.equals("subject")) {
			setSubject(aItem.getValueString());
		} else if (itemName.equals("body")) {
			setBody(aItem.getValueString());
		} else if (itemName.equals("chair")) {
			try {
				final List<Person> findPerson = PersonFactory.findPerson(CalendarType.Notes, aItem.getValueString());
				if ((findPerson != null) && (findPerson.size() > 0)) {
					setChair(findPerson.get(0));
				}
			} catch (final ItemNotFoundException e) {
				// chair not found in DB
			}
		} else if (itemName.equals("location")) {
			setLocation(aItem.getValueString());
		} else if (itemName.equals("room")) {
			setRoom(aItem.getValueString());
		} else if (itemName.equals("orgconfidential")) {
			setConfidential(aItem.getValueString().equals("1"));
		} else if (itemName.equals("startdatetime")) {
			final Vector<?> dates = aItem.getValueDateTimeArray();
			for (final Object object : dates) {
				final Date javaDate = ((DateTime) object).toJavaDate();
				addStartDate(javaDate);
			}
		} else if (itemName.equals("enddatetime")) {
			final Vector<?> dates = aItem.getValueDateTimeArray();
			for (final Object object : dates) {
				final Date javaDate = ((DateTime) object).toJavaDate();
				addEndDate(javaDate);
			}
			//} else if (itemName.equals("$alarmoffset")) {
			// DateTime thisAlarmOffset = aItem.getDateTimeValue();
			// TODO: 5: Handle alarm settings
			// http://github.com/hwacookie/CalendarSyncTool/issues/issue/5
		} else if (itemName.equals("originalmodtime")) {
			setLastModified(aItem.getDateTimeValue().toJavaDate());
		} else if (itemName.equals("requiredattendees") || itemName.equals("optionalattendees")) {
			try {
				if (aItem.getValues() != null) {
					final Vector<?> v = aItem.getValues();
					for (final Object object : v) {
						final String s = object.toString();
						if (s.matches(".+@.+")) {
							addAttendee(s, itemName);
						}
					}
				}
			} catch (final NotesException e) {
				// TODO: check if the NotesException really occurs, and find a way to handle it properly
				// ignore all problems related to attendees
			}
		}

	}

	public boolean isConfidential() {
		return confidential;
	}

	public void setConfidential(boolean aConfidential) {
		confidential = aConfidential;
	}

	@Override
	public String toString() {
		String s = super.toString();
		s = s + "---------------------------------------------\n";
		s = s + "is confidential: " + isConfidential() + "\n";
		return s;
	}

}
