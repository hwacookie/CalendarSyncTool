/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.calendar;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import de.mbaaba.util.CommConfigUtil;
import de.mbaaba.util.Logger;
import de.mbaaba.util.PropertyFileConfigurator;
import de.mbaaba.util.Units;

/**
 * The Class Notes2GoogleExporter.
 */
public class CalendarSyncTool {

	/**
	 * Encapsulates constants for configuration parameters and their default values.
	 */
	class ConfigParameter {
		private static final String CALENDAR_FROM = "calendar.from";

		private static final String DEFAULT_CALENDAR_FROM = "de.mbaaba.notes.NotesCalendar";

		private static final String CALENDAR_NUM_DAYS_FUTURE = "calendar.numDaysFuture";

		private static final int DEFAULT_CALENDAR_NUM_DAYS_FUTURE = 14;

		private static final String CALENDAR_NUM_DAYS_PAST = "calendar.numDaysPast";

		private static final int DEFAULT_CALENDAR_NUM_DAYS_PAST = 0;

		private static final String CALENDAR_TO = "calendar.to";

		/** Default target calendar type. */
		private static final String DEFAULT_WRITETO_CLASSNAME = "de.mbaaba.google.GoogleCalendar";

		private static final String FILTERS_SCRIPTS = "filters.scripts";

		private static final String DEFAULT_FILTERS_SCRIPTS = "";

	}

	/**
	 * A logger for this class.
	 */
	private static final Logger LOG = new Logger(CalendarSyncTool.class);

	/**
	 * A constant that is used as sleep time between two invocations of the sync-loop in case that an error occurs.
	 */
	private static final long TEN_SECONDS = Units.SECOND * 10;

	/** Used to read configuration parameter. */
	private PropertyFileConfigurator configurator;

	/**
	 * Stores the last known calendar entries.
	 */
	private FileCalendar lastKnownCalendarState;

	/**
	 * Instance of a helper class that handles the sleep between two runs of the sync loop.
	 */
	private SleepUtililty sleepUtililty;

	/**
	 * Instantiates a calendar converter.
	 */
	public CalendarSyncTool() {
		configurator = new PropertyFileConfigurator("Notes2Google.properties");
		lastKnownCalendarState = new FileCalendar();
		lastKnownCalendarState.init(configurator);
		sleepUtililty = new SleepUtililty(configurator);

	}

	/**
	 * This will perform the read/filter/write loop.
	 */
	private void syncLoop() {

		// initially, try to read all calendar entries that we know about from the last run. 
		ArrayList<ICalendarEntry> oldSourceEntries = lastKnownCalendarState.readCalendarEntries(null, null);

		while (true) {

			try {

				configurator = new PropertyFileConfigurator("Notes2Google.properties");
				CommConfigUtil.init(configurator);

				String readFromClassName = configurator.getProperty(ConfigParameter.CALENDAR_FROM,
						ConfigParameter.DEFAULT_CALENDAR_FROM);

				OutputManager.println("Reading from calendar " + readFromClassName + " ... ");

				AbstractCalendar sourceCalendar = (AbstractCalendar) Class.forName(readFromClassName).newInstance();
				sourceCalendar.init(configurator);

				long numDaysPast = configurator.getProperty(ConfigParameter.CALENDAR_NUM_DAYS_PAST,
						ConfigParameter.DEFAULT_CALENDAR_NUM_DAYS_PAST);
				long numDaysFuture = configurator.getProperty(ConfigParameter.CALENDAR_NUM_DAYS_FUTURE,
						ConfigParameter.DEFAULT_CALENDAR_NUM_DAYS_FUTURE);

				Date startDate = new Date(System.currentTimeMillis() - Units.DAY * numDaysPast);
				Date endDate = new Date(System.currentTimeMillis() + Units.DAY * numDaysFuture);

				ArrayList<ICalendarEntry> sourceEntriesUnfiltered = null;
				try {
					sourceEntriesUnfiltered = sourceCalendar.readCalendarEntries(startDate, endDate);
				} catch (Exception e) {
					LOG.warn("Error while reading entries from source calendar!", e);
				}

				// if we did not get a list of entries, something must have gone
				// wrong. In that case, do not write anything to the target
				// calendar
				if (sourceEntriesUnfiltered != null) {
					ArrayList<ICalendarEntry> sourceEntries = runFilters(sourceEntriesUnfiltered);

					ArrayList<ICalendarEntry> newEntries = getNewEntries(sourceEntries, oldSourceEntries);
					ArrayList<ICalendarEntry> obsoleteEntries = getObsoleteEntries(sourceEntries, oldSourceEntries);

					if ((newEntries.size() > 0) || (obsoleteEntries.size() > 0)) {
						writeToTargetCalendar(newEntries, obsoleteEntries);
						copyList(sourceEntries, oldSourceEntries);
						saveLastKnownCalendarState(sourceEntries);
					} else {
						OutputManager.println("... no changes found.");
					}
				}

				sleepUtililty.sleepUntilNextRun();

			} catch (Throwable e) {
				// catch all exceptions because even if something goes wrong
				// while reading/writing entries, we never want to break the
				// while-true-loop.
				LOG.error(e.getMessage(), e);
				OutputManager.printerr("Some error occured, resuming loop anyway. I'm still here! Error", e);
				OutputManager.println("Now sleeping for 10 seconds ...");
				try {
					Thread.sleep(TEN_SECONDS);
				} catch (InterruptedException e1) {
					// ignore
				}
			}

		}
	}

