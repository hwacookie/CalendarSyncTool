/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.util;

import java.text.DateFormat;
import java.util.Date;


/**
 * The Class OutputManager handles output to the screen.
 */
public final class OutputManager {

	/**
	 * A formatter for dates. Used for printing to the console.
	 */
	private static DateFormat logDateFormatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

	/**
	 * A logger for this class.
	 */
	private static final Logger LOG = new Logger("CalendarSyncTool");

	/**
	 * Inhibit instantiation.
	 */
	private OutputManager() {
	}

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
	 * @param aErrorMessage the string
	 * @param aException the a exception
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

}
