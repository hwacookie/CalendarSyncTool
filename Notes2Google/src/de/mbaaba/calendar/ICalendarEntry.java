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

public interface ICalendarEntry {

	public abstract String getBody();

	public abstract void setBody(String aBody);

	public abstract Person getChair();

	public abstract void setChair(Person aChair);

	public abstract String getLocation();

	public abstract void setLocation(String aLocation);

	public abstract String getRoom();

	public abstract void setRoom(String aRoom);

	public abstract List<Person> getAttendees();

	public abstract void setAttendees(List<Person> aAttendees);

	public abstract ArrayList<Date> getStartDates();

	public abstract void addStartDate(Date aStartDate);

	public abstract ArrayList<Date> getEndDates();

	public abstract void addEndDate(Date aEndDate);

	public abstract Date getLastModified();

	public abstract void setLastModified(Date aLastModified);

	public abstract String getUniqueID();

	public abstract void setUniqueID(String aUniqueID);

	public abstract String getSubject();

	public abstract void setSubject(String aSubject);

	public abstract void addAttendee(String aAttendee, String aType);

	public abstract String getShortString();

	public abstract void setAlarmTime(Date alarmOffset);

	public abstract Date getAlarmTime();

}