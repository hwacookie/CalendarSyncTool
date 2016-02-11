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
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.log4j.Appender;
import org.apache.log4j.AsyncAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.FileWatchdog;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.net.SocketHubAppender;
import org.apache.log4j.net.SyslogAppender;

/**
 * This class provides initialization of logging components.
 */
public final class LoggingUtility {

	/** inhibit instantiation */
	private LoggingUtility() {
		//
	}

	/** Sets the short hostname as default log id */
	private static final String DEFAULT_LOGID = "localhost";

	/** the log id -> initial the default log id */
	private static String logID = DEFAULT_LOGID;

	/** if true, the hostname will be prepended to all logger names. */
	private static boolean createPrefix;

	/** the log4j properties */
	private static Properties log4jProperties;

	/** the log4j properties prefix */
	private static final String LOG4J_LOGGER_PREFIX = "log4j.logger.";

	/** the log4j.properties file watchdog */
	private static Log4jPropertyWatchdog log4jPropertyWatchdog;

	/** indicated if the logger was already initialized */
	private static boolean initialized = false;

	static {
		try {
			initLog4J();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * load properties.
	 * 
	 * @throws IOException
	 *             if the properties could not be loaded
	 */
	public static final void loadLog4JProperties() throws IOException {
		loadLog4JProperties(false);
	}

	/**
	 * load properties.
	 * 
	 * @param pStartWatchdog
	 *            indicates if the watchdog shall be started or not
	 * 
	 * @throws IOException
	 *             if the properties could not be loaded
	 */
	private static final void loadLog4JProperties(boolean pStartWatchdog) throws IOException {
		final String log4jPropertiesFileName = LoggingUtilityProperties.getLog4JPropertiesFile();

		if (log4jPropertiesFileName != null) {
			File file = new File(log4jPropertiesFileName);
			if (!file.exists()) {
				final URL resource = LoggingUtility.class.getResource(log4jPropertiesFileName);
				if (resource != null) {
					file = new File(resource.getFile());
				}
			}
			if (file.exists()) {
				if (pStartWatchdog) {
					if (log4jPropertyWatchdog == null) {
						log4jPropertyWatchdog = new Log4jPropertyWatchdog(log4jPropertiesFileName);
						log4jPropertyWatchdog.setDelay(Units.MINUTE);
						log4jPropertyWatchdog.start();
					}
				}
				log4jProperties = new Properties();
				log4jProperties.load(new FileInputStream(file));
				setLog4jLevel();
			}
		}
	}

	/**
	 * Create a subcategory of HMSCII using Host and Node name
	 * 
	 * @param aName
	 *            Category name
	 * 
	 * @return A Logger with name
	 *         <code>{hostName}/{nodeName}.{cassName - de.biotronik.}</code>
	 */
	static final org.apache.log4j.Logger createCategory(String aName) {

		String categoryName = aName;
		if (createPrefix) {

			final String staticName = "";

			if (aName.startsWith("de.biotronik.")) {
				categoryName = staticName + aName.substring("de.biotronik.".length());
			} else {
				categoryName = staticName + aName;
			}

		}
		final String customizedCategoryName = logID + "." + categoryName;
		final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(customizedCategoryName);

		return logger;
	}

	/**
	 * Create a subcategory of HMSCII using Host and Node name
	 * 
	 * @param aClass
	 *            A class for which category should be created
	 * 
	 * @return A Logger with name
	 *         <code>{hostName}/{nodeName}.{cassName - de.biotronik.}</code>
	 */
	public static final org.apache.log4j.Logger createCategory(Class<?> aClass) {
		return createCategory(aClass.getName());
	}

	/**
	 * Configure log4j
	 * 
	 * @throws IOException
	 *             if the properties could not be loaded
	 */
	private static final void initLog4J() throws IOException {
		final org.apache.log4j.Logger root = org.apache.log4j.Logger.getRootLogger();
		final String level = LoggingUtilityProperties.getLogLevel();
		String host = LoggingUtilityProperties.getLoghost();
		final int port = LoggingUtilityProperties.getLogport();
		createPrefix = LoggingUtilityProperties.isUsePrefix();
		root.setLevel(Level.toLevel(level));

		try {
			InetAddress.getByName(host);
		} catch (final UnknownHostException e1) {
			System.err.println("Unknown host: " + host);
			System.err.println("The socket appender will be disabled");
			host = "";
		}

		// do log4j server logging except when explicitly disabled by specifying
		// invalid property data
		Appender appender = null;
		if ((host.length() > 0) && (port > 0)) {

			switch (LoggingUtilityProperties.getLogAppender()) {

			case ASYNC:
				final AsyncAppender asyncAppender = new AsyncAppender();
				asyncAppender.setBufferSize(LoggingUtilityProperties.getAsyncBufferSize());
				asyncAppender.addAppender(new SocketAppender(host, port));
				appender = asyncAppender;
				break;

			case HUB:
				appender = new SocketHubAppender(port);
				break;

			case NONE:
				break;

			case STANDARD:
			default:
				appender = new SocketAppender(host, port);
				break;
			}

			if (appender != null) {
				appender.setName("localhost");
				root.addAppender(appender);
			}
		}

		// append to logfile if wanted
		final String logfile = LoggingUtilityProperties.getLogfile();

		if ((logfile != null) && (logfile.length() > 0)) {
			try {
				final Layout layout = new PatternLayout("%d{DATE} [%p] %c %C{1} %m %n");
				final FileAppender fAppender = new FileAppender(layout, logfile);
				root.addAppender(fAppender);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		// do syslog logging if enabled
		String syslogHost = LoggingUtilityProperties.getSysloghost();
		try {
			InetAddress.getByName(syslogHost);
		} catch (final UnknownHostException e1) {
			System.err.println("Unknown host: " + syslogHost);
			System.err.println("The SysLog appender will be disabled");
			syslogHost = null;
		}
		if ((syslogHost != null) && (syslogHost.length() > 0)) {
			final SyslogAppender syslogAppender = new SyslogAppender();

			String fac = LoggingUtilityProperties.getSyslogFacility();
			if ((fac == null) || (fac.length() <= 0)) {
				fac = "LOCAL0";
			}

			syslogAppender.setFacility(fac);
			syslogAppender.setName("syslog-" + fac);
			syslogAppender.setSyslogHost(syslogHost);

			final Layout layout = new PatternLayout("%d{DATE} [%p] %c %C{1} %m %n");
			syslogAppender.setLayout(layout);

			if ((appender != null) && (appender instanceof AsyncAppender)) {
				final AsyncAppender asyncAppender = (AsyncAppender) appender;
				asyncAppender.addAppender(syslogAppender);
			} else {
				root.addAppender(syslogAppender);
			}
		}
		// load logger configuration based on the properties file. has highest
		// priority.
		loadLog4JProperties(true);
		initialized = true;
	}

	/**
	 * Sets the logID.
	 * 
	 * @param aLogID
	 *            The logID to set
	 */
	public static void setLogID(String aLogID) {
		if (logID.equals(DEFAULT_LOGID)) {
			logID = aLogID;
			// set the category levels with the new logID
			setLog4jLevel();
		}
	}

	/**
	 * Returns the logID.
	 * 
	 * @return String
	 */
	public static String getLogID() {
		return logID;
	}

	/**
	 * sets the log level for all categories declared in log4j.properties for
	 * 'categoryName' and 'logID.categoryName'
	 * 
	 */
	private static void setLog4jLevel() {
		for (final Object log4jCategoryObject : log4jProperties.keySet()) {
			final String log4jCategory = (String) log4jCategoryObject;
			if (log4jCategory.startsWith(LOG4J_LOGGER_PREFIX)) {
				final String categoryName = log4jCategory.substring(LOG4J_LOGGER_PREFIX.length());
				final Level level = Level.toLevel((String) log4jProperties.get(log4jCategory));
				final Logger loggerOriginal = LogManager.getLogger(categoryName);
				final Logger loggerWithId = LogManager.getLogger(logID + "." + categoryName);
				loggerOriginal.setLevel(level);
				loggerWithId.setLevel(level);
			}
		}
	}

	/**
	 * Watchdog for log4j properties.
	 * 
	 */
	static class Log4jPropertyWatchdog extends FileWatchdog {

		/**
		 * Constructor of Log4jPropertyWatchdog.
		 * 
		 * @param pFilename
		 *            the file to watch
		 */
		protected Log4jPropertyWatchdog(String pFilename) {
			super(pFilename);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.apache.log4j.helpers.FileWatchdog#doOnChange()
		 */
		@Override
		protected void doOnChange() {

			if (initialized) {
				final PropertyConfigurator propertyConfigurator = new PropertyConfigurator();
				// reload configuration
				try {
					loadLog4JProperties(false);
				} catch (final IOException e) {
					LogManager.getRootLogger().error("Something bad happened while loading the log4j properties", e);
				}
				propertyConfigurator.doConfigure(filename, LogManager.getLoggerRepository());
				// reset the level of each logger
				setLog4jLevel();
			}

		}

	}

}