	/**
	 * Saves the last known calendar state.
	 * 
	 * @param aEntries
	 *            the source entries
	 */
	private void saveLastKnownCalendarState(ArrayList<ICalendarEntry> aEntries) {
		OutputManager.println("Persisting list of calendar entries.");
		lastKnownCalendarState.deleteAll();
		lastKnownCalendarState.putList(aEntries);
		lastKnownCalendarState.close();
	}

	/**
	 * Write to target calendar.
	 *
	 * @param aNewEntries the new entries to be added to the target calendar
	 * @param aObsoleteEntries the obsolete entries that shall be removed from the target calendar
	 * @throws Exception thrown if anything goes wrong.  
	 */
	private void writeToTargetCalendar(ArrayList<ICalendarEntry> aNewEntries, ArrayList<ICalendarEntry> aObsoleteEntries)
			throws Exception {
		String writeToClassName = configurator
				.getProperty(ConfigParameter.CALENDAR_TO, ConfigParameter.DEFAULT_WRITETO_CLASSNAME);
		OutputManager.println("Some entries have changed, writing to calendar " + writeToClassName + ", ");

		AbstractCalendar targetCalendar = (AbstractCalendar) Class.forName(writeToClassName).newInstance();
		OutputManager.println("Initializing targetCalendar");
		targetCalendar.init(configurator);

		List<ICalendarEntry> toBeDeletedList = new ArrayList<ICalendarEntry>();
		for (ICalendarEntry calendarEntry : aObsoleteEntries) {
			if (!listHasEntryWithID(aNewEntries, calendarEntry)) {
				toBeDeletedList.add(calendarEntry);
			}
		}
		if (toBeDeletedList.size() > 0) {
			OutputManager.println("Removing " + aObsoleteEntries.size() + " entries ...");
			targetCalendar.deleteList(toBeDeletedList);
		}

		OutputManager.println("Adding/Updating " + aNewEntries.size() + " entries ...");
		targetCalendar.putList(aNewEntries);

		targetCalendar.close();
	}

	/**
	 * The main method.
	 * 
	 * @param arAgs
	 *            the arguments
	 * @throws Exception
	 *             the exception
	 */
	public static void main(String[] aAgs) throws Exception {
		CalendarSyncTool notes2GoogleExporter = new CalendarSyncTool();
		notes2GoogleExporter.syncLoop();

	}

