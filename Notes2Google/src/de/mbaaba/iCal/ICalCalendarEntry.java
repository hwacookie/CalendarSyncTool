/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.iCal;

import net.fortuna.ical4j.model.component.VEvent;
import de.mbaaba.calendar.CalendarEntry;
import de.mbaaba.calendar.ICalendarEntry;

public class ICalCalendarEntry extends CalendarEntry {

	public ICalCalendarEntry(ICalendarEntry aCalendarEntry) {
		super(aCalendarEntry);
	}

	public ICalCalendarEntry(VEvent aVEvent) {
		// TODO: read all attributes from a VEvent.
		// setAttendees(copy.getAttendees());
		// setBody(copy.getBody());
		// setChair(copy.getChair());
		// setEndDate(copy.getEndDate());
		// setLastModified(copy.getLastModified());
		// setLocation(copy.getLocation());
		// setRoom(copy.getRoom());
		// setStartDate(copy.getStartDate());
		setSubject(aVEvent.getSummary().getValue());
		setUniqueID(aVEvent.getUid().getValue());
		throw new RuntimeException("Reading from iCal Calendar is not yet supported.");
	}

}
