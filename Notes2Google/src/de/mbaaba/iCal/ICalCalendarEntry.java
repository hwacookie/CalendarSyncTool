/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.iCal;

import java.util.Date;

import de.mbaaba.calendar.CalendarEntry;
import de.mbaaba.calendar.ICalendarEntry;
import de.mbaaba.calendar.PersonFactory;
import de.mbaaba.calendar.PersonFactory.CalendarType;
import net.fortuna.ical4j.model.component.VEvent;

/**
 * The Class ICalCalendarEntry.
 */
public class ICalCalendarEntry extends CalendarEntry {

	public ICalCalendarEntry(ICalendarEntry aCalendarEntry) {
		super(aCalendarEntry);
	}

	public ICalCalendarEntry(VEvent aVEvent) {
		// TODO: read all attributes from a VEvent.
		// setAttendees(aVEvent.getcopy.getAttendees());
		// setBody(copy.getBody());
		// setChair(copy.getChair());
		// setEndDate(copy.getEndDate());
		// setLastModified(copy.getLastModified());
		// setLocation(copy.getLocation());
		// setRoom(copy.getRoom());
		addStartDate(new Date(aVEvent.getStartDate().getDate().getTime()));
		addEndDate(new Date(aVEvent.getEndDate().getDate().getTime()));
		setSubject(aVEvent.getSummary().getValue());
		setUniqueID(aVEvent.getUid().getValue());
		
		
		setAlarmTime(null);
		try {
			if (aVEvent.getOrganizer()!=null) { 
				setAttendees(PersonFactory.findPerson(CalendarType.iCal, aVEvent.getOrganizer().getValue()));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		setBody(aVEvent.getDescription().getValue());
		try {
			if (aVEvent.getOrganizer()!=null) { 
				setChair(PersonFactory.findPerson(CalendarType.iCal, aVEvent.getOrganizer().getValue()).get(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		setLastModified(new Date(0));
		setLocation(aVEvent.getLocation().getValue());
		setRoom("");
		setSubject(aVEvent.getName());
		setUniqueID(aVEvent.getUid().getValue());
	}

}
