/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.googleNew;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.mbaaba.calendar.AbstractCalendar;
import de.mbaaba.calendar.ICalendarEntry;
import de.mbaaba.util.Configurator;

/**
 * The Class GoogleCalendar allows to access events within a calendar hosted at google.
 */
public class GoogleCalendar extends AbstractCalendar {
	private static final String NOTES_ID = "notes-id";


	@Override
	public void deleteList(List<ICalendarEntry> aEntriesToDelete) {
	}

	@Override
	public void init(Configurator aConfigurator) throws Exception {
		final String username = aConfigurator.getProperty("google.user", "");
		final String password = aConfigurator.getProperty("google.pwd", "");
		
	}

	@Override
	public ArrayList<ICalendarEntry> readCalendarEntries(Date aStartDate, Date aEndDate) {
		return null;
	}
	
	/**
	 * Adds all given calendar entries.
	 * 
	 * @param aCalendarEntriesthe calendar entries to be added or updated.
	 */
	@Override
	public void putList(List<ICalendarEntry> aCalendarEntries) {

	}

	@Override
	public void put(ICalendarEntry aCalendarEntry) {
	}

	@Override
	public void close() {
	}

	@Override
	public void delete(ICalendarEntry aParamCalendarEntry) {
		final List<ICalendarEntry> list = new ArrayList<ICalendarEntry>();
		list.add(aParamCalendarEntry);
		deleteList(list);
	}

	public void deleteAllEntries() {
		// TODO Auto-generated method stub
		
	}

}

