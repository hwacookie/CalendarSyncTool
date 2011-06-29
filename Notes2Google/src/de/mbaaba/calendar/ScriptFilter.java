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

public class ScriptFilter implements ICalendarFilter {

	private ScriptEngine scriptEngine;
	private File file;
	private FileReader reader;

	public ScriptFilter(String scriptName, Configurator aConfigurator) throws FileNotFoundException {
		file = new File(scriptName);
		reader = new FileReader(file);

		String extension = file.getName().substring(file.getName().lastIndexOf('.') + 1);

		ScriptEngineManager manager = new ScriptEngineManager();
		scriptEngine = manager.getEngineByExtension(extension);
		scriptEngine.put("configurator", aConfigurator);
	}

	public boolean passes(ICalendarEntry aParamCalendarEntry) throws Exception {
		scriptEngine.put("calendarEntry", aParamCalendarEntry);
		scriptEngine.eval(reader);

		boolean returnValue = (Boolean) ((Invocable) scriptEngine).invokeFunction("filter");
		return returnValue;
	}

}
