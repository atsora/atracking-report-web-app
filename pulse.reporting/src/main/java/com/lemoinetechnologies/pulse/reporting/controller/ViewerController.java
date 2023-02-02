// Copyright (C) 2009-2023 Lemoine Automation Technologies
//
// SPDX-License-Identifier: EPL-2.0

package com.lemoinetechnologies.pulse.reporting.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.birt.report.engine.api.IRenderTask;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.lemoinetechnologies.pulse.reporting.birt.BirtEngineHelper;
import com.lemoinetechnologies.pulse.reporting.domain.*;
import com.lemoinetechnologies.pulse.reporting.exception.IncorrectParameterValueException;
import com.lemoinetechnologies.pulse.reporting.exception.InvalidPageRangeException;
import com.lemoinetechnologies.pulse.reporting.exception.ReportTemplateNotFoundException;
import com.lemoinetechnologies.pulse.reporting.messages.I18nPulseReporting;
import com.lemoinetechnologies.pulse.reporting.service.ReportEngineServiceImpl;
import com.lemoinetechnologies.pulse.reporting.service.ReportTemplateServiceImpl;
import com.lemoinetechnologies.pulse.reporting.util.BirtEngineUtils;
import com.lemoinetechnologies.pulse.reporting.util.EmitterUtil;
import com.lemoinetechnologies.pulse.reporting.util.Utils;
import com.lemoinetechnologies.pulse.reporting.birt.Configuration;

@Controller
public class ViewerController
{
  protected static Logger LOGGER = LogManager.getLogger(ViewerController.class);

  @Resource(name = "reportEngineService")
  ReportEngineServiceImpl reportEngineService;

  @Resource(name = "reportTemplateService")
  ReportTemplateServiceImpl reportTemplateService;
  
  @SuppressWarnings("unchecked")
  @RequestMapping("/viewer")
  public String viewer(Locale locale, HttpServletRequest req, Model model)
  {
    // Specify the different formats available for an export
    model.addAttribute("availableFormat", EmitterUtil.getAvailableFormat());
    return "viewer";
  }

  /*
    Run the report. Possible errors:
    -  0: no errors
    -  1: report not specified (__report parameter in the query)
    -  2: report not found (__report parameter in the query)
    -  3: invalid parameters
    - 99: unknown error
  */
  @SuppressWarnings("unchecked")
  @RequestMapping("/runreport")
  public @ResponseBody
  Map<String, String> runReport(Locale locale, HttpServletRequest req, Model model)
  {
    // Prepare the result
    Map<String, String> result = new TreeMap<String, String>();
    result.put("errornumber", "0");
    result.put("error", "");
    result.put("sessionid", "");
    result.put("reporttitle", "");
    result.put("reportid", "");
    result.put("querystring", "");
    result.put("totalpage", "0");
    
    Triplet<ViewingSession, Integer, String> sessionWithError = GetOrBuildSessionWithReportViewer(locale, req);
    if (sessionWithError.second() != 0) {
      result.put("errornumber", Integer.toString(sessionWithError.second()));
      result.put("error", sessionWithError.third());
    } else {
      try {
        ViewingSession session = sessionWithError.first();

        // Create a rptdocument file
        IReportDocument reportDocument = BirtEngineHelper.getReportEngine().openReportDocument(session.getDocumentPath());
  
        // Build the table of content
        ReportViewer reportViewer = session.getReportViewer();
        reportEngineService.buildTableOfContentForReport(reportViewer, locale);
        reportViewer.setTotalPage((int) reportDocument.getPageCount());
  
        // Fill the properties
        result.put("sessionid", session.getId());
        result.put("reporttitle", reportViewer.getTitle());
        result.put("reportid", session.getReportId());
        result.put("querystring", reportViewer.getParametersQueryString(false));
        result.put("totalpage", Integer.toString((int) reportDocument.getPageCount()));
      } catch (Exception e) {
        LOGGER.error(Utils.getStackTraceAsString(e));
        result.put("error", e.getClass().getSimpleName());
        result.put("errornumber", "99");
      }
    }

    return result;
  }

