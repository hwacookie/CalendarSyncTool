/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.mbaaba.calendar.CalendarSyncTool;

/**
 * The Class AdapterConfigurator contains functionality to configure the
 * connection parameters for the {@link #Adapter()}.
 */
public class PropertyFileConfigurator implements Configurator {

	/** The file that contains the configuration data. */
	private Properties props;

	private String propFileName;

	/**
	 * Instantiates a new adapter configurator.
	 * 
	 * @param aPropFileName
	 */
	public PropertyFileConfigurator(String aPropFileName) {
		propFileName = aPropFileName;
		readProperties();
	}

	/**
	 * Reads configuration properties from the file "adapter.properties". This
	 * file must reside anywhere in the classpath.
	 */
	public void readProperties() {
		props = new Properties();
		try {
			InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			if (resourceStream == null) {
				resourceStream = new FileInputStream(new File("../etc/" + propFileName));
			}
			if (resourceStream != null) {
				props.load(resourceStream);
			}
		} catch (IOException e) {
			CalendarSyncTool.printerr("The file " + propFileName + " could not be found!", e);
			CalendarSyncTool.printerr("Copy the template-property file, make your changes and rename it to "+propFileName+".");
			CalendarSyncTool.printerr("I will exit now.");
			System.exit(1);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.biotronik.Configurator.adapter.AdapterConfigurator#getProperty(java.lang.String,
	 *      java.lang.String)
	 */
	public String getProperty(String aPropertyName, String aDefaultValue) {
		return props.getProperty(aPropertyName, aDefaultValue);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.biotronik.Configurator.adapter.AdapterConfigurator#setProperty(java.lang.String,
	 *      java.lang.String)
	 */
	public void setProperty(String aPropertyName, String aValue) {
		props.setProperty(aPropertyName, aValue);
	}

	public int getProperty(String aPropertyName, int aDefaultValue) {
		try {
			String s = props.getProperty(aPropertyName, "" + aDefaultValue);
			if (s.startsWith("<setup-")) {
				CalendarSyncTool.printerr("Missing a value for "+aPropertyName+" in the property file, please fix!");
				CalendarSyncTool.printerr("I will exit now.");
				System.exit(1);
			}
			Integer res = Integer.parseInt(s);
			return res.intValue();
		} catch (NumberFormatException e) {
			return aDefaultValue;
		}
	}

}
