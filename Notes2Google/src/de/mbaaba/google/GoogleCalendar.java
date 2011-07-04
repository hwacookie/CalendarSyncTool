/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.google;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.ILink;
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.batch.BatchOperationType;
import com.google.gdata.data.batch.BatchStatus;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.calendar.EventWho;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.ExtendedProperty;
import com.google.gdata.data.extensions.FamilyName;
import com.google.gdata.data.extensions.FullName;
import com.google.gdata.data.extensions.GivenName;
import com.google.gdata.data.extensions.PhoneNumber;
import com.google.gdata.data.extensions.PhoneNumber.Rel;
import com.google.gdata.data.extensions.Recurrence;
import com.google.gdata.data.extensions.Where;
import com.google.gdata.data.extensions.Who;
import com.google.gdata.util.ServiceException;

import de.mbaaba.calendar.AbstractCalendar;
import de.mbaaba.calendar.CalendarSyncTool;
import de.mbaaba.calendar.ICalendarEntry;
import de.mbaaba.calendar.Person;
import de.mbaaba.util.Configurator;

public class GoogleCalendar extends AbstractCalendar {
	private static final String NEWLINE = "\r\n";
	private static final String NOTES_ID = "notes-id";
	private CalendarService calendarService;
	private URL feedUrl;

	private CalendarEventEntry getByNotesID(String aNotesId) throws ServiceException, IOException {
		CalendarQuery myQuery = new CalendarQuery(feedUrl);
		myQuery.setExtendedPropertyQuery(new CalendarQuery.ExtendedPropertyMatch[] { new CalendarQuery.ExtendedPropertyMatch(NOTES_ID, aNotesId) });

		CalendarEventFeed resultFeed = (CalendarEventFeed) this.calendarService.getFeed(myQuery, CalendarEventFeed.class);
		if (resultFeed.getEntries().size() > 0) {
			if (resultFeed.getEntries().size() > 1) {
				CalendarSyncTool.printerr("Oops: More than one result for NOTES-ID=" + aNotesId + ", using first entry !");
			}
			CalendarEventEntry entry = (CalendarEventEntry) resultFeed.getEntries().get(0);
			return entry;
		}
		return null;
	}

	private ArrayList<ICalendarEntry> dateRangeQuery(DateTime startTime, DateTime endTime) throws ServiceException, IOException {
		CalendarQuery myQuery = new CalendarQuery(feedUrl);
		myQuery.setMinimumStartTime(startTime);
		myQuery.setMaximumStartTime(endTime);
		CalendarEventFeed resultFeed = (CalendarEventFeed) this.calendarService.query(myQuery, CalendarEventFeed.class);

		ArrayList<ICalendarEntry> res = new ArrayList<ICalendarEntry>();
		for (int i = 0; i < resultFeed.getEntries().size(); i++) {
			CalendarEventEntry entry = (CalendarEventEntry) resultFeed.getEntries().get(i);
			GoogleCalendarEntry googleCalendarEntry = new GoogleCalendarEntry(entry);
			res.add(googleCalendarEntry);
		}
		return res;
	}

	private void createEvent(ICalendarEntry aCalendarEntry) throws ServiceException, IOException {
		if (aCalendarEntry.getStartDates() == null) {
			CalendarSyncTool.printerr("Ooops: no start date set: " + aCalendarEntry.getUniqueID());
			return;
		}
		if (aCalendarEntry.getEndDates() == null) {
			CalendarSyncTool.printerr("Ooops: no end date set: " + aCalendarEntry.getUniqueID());
			return;
		}

		CalendarEventEntry googleCalendarEventEntry = createGoogleEvent(aCalendarEntry);

		addExtendedProperty(googleCalendarEventEntry, NOTES_ID, aCalendarEntry.getUniqueID());

		this.calendarService.insert(feedUrl, googleCalendarEventEntry);
	}

	private CalendarEventEntry createGoogleEvent(ICalendarEntry aCalendarEntry) {
		CalendarEventEntry googleCalendarEventEntry = new CalendarEventEntry();

		copyFromInternalToGoogleStyle(aCalendarEntry, googleCalendarEventEntry);
		return googleCalendarEventEntry;
	}

