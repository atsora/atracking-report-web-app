// Copyright (C) 2009-2023 Lemoine Automation Technologies
//
// SPDX-License-Identifier: EPL-2.0

package com.lemoinetechnologies.pulse.reporting.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class JavascriptLoggerController {
	protected static Logger logger = LogManager.getLogger(JavascriptLoggerController.class);
	
	@RequestMapping(value="/javascriptlogger" )
	public void javascriptLogger( HttpServletRequest req, HttpServletResponse resp, Locale locale) {
		if("info".equals(req.getParameter("level"))) {
			logger.info(req.getParameter("message"));
		}
		else if("warn".equals(req.getParameter("level"))) {
			logger.warn(req.getParameter("message"));
		}
		else if("error".equals(req.getParameter("level"))) {
			logger.error(req.getParameter("message"));
		}
		else{
			logger.debug(req.getParameter("message"));
		}		
	}

}
