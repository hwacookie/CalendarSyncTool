/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */


package de.mbaaba.calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.thoughtworks.xstream.XStream;

import de.mbaaba.util.ObjectUtil;

/**
 * The Class CalendarEntry.
 */
public class CalendarEntry implements ICalendarEntry {
	
	/** The Constant sdf. */
	private static final DateFormat sdf = SimpleDateFormat.getDateTimeInstance();
	
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
	public String getBody() {
		return body;
	}

	/* (non-Javadoc)
	 * @see de.mbaaba.calendar.ICalendarEntry#setBody(java.lang.String)
	 */
	public void setBody(String aBody) {
		body = aBody;
	}

	public Person getChair() {
		return chair;
	}

	public void setChair(Person aChair) {
		chair = aChair;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String aLocation) {
		location = aLocation;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String aRoom) {
		room = aRoom;
	}

	public List<Person> getAttendees() {
		return attendees;
	}

	public void setAttendees(List<Person> aAttendees) {
		attendees = aAttendees;
	}

	public ArrayList<Date> getStartDates() {
		return startDates;
	}

	public void addStartDate(Date aStartDate) {
		startDates.add(aStartDate);
	}

	public ArrayList<Date> getEndDates() {
		return endDates;
	}

	public void addEndDate(Date aEndDate) {
		endDates.add(aEndDate);
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date aLastModified) {
		lastModified = aLastModified;
	}

	public String getUniqueID() {
		return uniqueID;
	}

	public void setUniqueID(String aUniqueID) {
		uniqueID = aUniqueID;
	}

	public String getSubject() {
		return subject;
	}

	/**
	 * Instantiates a new calendar entry.
	 */
	public CalendarEntry() {
		startDates = new ArrayList<Date>();
		endDates = new ArrayList<Date>();
		sdf.setLenient(true);
		attendees = new ArrayList<Person>();
		setChair(UNKNOWN_PERSON);
	}


	public void setSubject(String aSubject) {
		subject = aSubject;
	}


	public void addAttendee(String aAttendee, String aType) {
		try {
			List<Person> persons = PersonFactory.findPerson(PersonFactory.CalendarType.Notes, aAttendee);
			for (Person person2 : persons) {
				attendees.add(person2);
			}
		} catch (ItemNotFoundException localItemNotFoundException) {
		}
	}

	public String toString() {
		String location = "";
		if (getLocation() != null) {
			location = getLocation();
		}
		if ((getRoom() != null) && (getRoom().length() > 0)) {
			if (location.length() > 0)
				location = location + " [" + getRoom() + "]";
			else {
				location = getRoom();
			}
		}
		String s = "---------------------------------------------\n";
		s = s + "ID: " + getUniqueID() + "\n";
		s = s + "Subject: " + getSubject() + "\n" + "StartDate: " + getStartDates() + "\n" + "EndDate: " + getEndDates() + "\n" + "Location: " + getLocation() + "\n"  
				+ "\n" + "Chair: " + getChair().getPrettyMailAdress() + "\n" + "Attendees:\n";
		for (Person attendee : attendees) {
			s = s + attendee.getPrettyMailAdress() + "\n";
		}
		s = s + "---------------------------------------------\n";
		if (getBody() != null) {
			s = s + getBody() + "\n";
		}
		return s;
	}


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
		XStream xStream = new XStream();
		String xml = xStream.toXML(aCalendarEntry);
		CalendarEntry copy = (CalendarEntry) xStream.fromXML(xml);
		setAttendees(copy.getAttendees());
		setBody(copy.getBody());
		setChair(copy.getChair());
		for (Date date : copy.getEndDates()) {
			addEndDate(date);
		}
		for (Date date : copy.getStartDates()) {
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
	public void setAlarmTime(Date alarmOffset) {
		alarmTime = alarmOffset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#getAlarmTime()
	 */
	public Date getAlarmTime() {
		return alarmTime;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object aObj) {
		if (aObj instanceof CalendarEntry) {
			CalendarEntry other = (CalendarEntry) aObj;
			if (!uniqueID.equals(other.uniqueID)) {
				return false;
			}
			boolean isEqual = ObjectUtil.objectEquals(lastModified, other.lastModified) && ObjectUtil.objectEquals(subject, other.subject) && ObjectUtil.objectEquals(body, other.body)
					&& ObjectUtil.objectEquals(chair, other.chair) && ObjectUtil.objectEquals(location, other.location) && ObjectUtil.objectEquals(alarmTime, other.alarmTime)
					&& ObjectUtil.objectEquals(room, other.room) && ObjectUtil.objectEquals( startDates, other.startDates) && ObjectUtil.objectEquals( endDates, other.endDates);
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