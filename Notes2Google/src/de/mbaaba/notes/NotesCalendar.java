/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.notes;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.DateRange;
import lotus.domino.DbDirectory;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesFactory;
import lotus.domino.NotesThread;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;
import de.mbaaba.calendar.AbstractCalendar;
import de.mbaaba.calendar.CalendarEntry;
import de.mbaaba.calendar.ICalendarEntry;
import de.mbaaba.util.Configurator;
import de.mbaaba.util.Logger;

/**
 * The Class NotesCalendar allows to access events within a Lotus-Notes calendar.
 * Currently, the calendar is read only and does not allow to delete, add or change entries in the calendar.
 */
public class NotesCalendar extends AbstractCalendar {

	/** The time that we wait for the data fetcher to finish its job. */
	private static final long DATA_FETCHER_SLEEP_TIME = 100L;

	/** Used for logging. */
	private static final Logger LOG = new Logger(NotesCalendar.class);

	public ArrayList<ICalendarEntry> readCalendarEntries(Date aStartDate, Date aEndDate) {
		DataFetcher dataFetcher = new DataFetcher(aStartDate, aEndDate);
		NotesThread nt = new NotesThread(dataFetcher);
		nt.start();
		while (dataFetcher.isRunning()) {
			try {
				Thread.sleep(DATA_FETCHER_SLEEP_TIME);
			} catch (InterruptedException localInterruptedException) {
				// ignored
			}
		}
		ArrayList<ICalendarEntry> entries = new ArrayList<ICalendarEntry>();
		for (CalendarEntry calendarEntry : dataFetcher.getCalendarEntries()) {
			entries.add(calendarEntry);
		}
		return entries;
	}

	public void init(Configurator aConfigurator) {
	}

	/** 
	 * Nothing to close, yet.
	 */
	public void close() {
	}

	/**
	 * A Notes-runnable that fetches the data from the Notes calendar.
	 */
	public class DataFetcher extends NotesCalendar.AbstractRunnable {

		/** The Constant COL_START_DATE. */
		private static final int COL_START_DATE = 8;

		/** The Constant COL_END_DATE. */
		private static final int COL_END_DATE = 10;

		/** The end date. */
		private Date endDate;

		/** The start date. */
		private Date startDate;

		/** The calendar entries. */
		private HashMap<String, NotesCalendarEntry> calendarEntries;

		/**
		 * Instantiates a new data fetcher.
		 *
		 * @param aStartDate the a start date
		 * @param aEndDate the a end date
		 */
		public DataFetcher(Date aStartDate, Date aEndDate) {
			super();
			startDate = aStartDate;
			endDate = aEndDate;
			calendarEntries = new HashMap<String, NotesCalendarEntry>();
		}

		public void run() {
			try {
				Session session = NotesFactory.createSession();
				NotesPerson.init(session);

				String p = session.getPlatform();
				NotesCalendar.LOG.debug("Platform is " + p);

				Database mailDB = session.getDatabase("", "names.nsf");
				if (mailDB != null) {
					DbDirectory dir = session.getDbDirectory(null);
					mailDB = dir.openMailDatabase();

					if (!mailDB.isOpen()) {
						mailDB.open();
					}
					if (mailDB.isOpen()) {
						View view = mailDB.getView("($Calendar)");

						DateRange dr = session.createDateRange(startDate, endDate);

						ViewEntryCollection collection = view.getAllEntriesByKey(dr, true);

						ViewEntry viewEntry = collection.getFirstEntry();

						while (viewEntry != null) {
							String universalID = viewEntry.getUniversalID();
							if (!calendarEntries.containsKey(universalID)) {
								NotesCalendarEntry calendarEntry = new NotesCalendarEntry();
								calendarEntry.setUniqueID(universalID);

								Object viewEntryStartDate = viewEntry.getColumnValues().get(COL_START_DATE);

								Object viewEntryEndDate = viewEntry.getColumnValues().get(COL_END_DATE);

								// only add if we have both a start and a
								// endDate (i.e., this is not a ToDo)
								if ((viewEntryStartDate != null) && (viewEntryEndDate != null)) {
									calendarEntries.put(universalID, calendarEntry);
									// now parse the document for details
									Document doc = viewEntry.getDocument();
									Vector<?> items = doc.getItems();
									for (int j = 0; j < items.size(); j++) {
										Item item = (Item) items.elementAt(j);
										calendarEntry.mapItem(item);
									}
								}

							}
							viewEntry = collection.getNextEntry();

						}
						running = false;
					}
				}

			} catch (Exception e) {
				running = false;
				e.printStackTrace();
			}
		}

		/**
		 * Gets the calendar entries.
		 *
		 * @return the calendar entries
		 */
		public ArrayList<NotesCalendarEntry> getCalendarEntries() {
			ArrayList<NotesCalendarEntry> res = new ArrayList<NotesCalendarEntry>();
			for (NotesCalendarEntry entry : calendarEntries.values()) {
				res.add(entry);
			}
			return res;
		}
	}

	/**
	 * A simple class that extends {@link Runnable} and adds a field that indicates if the run method is still running.
	 */
	public abstract class AbstractRunnable implements Runnable {

		/** The running. */
		protected boolean running = true;

		/**
		 * Instantiates a new notes runnable.
		 */
		public AbstractRunnable() {
		}

		/**
		 * Checks if is running.
		 *
		 * @return true, if is running
		 */
		public boolean isRunning() {
			return running;
		}
	}

	/**
	 * Adding calendar entries is not supported (yet?)
	 *
	 * @param aCalendarEntry the a calendar entry
	 * @see de.mbaaba.calendar.AbstractCalendar#put(de.mbaaba.calendar.ICalendarEntry)
	 */
	@Override
	public void put(ICalendarEntry aCalendarEntry) {
		throw new RuntimeException("Operation not yet supported!");
	}

	/**
	 * Deleting calendar entries is not supported (yet?)
	 *
	 * @param aCalendarEntry the a calendar entry
	 * @see de.mbaaba.calendar.AbstractCalendar#delete(de.mbaaba.calendar.ICalendarEntry)
	 */
	@Override
	public void delete(ICalendarEntry aCalendarEntry) {
		throw new RuntimeException("Operation not yet supported!");
	}

}