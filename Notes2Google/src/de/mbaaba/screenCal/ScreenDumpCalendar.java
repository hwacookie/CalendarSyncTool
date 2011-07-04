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

public class ScreenDumpCalendar extends AbstractCalendar {
	public void init(Configurator aConfigurator) throws Exception {
	}

	public void close() {
	}

	public ArrayList<ICalendarEntry> readCalendarEntries(Date aStartDate, Date aEndDate) {
		return new ArrayList<ICalendarEntry>();
	}

	public void put(ICalendarEntry aCalendarEntry) {
		String string = aCalendarEntry.toString();
		System.out.println(string);
	}

	@Override
	public void delete(ICalendarEntry aParamCalendarEntry) {
	}


	@Override
	public void deleteList(List<ICalendarEntry> aList) {
	}
}