  // Get an existing session if the session id is specified or create a session and associate a report viewer
  Triplet<ViewingSession, Integer, String> GetOrBuildSessionWithReportViewer(Locale locale, HttpServletRequest req)
  {
    // Results
    ViewingSession session = null;

    try {
      String sessionId = "";

      // Extract parameters from the request
      TreeMap<String, String[]> parameterMap = new TreeMap<String, String[]>();
      Enumeration<String> parameters = req.getParameterNames();
      while (parameters.hasMoreElements()) {
        String parameter = parameters.nextElement();
        if ("__session".equals(parameter)) {
          String[] values = req.getParameterValues(parameter);
          if (values.length == 1)
            sessionId = values[0];
        } else
          parameterMap.put(parameter, req.getParameterValues(parameter));
      }

      // Maybe there is an existing session?
      session = ViewingSessionManager.getInstance().getSession(sessionId);
      if (session != null)
        return new Triplet<ViewingSession, Integer, String>(session, 0, "");

      // Find the mandatory parameter "__report" and process it
      if (!parameterMap.containsKey("__report"))
        return new Triplet<ViewingSession, Integer, String>(null, 1, "report not specified");
      if (parameterMap.get("__report").length != 1)
        return new Triplet<ViewingSession, Integer, String>(null, 1, "report not specified");
      
      String reportId = parameterMap.get("__report")[0];
      reportId = reportId.substring(reportId.replace("\\", "/").lastIndexOf("/") + 1); // Possibly remove the directory
      if (reportId.endsWith(".rptdesign")) // Possibly remove the extension
        reportId = reportId.substring(0, reportId.length() - 10);
      parameterMap.remove("__report");

      // Get the corresponding report template
      ReportTemplate reportTemplate = BirtEngineUtils.getReportTemplate(reportId, parameterMap, locale);
      if (reportTemplate == null)
        return new Triplet<ViewingSession, Integer, String>(null, 2, "report template '" + reportId + "'not found");

      // Create a viewing session for the current report
      ViewingSessionManager sessionmanager = ViewingSessionManager.getInstance();
      session = sessionmanager.createSession(req.getSession().getId(), reportId);

      // Build a ReportViewer instance and link it to the current session
      ReportViewer reportViewer = new ReportViewer(reportTemplate, session.getId());
      session.setReportViewer(reportViewer);

      // Check that report parameters are correct
      String[] invalidsParameters = reportViewer.validateParameterValue();
      if (invalidsParameters != null) {
        String msg;
        if (invalidsParameters.length == 1) {
          msg = "following parameter is not valid: " + invalidsParameters[0];
        } else {
          msg = "following parameters are not valid: ";
          for (int i = 0; i < invalidsParameters.length; i++) {
            msg += (i > 0 ? ", " : "") + invalidsParameters[i];
          }
        }
        return new Triplet<ViewingSession, Integer, String>(null, 3, msg);
      }

      // Run the report
      reportEngineService.runReport(reportViewer, locale);
    } catch (Exception e) {
      return new Triplet<ViewingSession, Integer, String>(null, 99, e.getClass().getSimpleName());
    }
    
    // Return the session and not errors attached
    return new Triplet<ViewingSession, Integer, String>(session, 0, "");
  }

  /*
    Display the toc. Possible errors:
    -  0: no errors
    -  1: session not found
  */
  @SuppressWarnings("unchecked")
  @RequestMapping("/gettoc")
  public String getToc(Locale locale, HttpServletRequest req, Model model)
  {
    // Get the viewing session
    Triplet<ViewingSession, Integer, String> sessionWithError = GetOrBuildSessionWithReportViewer(locale, req);
    ViewingSession session = sessionWithError.first();

    // Maybe there is an error
    model.addAttribute("errornumber", Integer.toString(sessionWithError.second()));
    model.addAttribute("error", sessionWithError.third());
    model.addAttribute("emptytoc", true);
    if (sessionWithError.second() != 0)
      return "fragment/tableofcontent";

    // Check that the toc is not empty
    ReportViewer reportViewer = session.getReportViewer();
    if (reportViewer.getToc().getChildren().size() > 0) {
      model.addAttribute("toclist", reportViewer.getToc().getChildren());
      model.addAttribute("emptytoc", false);
    }

    return "fragment/tableofcontent";
  }

