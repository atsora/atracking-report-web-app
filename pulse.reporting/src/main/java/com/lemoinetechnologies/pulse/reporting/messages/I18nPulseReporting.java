// Copyright (C) 2009-2023 Lemoine Automation Technologies
//
// SPDX-License-Identifier: EPL-2.0

package com.lemoinetechnologies.pulse.reporting.messages;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.lemoinetechnologies.pulse.reporting.util.Utils;

public class I18nPulseReporting {

	static Logger logger = LogManager.getLogger(I18nPulseReporting.class.getName());

	public static String getString(String key, Locale locale) {
		try {
			ResourceBundle messages = ResourceBundle
					.getBundle(
							"com.lemoinetechnologies.pulse.reporting.messages.messages",
							locale);
			return new String(messages.getString(key).getBytes("ISO-8859-1"),"UTF-8");
		} catch (Exception e) {
			logger.error("keyword : " + key+"\n"+Utils.getStackTraceAsString(e));
			return "???"+key+"???";
		} 
	}
}
