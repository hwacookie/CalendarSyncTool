/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.calendar;

import de.mbaaba.google.GoogleCalendar;
import de.mbaaba.util.CommConfigUtil;
import de.mbaaba.util.Configurator;
import de.mbaaba.util.PropertyFileConfigurator;

/**
 * Utility class that deletes all entries from the google calendar. Use with
 * care!
 * 
 * @author walden_h1
 */
public final class ClearGoogle {

	/**
	 * No initialization.
	 */
	private ClearGoogle() {

	}

	public static void clearGoogle() throws Exception {
		final Configurator aConfigurator = new PropertyFileConfigurator("Notes2Google.properties");
		CommConfigUtil.init(aConfigurator);
		final GoogleCalendar calendar = new GoogleCalendar();
		calendar.init(aConfigurator);
		calendar.deleteAllEntries();
	}

	public static void main(String[] aArgs) throws Exception {
		clearGoogle();
	}
}