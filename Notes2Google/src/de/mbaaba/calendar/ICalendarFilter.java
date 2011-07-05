/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.calendar;

/**
 * The Interface ICalendarFilter describes the functionality that all filter classes must implement.
 */
public abstract interface ICalendarFilter {
	public abstract boolean passes(ICalendarEntry aParamCalendarEntry) throws Exception;
}