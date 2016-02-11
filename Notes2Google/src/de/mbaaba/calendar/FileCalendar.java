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
import de.mbaaba.util.OutputManager;

/**
 * This is a calendar that uses {@link XStream} to store its content.
 */
public class FileCalendar extends AbstractCalendar {

	/** Used for logging purposes. */
	private static final Logger LOG = new Logger(FileCalendar.class);

	/**
	 * The key within the property file that is used to store the filename of
	 * the calendar.
	 */
	private static final String FILECAL_FILENAME_PROPERTY_TAG = "fileCalendar.FileName";

	/** A list that caches all entries within the calendar. */
	private ArrayList<CalendarEntry> allEntries;

	/** The name of the file that is used to store the calendar data. */
	private String fileName;

	/**
	 * Instantiates a new file calendar.
	 */
	public FileCalendar() {
		allEntries = new ArrayList<CalendarEntry>();
	}

	/**
	 * Closes this calendar by saving its content to the file.
	 * 
	 * @see de.mbaaba.calendar.AbstractCalendar#close()
	 */
	@Override
	public void close() {
		// save to file
		final XStream xStream = new XStream();
		try {
			xStream.toXML(allEntries, new FileOutputStream(fileName));
		} catch (final FileNotFoundException e) {
			LOG.error("Warning: " + fileName + " could not be written!", e);
			OutputManager.printerr("Warning: " + fileName + " could not be written!");
		}
	}

	/**
	 * Initializes this calendar and reads all entries from the file given in
	 * the properties.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void init(Configurator aConfigurator) {
		fileName = aConfigurator.getProperty(FILECAL_FILENAME_PROPERTY_TAG, "fileCal.xml");
		final XStream xStream = new XStream();
		try {
			allEntries = (ArrayList<CalendarEntry>) xStream.fromXML(new FileInputStream(fileName));
		} catch (final Throwable e) {
			LOG.error("Warning: " + fileName + " could not be opened for reading, using empty list!", e);
			OutputManager.printerr("Warning: " + fileName + " could not be opened for reading, using empty list!");
			
			allEntries = new ArrayList<CalendarEntry>();
		}
	}

	/*
	 * Returns a list of all entries between aStart and aEnd.
	 * 
	 * @see
	 * de.mbaaba.calendar.AbstractCalendar#readCalendarEntries(java.util.Date,
	 * java.util.Date)
	 */
	@Override
	public ArrayList<ICalendarEntry> readCalendarEntries(Date aStartDate, Date aEndDate) {
		final ArrayList<ICalendarEntry> res = new ArrayList<ICalendarEntry>();

		return res;
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

	/**
	 * 
	 * 
	 * @see de.mbaaba.calendar.AbstractCalendar#put(de.mbaaba.calendar.ICalendarEntry
	 * )
	 */
	@Override
	public void put(ICalendarEntry aCalendarEntry) {
		for (final CalendarEntry calendarEntry : allEntries) {
			if (calendarEntry.getUniqueID().equals(aCalendarEntry.getUniqueID())) {
				calendarEntry.copyFrom(aCalendarEntry);
				return;
			}
		}
		allEntries.add(new CalendarEntry(aCalendarEntry));
	}

	@Override
	public void delete(ICalendarEntry aCalendarEntry) {
		for (final CalendarEntry calendarEntry : allEntries) {
			if (calendarEntry.getUniqueID().equals(aCalendarEntry.getUniqueID())) {
				allEntries.remove(calendarEntry);
				return;
			}
		}
	}

	/**
	 * Delete all.
	 */
	public void deleteAll() {
		allEntries.clear();
	}

}
