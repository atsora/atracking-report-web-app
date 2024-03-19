// Copyright (C) 2009-2023 Lemoine Automation Technologies
// Copyright (C) 2023 Atsora Solutions
//
// SPDX-License-Identifier: EPL-2.0

package eu.atsora.tracking.reporting.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import eu.atsora.tracking.reporting.birt.Configuration;

/*
 * This controller is not fully realized. It will be used to customized
 * Birt engine settings at runtime
 * 
 */
@Controller
public class ConfigurationController {
  
	protected static Logger LOGGER = LogManager.getLogger(ConfigurationController.class);

	@RequestMapping("/configuration")
	public String configuration(Model model) {
		// Ne fonctionne plus => nécessité d'énumérer chaque élément de la configuration dans une variable séparée et d'adapter configuration.jsp
		//model.addAttribute("configuration", Configuration);
		return "configuration";
	}

	@RequestMapping("/configuration/reload")
	public String reloadconfiguration(Model model, HttpServletRequest req) {
		//model.addAttribute("configuration", Configuration);
		return "configuration";
	}
	
}
