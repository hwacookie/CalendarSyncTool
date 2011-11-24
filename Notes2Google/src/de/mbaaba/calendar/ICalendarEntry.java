/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.calendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.mbaaba.notes.AcceptStatus;

/**
 * The Interface ICalendarEntry.
 */
public interface ICalendarEntry {

	/**
	 * Gets the body.
	 *
	 * @return the body
	 */
	public abstract String getBody();

	/**
	 * Sets the body.
	 *
	 * @param aBody the new body
	 */
	public abstract void setBody(String aBody);

	/**
	 * Gets the chair.
	 *
	 * @return the chair
	 */
	public abstract Person getChair();

	/**
	 * Sets the chair.
	 *
	 * @param aChair the new chair
	 */
	public abstract void setChair(Person aChair);

	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	public abstract String getLocation();

	/**
	 * Sets the location.
	 *
	 * @param aLocation the new location
	 */
	public abstract void setLocation(String aLocation);

	/**
	 * Gets the room.
	 *
	 * @return the room
	 */
	public abstract String getRoom();

	/**
	 * Sets the room.
	 *
	 * @param aRoom the new room
	 */
	public abstract void setRoom(String aRoom);

	/**
	 * Gets the attendees.
	 *
	 * @return the attendees
	 */
	public abstract List<Person> getAttendees();

	/**
	 * Sets the attendees.
	 *
	 * @param aAttendees the new attendees
	 */
	public abstract void setAttendees(List<Person> aAttendees);

	/**
	 * Gets the start dates.
	 *
	 * @return the start dates
	 */
	public abstract ArrayList<Date> getStartDates();

	/**
	 * Adds the start date.
	 *
	 * @param aStartDate the start date
	 */
	public abstract void addStartDate(Date aStartDate);

	/**
	 * Gets the end dates.
	 *
	 * @return the end dates
	 */
	public abstract ArrayList<Date> getEndDates();

	/**
	 * Adds the end date.
	 *
	 * @param aEndDate the end date
	 */
	public abstract void addEndDate(Date aEndDate);

	/**
	 * Gets the last modified.
	 *
	 * @return the last modified
	 */
	public abstract Date getLastModified();

	/**
	 * Sets the last modified.
	 *
	 * @param aLastModified the new last modified
	 */
	public abstract void setLastModified(Date aLastModified);

	/**
	 * Gets the unique id.
	 *
	 * @return the unique id
	 */
	public abstract String getUniqueID();

	/**
	 * Sets the unique id.
	 *
	 * @param aUniqueID the new unique id
	 */
	public abstract void setUniqueID(String aUniqueID);

	/**
	 * Gets the subject.
	 *
	 * @return the subject
	 */
	public abstract String getSubject();

	/**
	 * Sets the subject.
	 *
	 * @param aSubject the new subject
	 */
	public abstract void setSubject(String aSubject);

	/**
	 * Adds the attendee.
	 *
	 * @param aAttendee the attendee
	 * @param aType the type
	 */
	public abstract void addAttendee(String aAttendee, String aType);

	/**
	 * Gets the short string.
	 *
	 * @return the short string
	 */
	public abstract String getShortString();

	/**
	 * Sets the alarm time.
	 *
	 * @param aAlarmOffset the new alarm time
	 */
	public abstract void setAlarmTime(Date aAlarmOffset);

	/**
	 * Gets the alarm time.
	 *
	 * @return the alarm time
	 */
	public abstract Date getAlarmTime();

	public abstract AcceptStatus getAcceptStatus();
	
	public abstract void sanityCheck();
	
}