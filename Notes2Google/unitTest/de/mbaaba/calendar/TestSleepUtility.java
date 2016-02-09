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

import de.mbaaba.util.Units;
import junit.framework.TestCase;

//CHECKSTYLE:OFF  ... show some mercy on hacked unit-tests.
public class TestSleepUtility extends TestCase {

	protected boolean passed;

	private Thread guardTimer(final int aTimeout) {
		System.out.println("WARNING: This test runs for approximatly " + aTimeout + " seconds!");

		final Thread parent = Thread.currentThread();

		final Thread guard = new Thread(new Runnable() {

			public void run() {
				try {
					Thread.sleep(aTimeout * Units.SECOND);
					System.err.println("The test has failed, the sleep took too long!");
					passed = false;
					parent.interrupt();
				} catch (final InterruptedException e) {
					passed = true;
				}
			}
		});
		guard.start();
		return guard;
	}

	public void testIsOfficehour() {
		final ConfiguratorForTests configurator = new ConfiguratorForTests();
		final GregorianCalendar cal = new GregorianCalendar();

		configurator.setProperty("officeHours.start", String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
		configurator.setProperty("officeHours.end", String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
		configurator.setProperty("officeHours.workdays", "" + cal.get(Calendar.DAY_OF_WEEK));

		final SleepUtililty sleepUtililty = new SleepUtililty(configurator);

		final long workdate = cal.getTimeInMillis();
		boolean inOfficehour = sleepUtililty.isInOfficehour(workdate);
		assertTrue("This should be an officehour!", inOfficehour);

		inOfficehour = sleepUtililty.isInOfficehour(workdate + Units.HOUR);
		assertFalse("This should not be an officehour!", inOfficehour);

		inOfficehour = sleepUtililty.isInOfficehour(workdate - Units.HOUR);
		assertFalse("This should not be an officehour!", inOfficehour);
	}

	public void testSleepUntilOnWorkdays() {

		final ConfiguratorForTests configurator = new ConfiguratorForTests();
		final GregorianCalendar cal = new GregorianCalendar();

		configurator.setProperty("officeHours.start", String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
		configurator.setProperty("officeHours.end", String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
		configurator.setProperty("officeHours.workdays", "" + cal.get(Calendar.DAY_OF_WEEK));
		configurator.setProperty("repeatEach.officehours", "1");
		configurator.setProperty("repeatEach.nonofficehours", "2");

		final SleepUtililty sleepUtililty = new SleepUtililty(configurator);

		final Thread guard = guardTimer(63);

		final long start = System.currentTimeMillis();
		sleepUtililty.sleepUntilNextRun();
		final long duration = System.currentTimeMillis() - start;

		stopGuardTimer(guard);

		assertTrue("SleepTime should have been approx. 60 seconds for workdays!", (duration >= 58 * Units.SECOND)
				&& (duration <= 62 * Units.SECOND));

	}

	private void stopGuardTimer(Thread guard) {
		if (!guard.isInterrupted()) {
			guard.interrupt();
		} else {
			fail("The test has failed, the sleep took too long!");
		}
	}

	public void testSleepUntilOnNonWorkdays() {
		final ConfiguratorForTests configurator = new ConfiguratorForTests();
		final GregorianCalendar cal = new GregorianCalendar();

		configurator.setProperty("officeHours.start", String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
		configurator.setProperty("officeHours.end", String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
		configurator.setProperty("repeatEach.nonofficehours", "2");

		final SleepUtililty sleepUtililty = new SleepUtililty(configurator);

		// make sure we are not in an offcehour
		if (cal.get(Calendar.DAY_OF_WEEK) == 0) {
			configurator.setProperty("officeHours.workdays", "1");
		} else {
			configurator.setProperty("officeHours.workdays", "0");
		}

		final Thread guard = guardTimer(123);

		final long start = System.currentTimeMillis();
		sleepUtililty.sleepUntilNextRun();
		final long duration = System.currentTimeMillis() - start;

		stopGuardTimer(guard);

		assertTrue("SleepTime should have been approx. 120 seconds for non-workdays!", (duration >= 118 * Units.SECOND)
				&& (duration <= 122 * Units.SECOND));

	}

}
