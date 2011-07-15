/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.iCal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.Calendars;
import de.mbaaba.calendar.AbstractCalendar;
import de.mbaaba.calendar.CalendarEntry;
import de.mbaaba.calendar.ICalendarEntry;
import de.mbaaba.calendar.OutputManager;
import de.mbaaba.calendar.Person;
import de.mbaaba.util.Configurator;

/**
 * The Class ICalCalendar allows to access events within a iCal calendar.
 * Currently, the calendar is does not allow to delete entries in the calendar.
 */
public class ICalCalendar extends AbstractCalendar {

	private static final String ICAL_FILENAME = "ical.filename";

	private String icsFilename;

	private net.fortuna.ical4j.model.Calendar fortunaCalendar;

	private boolean problem;

	@Override
	public void init(Configurator aConfigurator) {
		icsFilename = aConfigurator.getProperty(ICAL_FILENAME, "calendar.ics");
		if (icsFilename.startsWith("\"")) {
			icsFilename = icsFilename.substring(1);
		}
		if (icsFilename.endsWith("\"")) {
			icsFilename = icsFilename.substring(0, icsFilename.length() - 1);
		}
		fortunaCalendar = new net.fortuna.ical4j.model.Calendar();
		fortunaCalendar.getProperties().add(Version.VERSION_2_0);
		fortunaCalendar.getProperties().add(new ProdId("-//mbaaba.de//Notes2Google 1.0//EN"));
		try {
			final File file = new File(icsFilename);
			if (file.exists()) {
				loadCalendar();
			}
		} catch (final IOException e) {
			problem = true;
			e.printStackTrace();
		} catch (final ParserException e) {
			problem = true;
			e.printStackTrace();
		}
	}

	@Override
	public ArrayList<ICalendarEntry> readCalendarEntries(Date aStartDate, Date aEndDate) {
		final ArrayList<ICalendarEntry> res = new ArrayList<ICalendarEntry>();
		final ComponentList components = fortunaCalendar.getComponents();
		for (int i = 0; i < components.size(); i++) {
			final Object object = components.get(i);
			if (object instanceof VEvent) {
				final VEvent vEvent = (VEvent) object;
				final CalendarEntry newEntry = new ICalCalendarEntry(vEvent);
				res.add(newEntry);
			}
		}
		return res;
	}

	public VEvent convertToVEvent(ICalendarEntry aCalendarEntry) {
		final VEvent vEvent = new VEvent();
		if (aCalendarEntry.getStartDates() != null) {
			for (final Date date : aCalendarEntry.getStartDates()) {
				vEvent.getProperties().add(new DtStart(new DateTime(date)));
			}
		}

		if (aCalendarEntry.getEndDates() != null) {
			for (final Date date : aCalendarEntry.getEndDates()) {
				vEvent.getProperties().add(new DtEnd(new DateTime(date)));
			}
		}

		if (aCalendarEntry.getAlarmTime() != null) {
			vEvent.getProperties().add(new VAlarm(new DateTime(aCalendarEntry.getAlarmTime())));

		}
		if (aCalendarEntry.getLastModified() != null) {
			final net.fortuna.ical4j.model.DateTime endDate = new net.fortuna.ical4j.model.DateTime(aCalendarEntry.getLastModified());
			vEvent.getProperties().add(new LastModified(endDate));
		}
		vEvent.getProperties().add(new Summary(aCalendarEntry.getSubject()));
		vEvent.getProperties().add(new Uid(aCalendarEntry.getUniqueID()));
		// vEvent.getProperties().add(new
		// ProdId("-//mbaaba.de//Notes2Google 1.0//EN"));
		String description = aCalendarEntry.getBody();
		if (description == null) {
			description = "";
		}
		if (description.length() > 0) {
			description = description + "\n" + "----------------------\n";
		}
		description = description + aCalendarEntry.getChair().getShortContactInfo() + "\n";
		final List<Person> attendees2 = aCalendarEntry.getAttendees();
		if (attendees2.size() > 0) {
			for (final Person person : attendees2) {
				description = description + person.getShortContactInfo() + "\n";
			}
		}
		vEvent.getProperties().add(new Description(description));

		String location = "";
		if (aCalendarEntry.getLocation() != null) {
			location = aCalendarEntry.getLocation();
		}
		if ((aCalendarEntry.getRoom() != null) && (aCalendarEntry.getRoom().length() > 0)) {
			if (location.length() > 0) {
				location = location + " [" + aCalendarEntry.getRoom() + "]";
			} else {
				location = aCalendarEntry.getRoom();
			}
		}

		vEvent.getProperties().add(new Location(location));
		final Property organizer = convertToProperty(aCalendarEntry.getChair(), Role.CHAIR);
		if (organizer != null) {
			vEvent.getProperties().add(organizer);
		}
		for (final Person person : aCalendarEntry.getAttendees()) {
			final Property attendee = convertToProperty(person, Role.REQ_PARTICIPANT);
			if (attendee != null) {
				vEvent.getProperties().add(attendee);
			}
		}
		return vEvent;
	}

