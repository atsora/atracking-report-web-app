// Copyright (C) 2009-2023 Lemoine Automation Technologies
//
// SPDX-License-Identifier: EPL-2.0

package com.lemoinetechnologies.pulse.reporting.exception;


import java.util.Locale;

import com.lemoinetechnologies.pulse.reporting.messages.I18nPulseReporting;

public class InvalidPageRangeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8790964966001804399L;

	public InvalidPageRangeException(Locale locale) {
		super(I18nPulseReporting.getString("invalidpagerangeerrormsg", locale));
	}

	public InvalidPageRangeException(Throwable cause,Locale locale) {
		super(I18nPulseReporting.getString("invalidpagerangeerrormsg", locale), cause);
	}

}
