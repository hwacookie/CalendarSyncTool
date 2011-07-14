package de.mbaaba.calendar;

import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;
import de.mbaaba.util.Units;

public class TestSleepUtility extends TestCase {

	protected boolean passed;
	
	private Thread guardTimer(final int aTimeout) {
		System.out.println("WARNING: This test runs for approximatly "+aTimeout+" seconds!");
		
		final Thread parent = Thread.currentThread();
		
		Thread guard = new Thread(new Runnable() {

			public void run() {
				try {
					Thread.sleep(aTimeout*Units.SECOND);
					System.err.println("The test has failed, the sleep took too long!");
					passed = false;
					parent.interrupt();
				} catch (InterruptedException e) {
					passed = true;
				}
			}
		});
		guard.start();
		return guard;
	}
	

	public void testIsOfficehour() {
		ConfiguratorForTests configurator = new ConfiguratorForTests();
		GregorianCalendar cal = new GregorianCalendar();

		configurator.setProperty("officeHours.start", String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
		configurator.setProperty("officeHours.end", String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
		configurator.setProperty("officeHours.workdays", "" + cal.get(Calendar.DAY_OF_WEEK));

		SleepUtililty sleepUtililty = new SleepUtililty(configurator);

		long workdate = cal.getTimeInMillis();
		boolean inOfficehour = sleepUtililty.isInOfficehour(workdate);
		assertTrue("This should be an officehour!", inOfficehour);

		inOfficehour = sleepUtililty.isInOfficehour(workdate + Units.HOUR);
		assertFalse("This should not be an officehour!", inOfficehour);

		inOfficehour = sleepUtililty.isInOfficehour(workdate - Units.HOUR);
		assertFalse("This should not be an officehour!", inOfficehour);
	}

	public void testSleepUntilOnWorkdays() {

		ConfiguratorForTests configurator = new ConfiguratorForTests();
		GregorianCalendar cal = new GregorianCalendar();

		configurator.setProperty("officeHours.start", String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
		configurator.setProperty("officeHours.end", String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
		configurator.setProperty("officeHours.workdays", "" + cal.get(Calendar.DAY_OF_WEEK));
		configurator.setProperty("repeatEach.officehours", "1");
		configurator.setProperty("repeatEach.nonofficehours", "2");

		SleepUtililty sleepUtililty = new SleepUtililty(configurator);

		Thread guard = guardTimer(63);

		long start = System.currentTimeMillis();
		sleepUtililty.sleepUntilNextRun();
		long duration = System.currentTimeMillis() - start;

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
		ConfiguratorForTests configurator = new ConfiguratorForTests();
		GregorianCalendar cal = new GregorianCalendar();

		configurator.setProperty("officeHours.start", String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
		configurator.setProperty("officeHours.end", String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
		configurator.setProperty("repeatEach.nonofficehours", "2");

		SleepUtililty sleepUtililty = new SleepUtililty(configurator);

		


		// make sure we are not in an offcehour
		if (cal.get(Calendar.DAY_OF_WEEK) == 0) {
			configurator.setProperty("officeHours.workdays", "1");
		} else {
			configurator.setProperty("officeHours.workdays", "0");
		}

		Thread guard = guardTimer(123);
		
		long start = System.currentTimeMillis();
		sleepUtililty.sleepUntilNextRun();
		long duration = System.currentTimeMillis() - start;

		stopGuardTimer(guard);

		assertTrue("SleepTime should have been approx. 120 seconds for non-workdays!", (duration >= 118 * Units.SECOND)
				&& (duration <= 122 * Units.SECOND));

	}

}