	@Override
	public void put(ICalendarEntry aCalendarEntry) {
		final VEvent aEvent = convertToVEvent(aCalendarEntry);
		final ComponentList components = fortunaCalendar.getComponents(Component.VEVENT);
		for (final Object object : components) {
			if (object instanceof VEvent) {
				final VEvent storedEvent = (VEvent) object;
				if (storedEvent.getUid().equals(aEvent.getUid())) {
					OutputManager.println("replacing existing entry " + storedEvent.getUid());
					fortunaCalendar.getComponents().remove(storedEvent);
					fortunaCalendar.getComponents().add(aEvent);
					return;
				}
			}
		}
		fortunaCalendar.getComponents().add(aEvent);
	}

	public Property convertToProperty(Person aPerson, Role aRole) {
		Property res = null;
		try {
			if (aRole == Role.CHAIR) {
				res = new Organizer(aPerson.getURI());
			} else {
				res = new Attendee(aPerson.getURI());
			}
		} catch (final URISyntaxException e) {
			return null;
		}

		final Cn cn = new Cn(aPerson.getFirstName() + " " + aPerson.getLastName());
		res.getParameters().add(cn);
		final String roleText = aRole.getValue();
		final Role roleParam = new Role(roleText);
		res.getParameters().add(roleParam);
		return res;
	}

	private void loadCalendar() throws IOException, ParserException {
		fortunaCalendar = Calendars.load(icsFilename);
	}

	@Override
	public void close() {
		if (problem) {
			OutputManager.printerr("Had a problem while loading \"" + icsFilename + "\", will not overwrite current file!");
			return;
		}
		try {
			final File f = new File(icsFilename);
			if (!f.exists()) {
				try {
					f.createNewFile();
				} catch (final IOException e) {
					OutputManager.printerr("Cannot create new calendar file \"" + icsFilename + "\"");
				}
			}
			if (f.canWrite()) {
				final FileOutputStream fout = new FileOutputStream(icsFilename);
				final CalendarOutputter outputter = new CalendarOutputter();
				outputter.output(fortunaCalendar, fout);
				fout.close();
				OutputManager.println("Wrote calendar to " + icsFilename);
			} else {
				OutputManager.printerr("Cannot write to " + icsFilename);
			}
		} catch (final FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void delete(ICalendarEntry aParamCalendarEntry) {
		// TODO: iCal: Add ability to delete entries
		// http://github.com/hwacookie/CalendarSyncTool/issues/issue/2
		// fortunaCalendar.getComponents().remove(component)(aEvent);
	}

	@Override
	public void deleteList(List<ICalendarEntry> aList) {
		// TODO: iCal: Add ability to delete entries
		// http://github.com/hwacookie/CalendarSyncTool/issues/issue/2
	}

}