	private void copyFromInternalToGoogleStyle(ICalendarEntry aCalendarEntry, CalendarEventEntry aGoogleCalendarEventEntry) {
		aGoogleCalendarEventEntry.setId(aCalendarEntry.getUniqueID());
		aGoogleCalendarEventEntry.setTitle(new PlainTextConstruct(aCalendarEntry.getSubject()));
		aGoogleCalendarEventEntry.setContent(new PlainTextConstruct(aCalendarEntry.getBody()));
		aGoogleCalendarEventEntry.setQuickAdd(false);
		aGoogleCalendarEventEntry.setWebContent(null);

		if (aCalendarEntry.getLocation() != null) {
			Where where = new Where(Where.Rel.EVENT, "Address", aCalendarEntry.getLocation());
			aGoogleCalendarEventEntry.addLocation(where);
		}
		if (aCalendarEntry.getRoom() != null) {
			Where where = new Where(Where.Rel.EVENT_ALTERNATE, "Room", aCalendarEntry.getRoom());
			aGoogleCalendarEventEntry.addLocation(where);
		}

		aGoogleCalendarEventEntry.getTimes().clear();

		int numDates = aCalendarEntry.getStartDates().size();

		String pattern = "yyyyMMdd'T'HHmmss";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT-0:00"));

		Recurrence rr = new Recurrence();
		String rrS = "";
		for (int ctr = 0; ctr < numDates; ctr++) {
			Date start = aCalendarEntry.getStartDates().get(ctr);
			Date end = aCalendarEntry.getEndDates().get(ctr);

			if (ctr == 0) {
				rrS = "DTSTART;TZID=\"W. Europe\":" + sdf.format(start) + NEWLINE + "DTEND;TZID=\"W. Europe\":" + sdf.format(end) + NEWLINE + "TRANSP:OPAQUE" + NEWLINE + "RDATE;VALUE=PERIOD:";
			}

			rrS = rrS + sdf.format(start) + "Z/" + sdf.format(end) + "Z,";
		}
		rrS = rrS.substring(0, rrS.length() - 1);
		if (aCalendarEntry.getLastModified() != null) {
			rrS = rrS + NEWLINE + "DTSTAMP:" + sdf.format(aCalendarEntry.getLastModified());
		}

		rr.setValue(rrS);
		aGoogleCalendarEventEntry.setRecurrence(rr);

		List<Person> attendees = aCalendarEntry.getAttendees();
		for (Person person : attendees) {
			EventWho participant = createParticipant(person, Who.Rel.EVENT_ATTENDEE);
			aGoogleCalendarEventEntry.addParticipant(participant);
		}

		EventWho organizedBy = createParticipant(aCalendarEntry.getChair(), Who.Rel.EVENT_ORGANIZER);
		aGoogleCalendarEventEntry.addParticipant(organizedBy);

	}

	private EventWho createParticipant(Person person, String aRelation) {
		EventWho participant = new EventWho();

		if ((person.getFirstName() != null) && (person.getLastName() != null)) {
			FullName fullName = new FullName();
			fullName.setValue(person.getFirstName() + " " + person.getLastName());
			participant.setExtension(fullName);
		}

		if (person.getPhoneJob() != null) {
			PhoneNumber phoneNumberExtension = new PhoneNumber();
			phoneNumberExtension.setPhoneNumber(person.getPhoneJob());
			phoneNumberExtension.setRel(Rel.COMPANY_MAIN);
			participant.addRepeatingExtension(phoneNumberExtension);
		}

		if (person.getPhoneMobile() != null) {
			PhoneNumber mobileNumberExtension = new PhoneNumber();
			mobileNumberExtension.setPhoneNumber(person.getPhoneMobile());
			mobileNumberExtension.setRel(Rel.MOBILE);
			participant.addRepeatingExtension(mobileNumberExtension);
		}

		if (person.getINetAdress() != null) {
			Email emailExtension = new Email();
			emailExtension.setAddress(person.getINetAdress());
			participant.setExtension(emailExtension);
		}

		if (person.getFirstName() != null) {
			GivenName givenName = new GivenName(person.getFirstName(), "");
			participant.setExtension(givenName);
		}

		if (person.getLastName() != null) {
			FamilyName familyName = new FamilyName(person.getLastName(), "");
			participant.setExtension(familyName);
		}

		if (aRelation != null) {
			participant.setRel(aRelation);
		}

		if (person.getPrettyMailAdress() != null) {
			participant.setEmail(person.getPrettyMailAdress());
		}

		return participant;
	}

	private static void addExtendedProperty(CalendarEventEntry entry, String name, String value) throws ServiceException, IOException {
		ExtendedProperty property = new ExtendedProperty();
		property.setName(name);
		property.setValue(value);

		entry.addExtension(property);
	}

