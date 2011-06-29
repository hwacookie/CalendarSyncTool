/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.calendar;

import java.util.List;

public abstract class ReadOnlyCalendar extends AbstractCalendar {

	@Override
	public void put(ICalendarEntry aParamCalendarEntry) {
	}

	@Override
	public void put(List<CalendarEntry> aParamCalendarEntry) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(CalendarEntry aParamCalendarEntry) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(List<CalendarEntry> aList) {
		// TODO Auto-generated method stub

	}

}
