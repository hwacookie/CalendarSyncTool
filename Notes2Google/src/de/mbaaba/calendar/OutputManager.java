package de.mbaaba.calendar;

import java.text.DateFormat;
import java.util.Date;

import de.mbaaba.util.Logger;

public class OutputManager {

	/**
	 * A logger for this class.
	 */
	private static final Logger LOG = new Logger("CalendarSyncTool");

	/**
	 * Prints a status message.
	 * 
	 * @param aMessage
	 *            the string
	 */
	public static void print(String aMessage) {
		LOG.debug(aMessage);
		System.out.print(logDateFormatter.format(new Date()) + " | " + aMessage);
	}

	/**
	 * Prints a error message.
	 * 
	 * @param aErrorMessage
	 *            the string
	 */
	public static void printerr(String aErrorMessage) {
		LOG.error(aErrorMessage);
		System.err.println(logDateFormatter.format(new Date()) + " | " + aErrorMessage);
	}

	/**
	 * Prints a error message.
	 * 
	 * @param aErrorMessage
	 *            the string
	 */
	public static void printerr(String aErrorMessage, Throwable aException) {
		LOG.error(aErrorMessage, aException);
		System.err.println(logDateFormatter.format(new Date()) + " | " + aErrorMessage + ": " + aException.getMessage());
	}

	/**
	 * Prints a status message.
	 * 
	 * @param aMessage
	 *            the string
	 */
	public static void println(String aMessage) {
		LOG.debug(aMessage);
		System.out.println(logDateFormatter.format(new Date()) + " | " + aMessage);
	}

	/**
	 * A formatter for dates. Used for println to the console.
	 */
	private static DateFormat logDateFormatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

}
