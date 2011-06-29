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

import de.mbaaba.util.Configurator;

/**
 * Abstract base class for all calendars.
 * @author walden_h1
 *
 */
public abstract class AbstractCalendar {
	public abstract void init(Configurator paramConfigurator) throws Exception;

	public abstract void close();

	public abstract ArrayList<CalendarEntry> readCalendarEntries(Date paramDate1, Date paramDate2);

	public void putAll(List<CalendarEntry> aCalendarEntries) {
		for (ICalendarEntry calendarEntry : aCalendarEntries)
			put(calendarEntry);
	}

	public abstract void put(ICalendarEntry paramCalendarEntry);

	public abstract void put(List<CalendarEntry> paramCalendarEntry);

	public abstract void delete(CalendarEntry paramCalendarEntry);

	public abstract void delete(List<CalendarEntry> aList);

}