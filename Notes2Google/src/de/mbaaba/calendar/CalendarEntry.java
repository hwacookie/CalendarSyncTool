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

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.thoughtworks.xstream.XStream;

import de.mbaaba.util.Logger;
import de.mbaaba.util.ObjectUtil;

/**
 * The Class CalendarEntry.
 */
public class CalendarEntry implements ICalendarEntry {

	/**
	 * A logger for this class.
	 */
	private static final Logger LOG = new Logger(CalendarEntry.class);

	/** The Constant UNKNOWN_PERSON. */
	private static final Person UNKNOWN_PERSON = new Person();

	/** The subject. */
	private String subject;

	/** The body. */
	private String body;

	/** The chair. */
	private Person chair;

	/** The location. */
	private String location;

	/** The room. */
	private String room;

	/** The attendees. */
	private List<Person> attendees;

	/** The start dates. */
	private ArrayList<Date> startDates;

	/** The end dates. */
	private ArrayList<Date> endDates;

	/** The last modified. */
	private Date lastModified;

	/** The unique id. */
	private String uniqueID;

	/** The alarm time. */
	private Date alarmTime;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#getBody()
	 */
	@Override
	public String getBody() {
		return body;
	}

	/* (non-Javadoc)
	 * @see de.mbaaba.calendar.ICalendarEntry#setBody(java.lang.String)
	 */
	@Override
	public void setBody(String aBody) {
		body = aBody;
	}

	@Override
	public Person getChair() {
		return chair;
	}

	@Override
	public void setChair(Person aChair) {
		chair = aChair;
	}

	@Override
	public String getLocation() {
		return location;
	}

	@Override
	public void setLocation(String aLocation) {
		location = aLocation;
	}

	@Override
	public String getRoom() {
		return room;
	}

	@Override
	public void setRoom(String aRoom) {
		room = aRoom;
	}

	@Override
	public List<Person> getAttendees() {
		return attendees;
	}

	@Override
	public void setAttendees(List<Person> aAttendees) {
		attendees = aAttendees;
	}

	@Override
	public ArrayList<Date> getStartDates() {
		return startDates;
	}

	@Override
	public void addStartDate(Date aStartDate) {
		startDates.add(aStartDate);
	}

	@Override
	public ArrayList<Date> getEndDates() {
		return endDates;
	}

	@Override
	public void addEndDate(Date aEndDate) {
		endDates.add(aEndDate);
	}

	@Override
	public Date getLastModified() {
		return lastModified;
	}

	@Override
	public void setLastModified(Date aLastModified) {
		lastModified = aLastModified;
	}

	@Override
	public String getUniqueID() {
		return uniqueID;
	}

	@Override
	public void setUniqueID(String aUniqueID) {
		uniqueID = aUniqueID;
	}

	@Override
	public String getSubject() {
		return subject;
	}

	/**
	 * Instantiates a new calendar entry.
	 */
	public CalendarEntry() {
		startDates = new ArrayList<Date>();
		endDates = new ArrayList<Date>();
		attendees = new ArrayList<Person>();
		setChair(UNKNOWN_PERSON);
	}

	@Override
	public void setSubject(String aSubject) {
		subject = aSubject;
	}

	@Override
	public void addAttendee(String aAttendee, String aType) {
		try {
			final List<Person> persons = PersonFactory.findPerson(PersonFactory.CalendarType.Notes, aAttendee);
			for (final Person person2 : persons) {
				attendees.add(person2);
			}
		} catch (final ItemNotFoundException localItemNotFoundException) {
			LOG.warn("Problem while attaching an attendee", localItemNotFoundException);
		}
	}

	@Override
	public String toString() {
		String toStringLocation = "";
		if (getLocation() != null) {
			toStringLocation = getLocation();
		}
		if ((getRoom() != null) && (getRoom().length() > 0)) {
			if (toStringLocation.length() > 0) {
				toStringLocation = toStringLocation + " [" + getRoom() + "]";
			} else {
				toStringLocation = getRoom();
			}
		}
		String s = "---------------------------------------------\n";
		s = s + "ID: " + getUniqueID() + "\n";
		s = s + "Subject: " + getSubject() + "\n" + "StartDate: " + getStartDates() + "\n" + "EndDate: " + getEndDates() + "\n"
				+ "Location: " + getLocation() + "\n" + "\n" + "Chair: " + getChair().getPrettyMailAdress() + "\n"
				+ "Attendees:\n";
		for (final Person attendee : attendees) {
			s = s + attendee.getPrettyMailAdress() + "\n";
		}
		s = s + "---------------------------------------------\n";
		if (getBody() != null) {
			s = s + getBody() + "\n";
		}
		return s;
	}

	@Override
	public String getShortString() {
		return "\"" + getSubject() + "\" (" + getUniqueID() + ")";
	}

	/**
	 * Instantiates a new calendar entry.
	 *
	 * @param aCalendarEntry the calendar entry
	 */
	public CalendarEntry(ICalendarEntry aCalendarEntry) {
		this();
		copyFrom(aCalendarEntry);
	}

	/**
	 * Copy from.
	 *
	 * @param aCalendarEntry the calendar entry
	 */
	public void copyFrom(ICalendarEntry aCalendarEntry) {
		final XStream xStream = new XStream();
		final String xml = xStream.toXML(aCalendarEntry);
		final CalendarEntry copy = (CalendarEntry) xStream.fromXML(xml);
		setAttendees(copy.getAttendees());
		setBody(copy.getBody());
		setChair(copy.getChair());
		for (final Date date : copy.getEndDates()) {
			addEndDate(date);
		}
		for (final Date date : copy.getStartDates()) {
			addStartDate(date);
		}
		setLastModified(copy.getLastModified());
		setLocation(copy.getLocation());
		setRoom(copy.getRoom());
		setSubject(copy.getSubject());
		setUniqueID(copy.getUniqueID());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#setAlarmTime(java.util.Date)
	 */
	@Override
	public void setAlarmTime(Date aAlarmOffset) {
		alarmTime = aAlarmOffset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#getAlarmTime()
	 */
	@Override
	public Date getAlarmTime() {
		return alarmTime;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object aObj) {
		if (aObj instanceof CalendarEntry) {
			final CalendarEntry other = (CalendarEntry) aObj;
			if (!uniqueID.equals(other.uniqueID)) {
				return false;
			}
			final boolean isEqual = ObjectUtil.objectEquals(lastModified, other.lastModified)
					&& ObjectUtil.objectEquals(subject, other.subject) && ObjectUtil.objectEquals(body, other.body)
					&& ObjectUtil.objectEquals(chair, other.chair) && ObjectUtil.objectEquals(location, other.location)
					&& ObjectUtil.objectEquals(alarmTime, other.alarmTime) && ObjectUtil.objectEquals(room, other.room)
					&& ObjectUtil.objectEquals(startDates, other.startDates) && ObjectUtil.objectEquals(endDates, other.endDates);
			return isEqual;
		}
		return false;
	}

	/**
	 * Calculates the hashCode for this calendar entry, using the {@link HashCodeBuilder#reflectionHashCode(Object)} utility.
	 */
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}