  @SuppressWarnings("unchecked")
  @RequestMapping("/getviewingpage")
  public @ResponseBody
  Map<String, String> getViewingPage(
      @RequestParam(value = "__page", required = true, defaultValue = "1") int pagenumber,
      Locale locale, HttpServletRequest req, Model model)
  {
    // Prepare the result
    Map<String, String> result = new TreeMap<String, String>();
    result.put("outputfilepath", "");
    result.put("sessionid", "");
    
    // Get the viewing session
    Triplet<ViewingSession, Integer, String> sessionWithError = GetOrBuildSessionWithReportViewer(locale, req);
    ViewingSession session = sessionWithError.first();

    // Maybe there is an error
    if (sessionWithError.second() != 0) {
      result.put("error", sessionWithError.third());
      return result;
    }

    try {
      File file = new File(session.getDocumentPath());
      ReportViewer reportViewer = session.getReportViewer();
      String outputfilepath = session.getReportPath(pagenumber);
      file = new File(outputfilepath);
      if (!file.exists()) {
        String baseUrl;
        String imageUrl;
        baseUrl = reportEngineService.getBaseUrl(req);
        imageUrl = reportEngineService.getImageUrl(req, session.getImageFolder());
        reportEngineService.renderHtmlPage(Utils.getWebAppBaseUrl(req), reportViewer, pagenumber, baseUrl, imageUrl, locale);
      }
      result.put("outputfilepath", outputfilepath);
      result.put("sessionid", session.getId());
      LOGGER.debug("Request to get viewing page return output file path: " + outputfilepath);
      return result;
    } catch (Exception e) {
      LOGGER.error("Request to get viewing page return error.\n" + Utils.getStackTraceAsString(e));
      result.put("error", Utils.getStackTraceAsString(e));
      return result;
    }
  }

  @RequestMapping("/getreporttemplate")
  public String getReportTemplate(@RequestParam(value = "__report", required = true) String reportId,
    @RequestParam(value = "__session", required = false, defaultValue = "") String sessionId,
    Locale locale, HttpServletRequest req, Model model)
  {
    // Build the html form for configuring the report parameters
    try {
      // Build a report template
      ReportTemplate reportTemplate = null;

      ViewingSession session = ViewingSessionManager.getInstance().getSession(sessionId);
      if (session == null) {
        // Prepare the parameters
        Map<String, String[]> parameterMap = new TreeMap<String, String[]>();
        Enumeration<String> parameters = req.getParameterNames();
        while (parameters.hasMoreElements()) {
          String parameter = parameters.nextElement();
          if (!"__session".equalsIgnoreCase(parameter))
            parameterMap.put(parameter, req.getParameterValues(parameter));
        }

        // Build a report template
        reportTemplate = BirtEngineUtils.getReportTemplate(reportId, parameterMap, locale);
      } else {
        // Report template from the current session
        reportTemplate = session.getReportViewer();
      }

      model.addAttribute("reportTemplate", reportTemplate);
    } catch (Exception e) {
      LOGGER.error("End of request to get report template with error for report " + reportId + "\n" + Utils.getStackTraceAsString(e));
      model.addAttribute("error_type", e.getClass().getName());
      model.addAttribute("error_msg", Utils.getStackTraceAsString(e));
      model.addAttribute("message", e.getClass().getName() + "\n" + Utils.getStackTraceAsString(e));
      return "test";
    }

    // Fill the .jsp "parametersdialogbox"
    return "parametersdialogbox";
  }

