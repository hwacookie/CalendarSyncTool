/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.googleNew;

import java.util.Date;
import java.util.List;

import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.EventWho;
import com.google.gdata.data.extensions.When;
import com.google.gdata.data.extensions.Where;

import de.mbaaba.calendar.CalendarEntry;
import de.mbaaba.calendar.ICalendarEntry;

/**
 * The Class GoogleCalendarEntry.
 */
public class GoogleCalendarEntry extends CalendarEntry {

	private String googleID;

	public GoogleCalendarEntry(ICalendarEntry aCalendarEntry) {
		super(aCalendarEntry);
	}

	public GoogleCalendarEntry(CalendarEventEntry aEntry) {


		final Date lastModified = new Date(aEntry.getUpdated().getValue());
		setLastModified(lastModified);
		final List<EventWho> participants = aEntry.getParticipants();
		for (final EventWho eventWho : participants) {
			getAttendees().add(new GooglePerson(eventWho));
		}
		setBody(aEntry.getPlainTextContent());

		final List<com.google.gdata.data.Person> list = aEntry.getAuthors();
		if ((list != null) && (list.size() > 0)) {
			final com.google.gdata.data.Person firstAuthor = list.get(0);
			if (firstAuthor != null) {
				setChair(new GooglePerson(firstAuthor));
			}
		}

		for (final When when : aEntry.getTimes()) {
			if (when != null) {
				addStartDate(new Date(when.getStartTime().getValue()));
				addEndDate(new Date(when.getEndTime().getValue()));
			}
		}

		final List<Where> locations = aEntry.getLocations();
		if ((locations != null) && (locations.size() > 0)) {
			final Where where = locations.get(0);
			if (where != null) {
				setLocation(where.getValueString());
			}
		}

		if (aEntry.getTitle() != null) {
			setSubject(aEntry.getTitle().getPlainText());
		} else {
			setSubject("No Summary");
		}
		setGoogleID(aEntry.getId());
		final String notesID = "";
		setUniqueID(notesID);
	}

	public void setGoogleID(String aGoogleID) {
		this.googleID = aGoogleID;
	}

	public String getGoogleID() {
		return googleID;
	}
}
