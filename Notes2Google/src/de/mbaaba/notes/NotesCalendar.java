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
import de.mbaaba.calendar.CalendarEntry;
import de.mbaaba.calendar.ReadOnlyCalendar;
import de.mbaaba.util.Configurator;
import de.mbaaba.util.Logger;

public class NotesCalendar extends ReadOnlyCalendar {

	private static final Logger LOG = new Logger(NotesCalendar.class);

	public ArrayList<CalendarEntry> readCalendarEntries(Date aStartDate, Date aEndDate) {
		DataFetcher dataFetcher = new DataFetcher(aStartDate, aEndDate);
		NotesThread nt = new NotesThread(dataFetcher);
		nt.start();
		while (dataFetcher.isRunning()) {
			try {
				Thread.sleep(100L);
			} catch (InterruptedException localInterruptedException) {
			}
		}
		ArrayList<CalendarEntry> entries = new ArrayList<CalendarEntry>();
		for (CalendarEntry calendarEntry : dataFetcher.getCalendarEntries()) {
			entries.add(calendarEntry);
		}
		return entries;
	}

	public void init(Configurator aConfigurator) {
	}

	public void close() {
	}

	public class DataFetcher extends NotesCalendar.NotesRunnable {
		private Date endDate;
		private Date startDate;
		public HashMap<String, NotesCalendarEntry> calendarEntries;

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

								Object viewEntryStartDate = viewEntry.getColumnValues().get(8);

								Object viewEntryEndDate = viewEntry.getColumnValues().get(10);

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
									// if (calendarEntry.getLastModified() ==
									// null) {
									// calendarEntry.setLastModified(new
									// Date());
									// }
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

		public ArrayList<NotesCalendarEntry> getCalendarEntries() {
			ArrayList<NotesCalendarEntry> res = new ArrayList<NotesCalendarEntry>();
			for (NotesCalendarEntry entry : calendarEntries.values()) {
				res.add(entry);
			}
			return res;
		}
	}

	public abstract class NotesRunnable implements Runnable {
		protected boolean running = true;

		public NotesRunnable() {
		}

		public boolean isRunning() {
			return running;
		}
	}

}