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

public class CalendarEntry implements ICalendarEntry {
	private static final DateFormat sdf = SimpleDateFormat.getDateTimeInstance();
	private static final Person UNKNOWN_PERSON = new Person();

	private String subject;
	private String body;
	private Person chair;
	private String location;
	private String room;
	private List<Person> attendees;
	private ArrayList<Date> startDates;
	private ArrayList<Date> endDates;
	private Date lastModified;
	private String uniqueID;
	private boolean keepLocal;
	private Date alarmTime;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#getBody()
	 */
	public String getBody() {
		return body;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#setBody(java.lang.String)
	 */
	public void setBody(String aBody) {
		body = aBody;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#getChair()
	 */
	public Person getChair() {
		return chair;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mbaaba.calendar.ICalendarEntry#setChair(de.mbaaba.calendar.Person)
	 */
	public void setChair(Person aChair) {
		chair = aChair;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#getLocation()
	 */
	public String getLocation() {
		return location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#setLocation(java.lang.String)
	 */
	public void setLocation(String aLocation) {
		location = aLocation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#getRoom()
	 */
	public String getRoom() {
		return room;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#setRoom(java.lang.String)
	 */
	public void setRoom(String aRoom) {
		room = aRoom;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#getAttendees()
	 */
	public List<Person> getAttendees() {
		return attendees;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#setAttendees(java.util.List)
	 */
	public void setAttendees(List<Person> aAttendees) {
		attendees = aAttendees;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#getStartDates()
	 */
	public ArrayList<Date> getStartDates() {
		return startDates;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#addStartDate(java.util.Date)
	 */
	public void addStartDate(Date aStartDate) {
		startDates.add(aStartDate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#getEndDates()
	 */
	public ArrayList<Date> getEndDates() {
		return endDates;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#addEndDate(java.util.Date)
	 */
	public void addEndDate(Date aEndDate) {
		endDates.add(aEndDate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#getLastModified()
	 */
	public Date getLastModified() {
		return lastModified;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#setLastModified(java.util.Date)
	 */
	public void setLastModified(Date aLastModified) {
		lastModified = aLastModified;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#getUniqueID()
	 */
	public String getUniqueID() {
		return uniqueID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#setUniqueID(java.lang.String)
	 */
	public void setUniqueID(String aUniqueID) {
		uniqueID = aUniqueID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#getSubject()
	 */
	public String getSubject() {
		return subject;
	}

	public CalendarEntry() {
		startDates = new ArrayList<Date>();
		endDates = new ArrayList<Date>();
		sdf.setLenient(true);
		attendees = new ArrayList<Person>();
		setChair(UNKNOWN_PERSON);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#setSubject(java.lang.String)
	 */
	public void setSubject(String aSubject) {
		subject = aSubject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#addAttendee(java.lang.String,
	 * java.lang.String)
	 */
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
		s = s + "Subject: " + getSubject() + "\n" + "StartDate: " + getStartDates() + "\n" + "EndDate: " + getEndDates() + "\n" + "Location: " + getLocation() + "\n" + "is Private: " + isKeepLocal()
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mbaaba.calendar.ICalendarEntry#getShortString()
	 */
	public String getShortString() {
		return "\"" + getSubject() + "\" (" + getUniqueID() + ")";
	}

	public CalendarEntry(ICalendarEntry aCalendarEntry) {
		this();
		copyFrom(aCalendarEntry);
	}

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
		setKeepLocal(copy.isKeepLocal());
	}

	public void setKeepLocal(boolean keepPrivate) {
		keepLocal = keepPrivate;
	}

	public boolean isKeepLocal() {
		return keepLocal;
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

	@Override
	public boolean equals(Object aObj) {
		if (aObj instanceof CalendarEntry) {
			CalendarEntry other = (CalendarEntry) aObj;
			if (!uniqueID.equals(other.uniqueID)) {
				return false;
			}
			boolean isEqual = myequals("lastModified", lastModified, other.lastModified) && myequals("subject", subject, other.subject) && myequals("body", body, other.body)
					&& myequals("chair", chair, other.chair) && myequals("location", location, other.location) && myequals("alarmTime", alarmTime, other.alarmTime)
					&& myequals("room", room, other.room) && listEquals("startDates", startDates, other.startDates) && listEquals("endDates", endDates, other.endDates);
			// keepLocal is not checked!
			return isEqual;
		}
		return false;
	}

	public static boolean listEquals(String fieldName, ArrayList<?> list1, ArrayList<?> list2) {
		boolean res = list1.equals(list2);
		// if (res == false) {
		// Notes2GoogleExporter.println("List Field " + fieldName +
		// " differs!");
		// }
		return res;
	}

	public static boolean myequals(String fieldName, Object aO1, Object aO2) {
		if (aO1 == aO2) {
			return true;
		}
		boolean res;
		if ((aO1 == null) || (aO2 == null)) {
			// Notes2GoogleExporter.println("Field " + fieldName + " differs: "
			// + aO1 + " != " + aO2);
			return false;
		}
		res = aO1.equals(aO2);
		// if (res == false) {
		// Notes2GoogleExporter.println("Field " + fieldName + " differs: " +
		// aO1 + " != " + aO2);
		// }
		return res;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}