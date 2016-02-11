/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */
package de.mbaaba.calendar;

import java.util.Calendar;
import java.util.GregorianCalendar;

import de.mbaaba.util.Configurator;
import de.mbaaba.util.OutputManager;
import de.mbaaba.util.Units;

/**
 * A utility class that handles the sleep between invocations of the sync loop.
 */
public class SleepUtililty {

	/**
	 * This class encapsulates configuration parameters and their default values.
	 */
	class ConfigParameter {
		private static final String OFFICE_HOURS_END = "officeHours.end";

		private static final String OFFICE_HOURS_START = "officeHours.start";

		private static final String OFFICE_HOURS_WORKDAYS = "officeHours.workdays";

		private static final String REPEAT_EACH_NONOFFICEHOURS = "repeatEach.nonofficehours";

		private static final String REPEAT_EACH_OFFICEHOURS = "repeatEach.officehours";

		private static final int DEFAULT_OFFICE_HOURS_END = 18;

		private static final int DEFAULT_OFFICE_HOURS_START = 8;

		private static final int DEFAULT_REPEAT_EACH_OFFICEHOURS = 20;

		private static final int DEFAULT_REPEAT_EACH_NONOFFICEHOURS = 120;

		private static final String DEFAULT_OFFICE_HOURS_WORKDAYS = "1 2 3 4 5";

		private void dumpConfig() {
			dumpValue(ConfigParameter.OFFICE_HOURS_START);
			dumpValue(ConfigParameter.OFFICE_HOURS_END);
			dumpValue(ConfigParameter.OFFICE_HOURS_WORKDAYS);
			dumpValue(ConfigParameter.REPEAT_EACH_OFFICEHOURS);
			dumpValue(ConfigParameter.REPEAT_EACH_NONOFFICEHOURS);
		}

		private void dumpValue(String aParamName) {
			OutputManager.println(aParamName + "=" + configurator.getProperty(aParamName, "<set-value-for-" + aParamName + ">"));
		}
	}

	private static final long TEN_SECONDS = Units.SECOND * 10;

	private Configurator configurator;

	public SleepUtililty(Configurator aConfigurator) {
		configurator = aConfigurator;
		new ConfigParameter().dumpConfig();
	}

	void sleepUntilNextRun() {

		int nextRunInMinutes;
		if (isInOfficehour(System.currentTimeMillis())) {
			nextRunInMinutes = configurator.getProperty(ConfigParameter.REPEAT_EACH_OFFICEHOURS,
					ConfigParameter.DEFAULT_REPEAT_EACH_OFFICEHOURS);
		} else {
			nextRunInMinutes = configurator.getProperty(ConfigParameter.REPEAT_EACH_NONOFFICEHOURS,
					ConfigParameter.DEFAULT_REPEAT_EACH_NONOFFICEHOURS);
		}

		// Now, wait some time until we run again
		final long lastrun = System.currentTimeMillis();

		OutputManager.print("Sleeping until next run in approx. " + nextRunInMinutes + " minutes ");

		// sleep-loop
		while (true) {
			// No matter what, always sleep for at least 10 seconds.
			try {
				System.out.print(".");
				Thread.sleep(TEN_SECONDS);
			} catch (final InterruptedException e) {
				break;
			}

			// get current time
			final long now = System.currentTimeMillis();

			// how much time has passed since the last run?
			final long timePassedSinceLastRun = now - lastrun;

			// are we in an office hour now?
			if (isInOfficehour(now)) {
				// re-read configuration parameter
				final int repeatEachDuringOfficehours = configurator.getProperty(ConfigParameter.REPEAT_EACH_OFFICEHOURS,
						ConfigParameter.DEFAULT_REPEAT_EACH_OFFICEHOURS);
				if (timePassedSinceLastRun > (repeatEachDuringOfficehours * Units.MINUTE)) {
					// break the sleep-loop if it's time to run again
					break;
				}
			} else {
				// we are not in an officehour
				// re-read configuration parameter
				final int repeatEachDuringNonOfficehours = configurator.getProperty(ConfigParameter.REPEAT_EACH_NONOFFICEHOURS,
						ConfigParameter.DEFAULT_REPEAT_EACH_NONOFFICEHOURS);
				if (timePassedSinceLastRun > (repeatEachDuringNonOfficehours * Units.MINUTE)) {
					// break the sleep-loop if it's time to run again
					break;
				}
			}
		}
		System.out.println(" uaaargh ... yawn !!!");

	}

	/**
	 * Checks if the given time is within office hours.
	 *
	 * @param aDate the date to be checked
	 * @return true, if the given time is within office hours.
	 */
	boolean isInOfficehour(long aDate) {
		final String workdays = configurator.getProperty(ConfigParameter.OFFICE_HOURS_WORKDAYS,
				ConfigParameter.DEFAULT_OFFICE_HOURS_WORKDAYS);
		final int officeHoursStart = configurator.getProperty(ConfigParameter.OFFICE_HOURS_START,
				ConfigParameter.DEFAULT_OFFICE_HOURS_START);
		final int officeHoursEnd = configurator.getProperty(ConfigParameter.OFFICE_HOURS_END, ConfigParameter.DEFAULT_OFFICE_HOURS_END);

		final Calendar now = new GregorianCalendar();
		now.setTimeInMillis(aDate);
		final int day = now.get(Calendar.DAY_OF_WEEK);
		final int hour = now.get(Calendar.HOUR_OF_DAY);
		return ((workdays.indexOf(String.valueOf(day)) >= 0) && (hour >= officeHoursStart) && (hour <= officeHoursEnd));
	}
}
