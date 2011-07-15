/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.calendar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import de.mbaaba.util.Configurator;

/**
 * A class that encapsulates some script that acts as a {@link ICalendarFilter}. 
 */
public class ScriptFilter implements ICalendarFilter {

	/** The used scripting-engine. */
	private ScriptEngine scriptEngine;

	/** The file that contains the script. */
	private File file;

	/** The reader that is used to read the script. */
	private FileReader reader;

	public ScriptFilter(String aScriptName, Configurator aConfigurator) throws FileNotFoundException {
		file = new File(aScriptName);
		reader = new FileReader(file);

		final String extension = file.getName().substring(file.getName().lastIndexOf('.') + 1);

		final ScriptEngineManager manager = new ScriptEngineManager();
		scriptEngine = manager.getEngineByExtension(extension);
		scriptEngine.put("configurator", aConfigurator);
	}

	/**
	 * This method runs the script and returns the scripts return value as its result.
	 *
	 * @see de.mbaaba.calendar.ICalendarFilter#passes(de.mbaaba.calendar.ICalendarEntry)
	 */
	@Override
	public boolean passes(ICalendarEntry aParamCalendarEntry) throws Exception {
		scriptEngine.put("calendarEntry", aParamCalendarEntry);
		scriptEngine.eval(reader);

		final boolean returnValue = (Boolean) ((Invocable) scriptEngine).invokeFunction("filter");
		return returnValue;
	}

}
