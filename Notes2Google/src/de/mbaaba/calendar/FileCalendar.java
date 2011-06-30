/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.calendar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import de.mbaaba.util.Configurator;
import de.mbaaba.util.Logger;

public class FileCalendar extends AbstractCalendar {
	
	private static final Logger LOG = new Logger(FileCalendar.class);


	private static final String FILECAL_FILENAME = "fileCalendar.FileName";
	ArrayList<CalendarEntry> allEntries;
	private String fileName;

	public FileCalendar() {
		allEntries = new ArrayList<CalendarEntry>();
	}

	@Override
	public void close() {
		// save to file
		XStream xStream = new XStream();
		try {
			xStream.toXML(allEntries, new FileOutputStream(fileName));
		} catch (FileNotFoundException e) {
			LOG.error("Warning: " + fileName + " could not be written!", e);
			CalendarSyncTool.printerr("Warning: " + fileName + " could not be written!");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(Configurator aConfigurator) {
		fileName = aConfigurator.getProperty(FILECAL_FILENAME, "fileCal.xml");
		XStream xStream = new XStream();
		try {
			allEntries = (ArrayList<CalendarEntry>) xStream.fromXML(new FileInputStream(fileName));
		} catch (FileNotFoundException e) {
			LOG.error("Warning: " + fileName + " could not be opened for reading, using empty list!", e);
			CalendarSyncTool.printerr("Warning: " + fileName + " could not be opened for reading, using empty list!");
			allEntries = new ArrayList<CalendarEntry>();
		}
	}

	@Override
	public ArrayList<CalendarEntry> readCalendarEntries(Date aStartDate, Date aEndDate) {
		return allEntries;
	}

	@Override
	public void put(ICalendarEntry aCalendarEntry) {
		for (CalendarEntry calendarEntry : allEntries) {
			if (calendarEntry.getUniqueID().equals(aCalendarEntry.getUniqueID())) {
				calendarEntry.copyFrom(aCalendarEntry);
				return;
			}
		}
		allEntries.add(new CalendarEntry(aCalendarEntry));
	}

	@Override
	public void delete(CalendarEntry aParamCalendarEntry) {
		allEntries.remove(aParamCalendarEntry);
	}

	@Override
	public void putList(List<CalendarEntry> aParamCalendarEntry) {
		for (ICalendarEntry calendarEntry : aParamCalendarEntry) {
			put(calendarEntry);
		}
	}

	@Override
	public void deleteList(List<CalendarEntry> aList) {
		throw new RuntimeException("Not supported");
	}

	public void deleteAll() {
		allEntries.clear();
	}

}
