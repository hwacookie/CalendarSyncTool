/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */
package de.mbaaba.util;

/**
 * This class helps to read the properties.
 */
public final class PropertiesHelper {
	/**
	 * inhibit instanciation.
	 */
	private PropertiesHelper() {
	}

	/**
	 * get headless mode.
	 * @param pKey name of key.
	 * @param pDef default value.
	 * @return string stored in property.
	 */
	protected static String getProperty(String pKey, String pDef) {
		String str = System.getProperty(pKey, pDef);
		if (str != null) {
			return str.trim();
		} else {
			return null;
		}
	}

	/**
	 * get headless mode.
	 * @param pKey name of key.
	 * @return string stored in property.
	 */
	protected static String getProperty(String pKey) {
		return getProperty(pKey, null);
	}
}