  @RequestMapping("/print")
  public void printreport(
      @RequestParam(value = "__format", required = false, defaultValue = "pdf") String format,
      @RequestParam(value = "__pagerange", required = false) String pagerange,
      Locale locale, HttpServletRequest req, HttpServletResponse resp)
  {
    // Get the viewing session
    Triplet<ViewingSession, Integer, String> sessionWithError = GetOrBuildSessionWithReportViewer(locale, req);

    // Maybe there is an error
    if (sessionWithError.second() != 0)
      return;

    ViewingSession session = sessionWithError.first();
    ReportViewer reportViewer = session.getReportViewer();

    // Possibly create the rptdocument if it's not already done
    if (reportViewer.getTotalPage() == 0) {
      try {
        // Create a rptdocument file
        IReportDocument reportDocument = BirtEngineHelper.getReportEngine().openReportDocument(session.getDocumentPath());
        
        // Build the table of content
        reportEngineService.buildTableOfContentForReport(reportViewer, locale);
        reportViewer.setTotalPage((int) reportDocument.getPageCount());
      } catch (Exception e) {
        LOGGER.error(Utils.getStackTraceAsString(e));
      }
    }
    
    BufferedOutputStream out = null;
    BufferedInputStream in = null;
    try {
      // Check the page range
      if (pagerange != null) {
        if (!Utils.isValidPageRange(pagerange, reportViewer.getTotalPage())) {
          LOGGER.error("Following page range is not valid : " + pagerange);
          throw new InvalidPageRangeException(locale);
        }
      } else {
        pagerange = "1-" + reportViewer.getTotalPage();
      }

      String baseUrl = reportEngineService.getBaseUrl(req);
      String imageUrl = reportEngineService.getImageUrl(req, session.getImageFolder());
      String outputfilepath = session.getExportPath(format);
      int status = reportEngineService.renderReport(
        Utils.getWebAppBaseUrl(req), reportViewer, outputfilepath, pagerange, format, baseUrl, imageUrl, locale);
      if (status == IRenderTask.STATUS_SUCCEEDED) {
        File file = new File(outputfilepath);
        if (!file.exists()) {
          LOGGER.error("Printed file \"" + outputfilepath + "\" not found after rendering!");
          resp.sendError(HttpServletResponse.SC_NOT_FOUND); // 404
        }
        String s = file.getName();
        int index = s.lastIndexOf(".");
        String extension = s.substring(index + 1);
        resp.setContentType(Utils.getContentType(extension));
        resp.setHeader("Content-Disposition", "inline;filename=\"" + file.getName() + "\"");
        resp.setContentLength((int) file.length());
        byte[] buffer = new byte[1024];
        int n;
        out = new BufferedOutputStream(resp.getOutputStream());
        in = new BufferedInputStream(new FileInputStream(file));
        while ((n = in.read(buffer)) != -1) {
          try {
            out.write(buffer, 0, n);
          } catch (Exception e) {}
        }
        try {
          out.flush();
        } catch (Exception e) {}
      } else {
        LOGGER.error("Printing did not succeed. Session: " + session.getId() + ", report template: " + reportViewer.getId()
            + ", status: " + BirtEngineUtils.getTaskStatus(status));
        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
      }
    } catch (Exception e) {
      LOGGER.error("Printing fails for report template " + reportViewer.getId() + ": " + Utils.getStackTraceAsString(e));
      try {
        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
      } catch (IOException e1) {
        LOGGER.error(Utils.getStackTraceAsString(e));
      }
    } finally {
      try {
        if (in != null)
          in.close();
      } catch (IOException e) {
        LOGGER.error(Utils.getStackTraceAsString(e));
      }
      try {
        if (out != null)
          out.close();
      } catch (IOException e) {
        LOGGER.error(Utils.getStackTraceAsString(e));
      }
    }
  }

