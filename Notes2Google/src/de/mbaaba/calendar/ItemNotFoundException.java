/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */


package de.mbaaba.calendar;

public class ItemNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public ItemNotFoundException(String aMessage) {
		super(aMessage);
	}

	public ItemNotFoundException(String aMessage, Exception aE) {
		super(aMessage, aE);
	}
}