	/**
	 * Runs all filters given in the configuration file.
	 * 
	 * These filters will either remove entries completely, or they may change
	 * certain fields of the {@link CalendarEntry}.
	 * 
	 * @param aUnfilteredList
	 *            the unfiltered list of entries
	 * @return The list of filtered entries after the filters have been applied.
	 */
	private ArrayList<ICalendarEntry> runFilters(ArrayList<ICalendarEntry> aUnfilteredList) {
		ArrayList<ICalendarEntry> filteredEntries = new ArrayList<ICalendarEntry>();
		List<ICalendarFilter> allFilters = new ArrayList<ICalendarFilter>();

		String filterScriptNames = configurator.getProperty(ConfigParameter.FILTERS_SCRIPTS,
				ConfigParameter.DEFAULT_FILTERS_SCRIPTS);

		StringTokenizer tok = new StringTokenizer(filterScriptNames, ",");
		while (tok.hasMoreTokens()) {
			String scriptName = tok.nextToken();
			try {
				ScriptFilter filter = new ScriptFilter(scriptName, configurator);
				allFilters.add(filter);
			} catch (FileNotFoundException e) {
				OutputManager.printerr("Found no script named \"" + scriptName + "\"", e);
			}
		}

		// apply the fix location filters
		allFilters.add(new FixLocationFilter(configurator));

		for (ICalendarEntry calendarEntry : aUnfilteredList) {
			boolean skipThisEntry = false;

			for (ICalendarFilter filter : allFilters) {
				try {
					if (!filter.passes(calendarEntry)) {
						skipThisEntry = true;
						break;
					}
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					skipThisEntry = true;
					break;
				}

			}

			if (!skipThisEntry) {
				filteredEntries.add(calendarEntry);
			}
		}
		return filteredEntries;
	}

	/**
	 * Copies all entries from the sourceList to the targetList.
	 * 
	 * @param aSourceList
	 *            the calendar entries read
	 * @param aTargetList
	 *            the last list
	 */
	private void copyList(ArrayList<ICalendarEntry> aSourceList, ArrayList<ICalendarEntry> aTargetList) {
		aTargetList.clear();
		for (ICalendarEntry calendarEntry : aSourceList) {
			aTargetList.add(new CalendarEntry(calendarEntry));
		}
	}

	/**
	 * Gets a list of new entries (that is, entries which are in the given
	 * newList, but not in the oldList).
	 * 
	 * @param aNewList
	 *            the new list
	 * @param aOldList
	 *            the old list
	 * @return the new entries
	 */
	private ArrayList<ICalendarEntry> getNewEntries(ArrayList<ICalendarEntry> aNewList, ArrayList<ICalendarEntry> aOldList) {
		ArrayList<ICalendarEntry> newEntries = new ArrayList<ICalendarEntry>();
		for (ICalendarEntry newEntry : aNewList) {
			if (!aOldList.contains(newEntry)) {
				newEntries.add(newEntry);
			}
		}
		return newEntries;
	}

	/**
	 * Gets a list of obsolete entries (that is, entries which are in the given
	 * oldList, but not in the newList).
	 * 
	 * @param aNewList
	 *            the new list
	 * @param aOldList
	 *            the old list
	 * @return the obsolete entries
	 */
	private ArrayList<ICalendarEntry> getObsoleteEntries(ArrayList<ICalendarEntry> aNewList, ArrayList<ICalendarEntry> aOldList) {
		ArrayList<ICalendarEntry> obsoleteEntries = new ArrayList<ICalendarEntry>();
		for (ICalendarEntry oldEntry : aOldList) {
			if (!aNewList.contains(oldEntry)) {
				obsoleteEntries.add(oldEntry);
			}
		}
		return obsoleteEntries;
	}

	/**
	 * Checks if the list has a calendar entry with the given id.
	 * 
	 * @param aList
	 *            the list
	 * @param aEntry
	 *            the entry
	 * @return true, if successful
	 */
	private boolean listHasEntryWithID(ArrayList<ICalendarEntry> aList, ICalendarEntry aEntry) {
		for (ICalendarEntry calendarEntry : aList) {
			if (calendarEntry.getUniqueID().equals(aEntry.getUniqueID())) {
				return true;
			}
		}
		return false;
	}

}