  @RequestMapping("/export")
  public void exportreport(
      @RequestParam(value = "__format", required = false, defaultValue = "pdf") String format,
      @RequestParam(value = "__pagerange", required = false) String pagerange,
      Locale locale, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    // Get the viewing session
    Triplet<ViewingSession, Integer, String> sessionWithError = GetOrBuildSessionWithReportViewer(locale, req);
    
    // Maybe there is an error
    if (sessionWithError.second() != 0)
      return;
      
    ViewingSession session = sessionWithError.first();
    ReportViewer reportViewer = session.getReportViewer();

    // Possibly create the rptdocument if it's not already done
    if (reportViewer.getTotalPage() == 0) {
      try {
        // Create a rptdocument file
        IReportDocument reportDocument = BirtEngineHelper.getReportEngine().openReportDocument(session.getDocumentPath());
        
        // Build the table of content
        reportEngineService.buildTableOfContentForReport(reportViewer, locale);
        reportViewer.setTotalPage((int) reportDocument.getPageCount());
      } catch (Exception e) {
        LOGGER.error(Utils.getStackTraceAsString(e));
      }
    }

    BufferedOutputStream out = null;
    BufferedInputStream in = null;
    try {
      // Check the page range
      if (pagerange != null) {
        if (!Utils.isValidPageRange(pagerange, reportViewer.getTotalPage())) {
          LOGGER.error("Following page range is not valid : " + pagerange);
          throw new InvalidPageRangeException(locale);
        }
      } else {
        pagerange = "1-" + reportViewer.getTotalPage();
      }

      String baseUrl = reportEngineService.getBaseUrl(req);
      String imageUrl = reportEngineService.getImageUrl(req, session.getImageFolder());
      String outputfilepath = session.getExportPath(format);
      int status = reportEngineService.renderReport(
        Utils.getWebAppBaseUrl(req), reportViewer, outputfilepath, pagerange, format, baseUrl, imageUrl, locale);
      if (status == IRenderTask.STATUS_SUCCEEDED) {
        File file = new File(outputfilepath);
        if (!file.exists()) {
          LOGGER.error("Exported File \"" + outputfilepath + "\" not found after exportation!");
          resp.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
        }
        String s = file.getName();
        int index = s.lastIndexOf(".");
        String extension = s.substring(index + 1);
        resp.setContentType(Utils.getContentTypeDownload(extension));
        resp.setHeader("Content-Disposition", "attachment;filename=\"" + file.getName() + "\"");
        resp.setContentLength((int) file.length());
        byte[] buffer = new byte[1024];
        int n;
        out = new BufferedOutputStream(resp.getOutputStream());
        in = new BufferedInputStream(new FileInputStream(file));
        while ((n = in.read(buffer)) != -1) {
          try {
            out.write(buffer, 0, n);
          } catch (Exception e) {}
        }
        try {
          out.flush();
        } catch (Exception e) {}
      } else {
        String msg = "Could not export report template. Session: " +
          session.getId() + ", report template : " + reportViewer.getId() + ", status : " + BirtEngineUtils.getTaskStatus(status);
        LOGGER.error(msg);
        //resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500.
        RequestDispatcher dispatcher = req.getRequestDispatcher("error");
        req.setAttribute("errormessage", "Could not export report template: " + session.getReportId());
        req.setAttribute("errorstackstrace", StringEscapeUtils.escapeHtml4(msg));
        dispatcher.forward(req, resp);
      }
    } catch (Exception e) {
      LOGGER.error("Could not export report template " + session.getReportId() + ": " + Utils.getStackTraceAsString(e));
      RequestDispatcher dispatcher = req.getRequestDispatcher("error");
      req.setAttribute("errormessage", "Could not export report template: " + session.getReportId());
      req.setAttribute("errorstacktrace", StringEscapeUtils.escapeHtml4(Utils.getStackTraceAsString(e)));
      dispatcher.forward(req, resp);
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException e) {
        LOGGER.error(Utils.getStackTraceAsString(e));
      }
      try {
        if (out != null) {
          out.close();
        }
      } catch (IOException e) {
        LOGGER.error(Utils.getStackTraceAsString(e));
      }
    }
  }
}