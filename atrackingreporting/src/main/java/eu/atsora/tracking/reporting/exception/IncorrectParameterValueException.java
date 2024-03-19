// Copyright (C) 2009-2023 Lemoine Automation Technologies
// Copyright (C) 2023 Atsora Solutions
//
// SPDX-License-Identifier: EPL-2.0

package eu.atsora.tracking.reporting.exception;

import java.util.Locale;

import eu.atsora.tracking.reporting.messages.I18nPulseReporting;

public class IncorrectParameterValueException extends Exception {

	private static final long serialVersionUID = 1313016153186342300L;

	public IncorrectParameterValueException(Locale locale) {
		super(I18nPulseReporting.getString("incorrectparametersvalueserrormsg", locale));
	}

	public IncorrectParameterValueException(Throwable cause, Locale locale) {
		super(I18nPulseReporting.getString("incorrectparametersvalueserrormsg", locale), cause);
	}

}
