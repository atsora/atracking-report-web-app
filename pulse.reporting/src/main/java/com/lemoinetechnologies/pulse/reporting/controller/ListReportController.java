// Copyright (C) 2009-2023 Lemoine Automation Technologies
//
// SPDX-License-Identifier: EPL-2.0

package com.lemoinetechnologies.pulse.reporting.controller;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.api.EngineException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContextUtils;
import com.lemoinetechnologies.pulse.reporting.domain.Report;
import com.lemoinetechnologies.pulse.reporting.domain.ReportTemplate;
import com.lemoinetechnologies.pulse.reporting.domain.ReportTree;
import com.lemoinetechnologies.pulse.reporting.service.ReportTemplateServiceImpl;
import com.lemoinetechnologies.pulse.reporting.util.BirtEngineUtils;
import com.lemoinetechnologies.pulse.reporting.util.EmitterUtil;
import com.lemoinetechnologies.pulse.reporting.util.Utils;
import com.lemoinetechnologies.pulse.reporting.birt.Configuration;

@Controller
public class ListReportController {

  protected static Logger LOGGER = LogManager.getLogger(ListReportController.class);

  @Resource(name = "reportTemplateService")
  ReportTemplateServiceImpl reportTemplateService;

  @RequestMapping("/")
  public String index() {
    return "redirect:listreport";
  }

  @RequestMapping("/listreport")
  public String listreport(Model model, HttpServletRequest req, HttpServletResponse resp, Locale locale) throws Exception {
    LOGGER.debug("Request to get \"listreport\" page");
    ReportTree reportTree = reportTemplateService.getReportTree();
    model.addAttribute("reportTree", reportTree);
    LOGGER.debug("End of request to get \"listreport\" page");
    return "listreport";
  }

  @RequestMapping("/reportdescription")
  public @ResponseBody
  Report getReportDescription(@RequestParam(value = "reportid", required = true) String reportId, HttpServletRequest req, HttpServletResponse resp,
      Model model, Locale locale) throws NullPointerException, SecurityException, EngineException, IOException, BirtException {
    LOGGER.debug("Request to get description of report template");
    Report report = reportTemplateService.getReportTemplate(reportId);
    LOGGER.debug("End of request to get description of report template");
    return report;
  }
}
