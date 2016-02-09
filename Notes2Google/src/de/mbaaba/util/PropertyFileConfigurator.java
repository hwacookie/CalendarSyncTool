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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;


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
		} catch (final IOException e) {
			OutputManager.printerr("The file " + propFileName + " could not be found!", e);
			OutputManager.printerr("Copy the template-property file, make your changes and rename it to " + propFileName + ".");
			OutputManager.printerr("I will exit now.");
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

	public List<String> getMultiProperty(String aPropertyName, String aDefaultValue) {
		String s = getProperty(aPropertyName, aDefaultValue);
		final StringTokenizer tok = new StringTokenizer(s, ",");
		List<String> allElements = new ArrayList<String>();
		while (tok.hasMoreTokens()) {
			final String element = tok.nextToken();
			allElements.add(element);
		}
		return allElements;
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
			final String s = props.getProperty(aPropertyName, "" + aDefaultValue);
			if (s.startsWith("<setup-")) {
				OutputManager.printerr("Missing a value for " + aPropertyName + " in the property file, please fix!");
				OutputManager.printerr("I will exit now.");
				System.exit(1);
			}
			final Integer res = Integer.parseInt(s);
			return res.intValue();
		} catch (final NumberFormatException e) {
			return aDefaultValue;
		}
	}

}
