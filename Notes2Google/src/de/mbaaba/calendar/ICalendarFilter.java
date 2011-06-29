/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.calendar;

public abstract interface ICalendarFilter
{
  public abstract boolean passes(ICalendarEntry paramCalendarEntry) throws Exception;
}