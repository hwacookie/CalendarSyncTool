/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden All rights reserved.
 * --------------------------------------------------------------------------
 */
package de.mbaaba.util;

/**
 * Utility class for objects.
 * 
 * @author walden_h1
 */
public final class ObjectUtil {

	/**
	 * No initialization!
	 */
	private ObjectUtil() {
	}

	/**
	 * Helper-method that compares two objects, circumventing the NPE that may
	 * occur if one of the two objects is <code>null</code>.
	 * 
	 * @param aO1
	 *            the first object
	 * @param aO2
	 *            the other object
	 * @return true, if a01 equals aO2
	 */
	public static boolean objectEquals(Object aO1, Object aO2) {
		if (aO1 == aO2) {
			return true;
		}
		if ((aO1 == null) || (aO2 == null)) {
			return false;
		}
		return aO1.equals(aO2);
	}
	
	// linux macht auch was
}
