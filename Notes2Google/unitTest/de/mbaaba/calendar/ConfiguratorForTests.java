/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.calendar;

import java.util.Properties;

import de.mbaaba.util.CommConfigUtil;
import de.mbaaba.util.Configurator;

/**
 * The Class ConfiguratorForTests.
 */
public class ConfiguratorForTests implements Configurator {

	Properties props = new Properties();

	public ConfiguratorForTests() {
		setProperty(CommConfigUtil.PROP_HTTP_PROXY_HOST, "10.0.13.240");
		setProperty(CommConfigUtil.PROP_HTTP_PROXY_PORT, "4834");
		setProperty(CommConfigUtil.PROP_PROXY_SET, "true");
		setProperty(CommConfigUtil.PROP_TRUST_ALL_HOSTNAMES, "true");
		setProperty(CommConfigUtil.PROP_HTTPS_PROXY_HOST, "10.0.13.240");
		setProperty(CommConfigUtil.PROP_HTTPS_PROXY_PORT, "4834");
		setProperty(CommConfigUtil.PROP_TRUST_ALL_HTTPS_CERTIFICATES, "true");
	}

	
	@Override
	public String getProperty(String aPropertyName, String aDefaultValue) {
		final String property = props.getProperty(aPropertyName);
		if (property == null) {
			System.err.println(aPropertyName + "=" + aDefaultValue);
			setProperty(aPropertyName, aDefaultValue);
			return aDefaultValue;
		}
		return property;
	}

	
	@Override
	public int getProperty(String aPropertyName, int aDefaultValue) {
		final String s = props.getProperty(aPropertyName);
		if (s == null) {
			System.err.println(aPropertyName + "=" + aDefaultValue);
			setProperty(aPropertyName, "" + aDefaultValue);
			return aDefaultValue;
		}
		return Integer.valueOf(s);
	}

	
	@Override
	public void setProperty(String aPropertyName, String aValue) {
		props.put(aPropertyName, aValue);
	}
}
