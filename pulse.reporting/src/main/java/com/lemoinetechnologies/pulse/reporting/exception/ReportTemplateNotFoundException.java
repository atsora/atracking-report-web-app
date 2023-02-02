// Copyright (C) 2009-2023 Lemoine Automation Technologies
//
// SPDX-License-Identifier: EPL-2.0

package com.lemoinetechnologies.pulse.reporting.exception;

public class ReportTemplateNotFoundException extends Exception {

	private static final long serialVersionUID = 7466227581652072637L;

	public ReportTemplateNotFoundException () {
		super();
	}
	
	public ReportTemplateNotFoundException (String message) {
		super(message);
	}

	public ReportTemplateNotFoundException (String message, Throwable cause) {
		super(message, cause);
	}

}
