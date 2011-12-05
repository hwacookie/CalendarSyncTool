/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.notify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.mbaaba.calendar.AbstractCalendar;
import de.mbaaba.calendar.ICalendarEntry;
import de.mbaaba.util.Configurator;

/**
 * The Class NotifyCalendar implements a calendar that is used to notify the user via a push message is anything in his calendar has changed.
 */
public class NotifyCalendar extends AbstractCalendar {

	@Override
	public void init(Configurator aConfigurator) throws Exception {
//		final String username = aConfigurator.getProperty("google.user", "");
//		final String password = aConfigurator.getProperty("google.pwd", "");
//		feedUrl = new URL(aConfigurator.getProperty("google.url", ""));
//		timezoneStr = aConfigurator.getProperty("google.timezone", "CET");
//
//		OutputManager.println("Using URL " + feedUrl);
//		this.calendarService = new CalendarService("exampleCo-exampleApp-1");
//		this.calendarService.setUserCredentials(username, password);
//		this.calendarService.useSsl();
	}

	@Override
	public void close() {
	}

	@Override
	public ArrayList<ICalendarEntry> readCalendarEntries(Date aStartDate, Date aEndDate) {
		return new ArrayList<ICalendarEntry>();
	}

	/**
	 * Adds all given calendar entries.
	 * 
	 * @see #put(ICalendarEntry)
	 * @param aCalendarEntries
	 *            the calendar entries to be added.
	 */
	@Override
	public final void putList(List<ICalendarEntry> aCalendarEntries) {
		for (final ICalendarEntry calendarEntry : aCalendarEntries) {
			put(calendarEntry);
		}
	}
	
	@Override
	public void put(ICalendarEntry aCalendarEntry) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(ICalendarEntry aCalendarEntry) {
		// TODO Auto-generated method stub

	}
}
