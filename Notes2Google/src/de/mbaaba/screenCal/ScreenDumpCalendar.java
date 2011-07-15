/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.screenCal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.mbaaba.calendar.AbstractCalendar;
import de.mbaaba.calendar.ICalendarEntry;
import de.mbaaba.util.Configurator;

/**
 * The Class ScreenDumpCalendar just prints out all its entries on the screen. 
 * 
 * Makes sense for debugging, only.
 */
public class ScreenDumpCalendar extends AbstractCalendar {
	@Override
	public void init(Configurator aConfigurator) throws Exception {
	}

	@Override
	public void close() {
	}

	@Override
	public ArrayList<ICalendarEntry> readCalendarEntries(Date aStartDate, Date aEndDate) {
		return new ArrayList<ICalendarEntry>();
	}

	@Override
	public void put(ICalendarEntry aCalendarEntry) {
		final String string = aCalendarEntry.toString();
		System.out.println(string);
	}

	@Override
	public void delete(ICalendarEntry aParamCalendarEntry) {
	}

	@Override
	public void deleteList(List<ICalendarEntry> aList) {
	}
}