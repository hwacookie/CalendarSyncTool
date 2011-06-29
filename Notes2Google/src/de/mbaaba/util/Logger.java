/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.util;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;

/**
 * encapsulates the apache log4j API.
 *
 * @author     HWalden
 * @created    24. Juni 2002
 * @version    $Revision: 1.16 $
 * @review 16.09.2003 mbender, cboehmer
 */
// TODO review comment: Javadocs
public class Logger implements Log {
	/** Decides whether to exit from the system in case of a fatal log */
	private static boolean noExitOnFatal;

	/**
	 * exit parameter.
	 */
	private static final int EXIT_PARAMETER = -3;

	static {
		// use this logger as the commons logging logger
		System.setProperty(LogFactoryImpl.LOG_PROPERTY, Logger.class.getName());
	}

	/** the category for this Logger */
	private org.apache.log4j.Logger logger = null;

	/**
	 *  Constructor for the Logger object.
	 *
	 * @param  aClass  set category to class name.
	 */
	public Logger(Class<?> aClass) {
		logger = LoggingUtility.createCategory(aClass);
	}

	/**
	 *  Constructor for the Logger object.
	 *
	 * @param  aLoggerName the logger name.
	 */
	public Logger(String aLoggerName) {
		logger = LoggingUtility.createCategory(aLoggerName);
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.log4j.Logger#isDebugEnabled()
	 */
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.log4j.Category#debug()
	 */
	public void debug(Object pObj, Throwable pEx, Object... pParams) {
		logger.debug(interpolate(pObj, pParams), pEx);
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.log4j.Category#debug()
	 */
	public void debug(Object pObj, Object... pParams) {
		logger.debug(interpolate(pObj, pParams));
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.commons.logging.Log#debug(java.lang.Object)
	 */
	public void debug(Object pObj) {
		logger.debug(pObj);
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.apache.log4j.Category#error()
	 */
	public void error(Object pObj, Throwable pEx, Object... pParams) {
		logger.error(interpolate(pObj, pParams), pEx);
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.log4j.Category#error()
	 */
	public void error(Object pObj, Object... pParams) {
		logger.error(interpolate(pObj, pParams));
	}

	/**
	 * Getter for category
	 *
	 * @return the category field
	 */
	public org.apache.log4j.Logger getCat() {
		return logger;
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.log4j.Category#fatal()
	 */
	public void fatal(Object pObj, Throwable pEx, Object... pParams) {
		logger.fatal(interpolate(pObj, pParams), pEx);
		if (!noExitOnFatal) {
			System.exit(EXIT_PARAMETER);
		}
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.log4j.Logger#isInfoEnabled()
	 */
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.log4j.Category#info()
	 */
	public void info(Object pObj, Throwable pEx, Object... pParams) {
		logger.info(interpolate(pObj, pParams), pEx);
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.log4j.Category#info()
	 */
	public void info(Object pObj, Object... pParams) {
		logger.info(interpolate(pObj, pParams));
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.log4j.Category#info()
	 */
	public void info(Object pObj) {
		logger.info(pObj);
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.apache.log4j.Category#warn()
	 */
	public void warn(Object pObj, Throwable pEx, Object... pParams) {
		logger.warn(interpolate(pObj, pParams), pEx);
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.log4j.Category#warn()
	 */
	public void warn(Object pObj, Object... pParams) {
		logger.warn(interpolate(pObj, pParams));
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.log4j.Category#warn()
	 */
	public void warn(Object pObj) {
		logger.warn(pObj);
	}
	
	/**
	 * Sets the noExitOnFatal.
	 * @param aNoExitOnFatal The noExitOnFatal to set
	 */
	public static void setNoExitOnFatal(boolean aNoExitOnFatal) {
		noExitOnFatal = aNoExitOnFatal;
	}

	/**
	 * Replace all expressions in the form #0, #1,... with their given parameter values.
	 * 
	 * @param pObject a template
	 * @param pParams the parameters to interpolate
	 * @return the interpolated string if the given object was a string, the object otherwise
	 */
	private Object interpolate(Object pObject, Object... pParams) {
		if (pObject instanceof String && pParams != null) {
			return interpolate((String) pObject, pParams);
		} else {
			return pObject;
		}
	}

	/**
	 * Replace all expressions in the form #0, #1,... with their given parameter values.
	 * 
	 * @param pString a template
	 * @param pParams the parameters to interpolate
	 * @return the interpolated string
	 */
	private String interpolate(String pString, Object... pParams) {
		if (pParams == null) {
			pParams = new Object[0];
		}

		if (pString.indexOf('#') >= 0) {
			pString = interpolateParameters(pString, pParams);
		}
		return pString;
	}

	/**
	 * Replace all expressions in the form #0, #1,... with their given parameter values.
	 * replaces #0 with the first value in pParams, #1 with the second,...
	 * 
	 * @param pString a template
	 * @param pParams the parameters to interpolate
	 * @return  the interpolated string
	 */
	private String interpolateParameters(String pString, Object... pParams) {
		StringTokenizer tokens = new StringTokenizer(pString, "#{}", true);
		StringBuilder builder = new StringBuilder(pString.length());
		while (tokens.hasMoreTokens()) {
			String tok = tokens.nextToken();
			if ("#".equals(tok) && tokens.hasMoreTokens()) {
				String nextTok = tokens.nextToken();
				int index;
				try {
					index = Integer.parseInt(nextTok.substring(0, 1));
					if (index >= pParams.length) {
						builder.append("#").append(nextTok);
					} else {
						builder.append(pParams[index]).append(nextTok.substring(1));
					}
				} catch (NumberFormatException nfe) {
					builder.append("#").append(nextTok);
				}
			} else {
				builder.append(tok);
			}
		}
		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.commons.logging.Log#debug(java.lang.Object, java.lang.Throwable)
	 */
	public void debug(Object pMessage, Throwable pT) {
		logger.debug(pMessage, pT);
		
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.commons.logging.Log#error(java.lang.Object)
	 */
	public void error(Object pMessage) {
		logger.error(pMessage);
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.commons.logging.Log#error(java.lang.Object, java.lang.Throwable)
	 */
	public void error(Object pMessage, Throwable pT) {
		logger.error(pMessage, pT);
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.commons.logging.Log#fatal(java.lang.Object)
	 */
	public void fatal(Object pMessage) {
		logger.fatal(pMessage);
		if (!noExitOnFatal) {
			System.exit(EXIT_PARAMETER);
		}
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.commons.logging.Log#fatal(java.lang.Object, java.lang.Throwable)
	 */
	public void fatal(Object pMessage, Throwable pT) {
		logger.fatal(pMessage, pT);
		if (!noExitOnFatal) {
			System.exit(EXIT_PARAMETER);
		}
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.commons.logging.Log#info(java.lang.Object, java.lang.Throwable)
	 */
	public void info(Object pMessage, Throwable pT) {
		logger.info(pMessage, pT);
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.commons.logging.Log#isErrorEnabled()
	 */
	public boolean isErrorEnabled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.commons.logging.Log#isFatalEnabled()
	 */
	public boolean isFatalEnabled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.commons.logging.Log#isTraceEnabled()
	 */
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.commons.logging.Log#isWarnEnabled()
	 */
	public boolean isWarnEnabled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.commons.logging.Log#trace(java.lang.Object)
	 */
	public void trace(Object pMessage) {
		logger.trace(pMessage);
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.commons.logging.Log#trace(java.lang.Object, java.lang.Throwable)
	 */
	public void trace(Object pMessage, Throwable pT) {
		logger.trace(pMessage, pT);
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.commons.logging.Log#warn(java.lang.Object, java.lang.Throwable)
	 */
	public void warn(Object pMessage, Throwable pT) {
		logger.warn(pMessage, pT);
	}

}