	@Override
	public void deleteList(List<ICalendarEntry> entriesToDelete) {
		ArrayList<CalendarEventEntry> eventsToDelete = new ArrayList<CalendarEventEntry>();
		for (ICalendarEntry calendarEntry : entriesToDelete) {
			try {
				CalendarEventEntry eventByNotesID = getByNotesID(calendarEntry.getUniqueID());
				if (eventByNotesID != null) {
					eventsToDelete.add(eventByNotesID);
				}
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (eventsToDelete.size() > 0) {
			deleteIntern(eventsToDelete);
		}
	}

	private void deleteIntern(List<CalendarEventEntry> eventsToDelete) {
		CalendarEventFeed batchRequest = new CalendarEventFeed();
		for (CalendarEventEntry toDelete : eventsToDelete) {
			if (toDelete == null) {
				throw new RuntimeException("Null Entry????");
			}
			if (toDelete.getId() != null) {
				BatchUtils.setBatchId(toDelete, String.valueOf("DEL_" + toDelete.getId()));
				BatchUtils.setBatchOperationType(toDelete, BatchOperationType.DELETE);
				batchRequest.getEntries().add(toDelete);
			} else {
				CalendarSyncTool.printerr("Ooops! Entry without ID cannot be deleted.");
			}
		}

		boolean isSuccess = false;
		try {
			CalendarEventFeed feed = (CalendarEventFeed) this.calendarService.getFeed(feedUrl, CalendarEventFeed.class);
			Link batchLink = feed.getLink("http://schemas.google.com/g/2005#batch", ILink.Type.ATOM);
			URL batchUrl = new URL(batchLink.getHref());

			CalendarEventFeed batchResponse = (CalendarEventFeed) this.calendarService.batch(batchUrl, batchRequest);

			isSuccess = true;
			for (CalendarEventEntry entry : batchResponse.getEntries()) {
				String batchId = BatchUtils.getBatchId(entry);
				if (!BatchUtils.isSuccess(entry)) {
					isSuccess = false;
					BatchStatus status = BatchUtils.getBatchStatus(entry);
					CalendarSyncTool.println("Delete of " + batchId + " failed: " + status.getReason());
				} else {
					CalendarSyncTool.println("Deleted " + batchId + ".");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		if (isSuccess) {
			CalendarSyncTool.println("Successfully deleted all events via batch request.");
		}
	}

	public void init(Configurator aConfigurator) throws Exception {
		String username = aConfigurator.getProperty("google.user", "");
		String password = aConfigurator.getProperty("google.pwd", "");
		feedUrl = new URL(aConfigurator.getProperty("google.url", ""));

		CalendarSyncTool.println("Using URL " + feedUrl);
		this.calendarService = new CalendarService("exampleCo-exampleApp-1");
		this.calendarService.setUserCredentials(username, password);
		this.calendarService.useSsl();
	}

	public ArrayList<ICalendarEntry> readCalendarEntries(Date aStartDate, Date aEndDate) {
		try {
			return dateRangeQuery(new DateTime(aStartDate), new DateTime(aEndDate));
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void put(ICalendarEntry aCalendarEntry) {
		try {
			if (aCalendarEntry.getUniqueID() == null) {
				CalendarSyncTool.printerr("Entry has no unique ID!");
				return;
			}
			CalendarEventEntry googleEvent = getByNotesID(aCalendarEntry.getUniqueID());
			if (googleEvent != null) {
				CalendarSyncTool.println("Updating entry " + aCalendarEntry.getShortString() + ".");
				copyFromInternalToGoogleStyle(aCalendarEntry, googleEvent);
				googleEvent.delete();
				createEvent(aCalendarEntry);
			} else {
				CalendarSyncTool.println("Adding entry " + aCalendarEntry.getShortString() + ".");
				createEvent(aCalendarEntry);
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot access Google Calendar.", e);
		}
	}

	public void close() {
	}

	// public void deleteAllEntries() {
	// try {
	// CalendarEventFeed resultFeed = (CalendarEventFeed)
	// this.calendarService.getFeed(feedUrl, CalendarEventFeed.class);
	// List<CalendarEventEntry> entries = resultFeed.getEntries();
	// delete(entries);
	// } catch (IOException e) {
	// e.printStackTrace();
	// } catch (ServiceException e) {
	// e.printStackTrace();
	// }
	// }

	@Override
	public void delete(ICalendarEntry aParamCalendarEntry) {
		List<ICalendarEntry> list = new ArrayList<ICalendarEntry>();
		list.add(aParamCalendarEntry);
		deleteList(list);
	}

	public void deleteAllEntries() {
		try {
			CalendarEventFeed resultFeed = (CalendarEventFeed) this.calendarService.getFeed(feedUrl, CalendarEventFeed.class);
			List<CalendarEventEntry> entries = resultFeed.getEntries();
			deleteIntern(entries);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

}

// DateTime startTime = new
// DateTime(aCalendarEntry.getStartDates().get(ctr),
// TimeZone.getTimeZone("GMT-1:00"));
// DateTime endTime = new
// DateTime(aCalendarEntry.getEndDates().get(ctr),
// TimeZone.getTimeZone("GMT-1:00"));
// When eventTime = new When();
// eventTime.setStartTime(startTime);
// eventTime.setEndTime(endTime);
// aGoogleCalendarEventEntry.addTime(eventTime);
