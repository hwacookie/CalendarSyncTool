/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.calendar;

import junit.framework.TestCase;
import de.mbaaba.notes.NotesCalendarEntry;
import de.mbaaba.util.CommConfigUtil;

public class TestFixLocationFilterTest extends TestCase {
	private static final String SIEVERSUFER_7_12359_BERLIN_DEUTSCHLAND = "Sieversufer 7, 12359 Berlin, Deutschland";


	public void setUp() {
		ConfiguratorForTests configurator = new ConfiguratorForTests();
		configurator.setProperty(FixLocationFilter.DEFAULT_LOCATION, SIEVERSUFER_7_12359_BERLIN_DEUTSCHLAND);
		CommConfigUtil.init(configurator);
		filter = new FixLocationFilter(configurator);
	}

	private FixLocationFilter filter;

	
	public void testMinimumLength() throws Exception {
		ICalendarEntry aParamCalendarEntry = new NotesCalendarEntry();
		String location = "4.23";
		String expected = SIEVERSUFER_7_12359_BERLIN_DEUTSCHLAND;
		aParamCalendarEntry.setLocation(location);
		filter.passes(aParamCalendarEntry);
		assertEquals(expected, aParamCalendarEntry.getLocation());
	}

	public void testBeautify() throws Exception {
		ICalendarEntry aParamCalendarEntry = new NotesCalendarEntry();
		String location = "Sieversufer 7, Berlin";
		String expected = SIEVERSUFER_7_12359_BERLIN_DEUTSCHLAND;
		aParamCalendarEntry.setLocation(location);
		filter.passes(aParamCalendarEntry);
		assertEquals("Beautification is broken", expected, aParamCalendarEntry.getLocation());
	}

	public void testAdditionalInfo() throws Exception {
		ICalendarEntry aParamCalendarEntry = new NotesCalendarEntry();
		String location = "SVU 4.32, Sieversufer 7, Berlin";
		String expected = SIEVERSUFER_7_12359_BERLIN_DEUTSCHLAND;
		aParamCalendarEntry.setLocation(location);
		filter.passes(aParamCalendarEntry);
		assertEquals(expected, aParamCalendarEntry.getLocation());
	}

	public void testRoom() throws Exception {
		ICalendarEntry aParamCalendarEntry = new NotesCalendarEntry();
		String location = "4.30";
		String expected = SIEVERSUFER_7_12359_BERLIN_DEUTSCHLAND;
		aParamCalendarEntry.setLocation(location);
		filter.passes(aParamCalendarEntry);
		assertEquals("Location calculation is broken", expected, aParamCalendarEntry.getLocation());
		assertEquals("Room calculation is broken","4.30", aParamCalendarEntry.getRoom());
	}
	
	public void testSVU() throws Exception {
		ICalendarEntry aParamCalendarEntry = new NotesCalendarEntry();
		String location = "R. 0.08, SVU";
		String expected = SIEVERSUFER_7_12359_BERLIN_DEUTSCHLAND;
		aParamCalendarEntry.setLocation(location);
		filter.passes(aParamCalendarEntry);
		assertEquals("Location calculation is broken", expected, aParamCalendarEntry.getLocation());
		assertEquals("Room calculation is broken","R. 0.08, SVU", aParamCalendarEntry.getRoom());
	}
	
	public void testZeuthen() throws Exception {
		ICalendarEntry aParamCalendarEntry = new NotesCalendarEntry();
		String location = "Seehotel Zeuthen, Fontaneallee 27/28, 15738 Zeuthen";
		String expected = "Fontaneallee 27, 15738 Zeuthen, Deutschland";
		aParamCalendarEntry.setLocation(location);
		filter.passes(aParamCalendarEntry);
		assertEquals("Location calculation is broken", expected, aParamCalendarEntry.getLocation());
	}
	
	
}
