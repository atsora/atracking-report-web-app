// Copyright (C) 2009-2023 Lemoine Automation Technologies
// Copyright (C) 2023 Atsora Solutions
//
// SPDX-License-Identifier: EPL-2.0

package eu.atsora.tracking.reporting.controller;

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
import eu.atsora.tracking.reporting.domain.Report;
import eu.atsora.tracking.reporting.domain.ReportTemplate;
import eu.atsora.tracking.reporting.domain.ReportTree;
import eu.atsora.tracking.reporting.service.ReportTemplateServiceImpl;
import eu.atsora.tracking.reporting.util.BirtEngineUtils;
import eu.atsora.tracking.reporting.util.EmitterUtil;
import eu.atsora.tracking.reporting.util.Utils;
import eu.atsora.tracking.reporting.birt.Configuration;

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
