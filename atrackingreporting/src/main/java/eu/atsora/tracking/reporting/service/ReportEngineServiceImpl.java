// Copyright (C) 2009-2023 Lemoine Automation Technologies
// Copyright (C) 2023 Atsora Solutions
//
// SPDX-License-Identifier: EPL-2.0

package eu.atsora.tracking.reporting.service;

import java.io.File;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.data.engine.api.DataEngine;
import org.eclipse.birt.report.engine.api.EmitterInfo;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IRenderTask;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunTask;
import org.eclipse.birt.report.engine.api.TOCNode;
import org.springframework.stereotype.Service;
import eu.atsora.tracking.reporting.birt.BirtEngineHelper;
import eu.atsora.tracking.reporting.birt.Configuration;
import eu.atsora.tracking.reporting.birt.MyHTMLActionHandler;
import eu.atsora.tracking.reporting.domain.ReportViewer;
import eu.atsora.tracking.reporting.domain.TableOfContent;
import eu.atsora.tracking.reporting.domain.ViewingSession;
import eu.atsora.tracking.reporting.domain.ViewingSessionManager;
import eu.atsora.tracking.reporting.util.BirtEngineUtils;
import eu.atsora.tracking.reporting.util.EmitterUtil;

@Service("reportEngineService")
public class ReportEngineServiceImpl {
  static Logger LOGGER = LogManager.getLogger(ReportEngineServiceImpl.class);

  @SuppressWarnings("unchecked")
  public int runReport(ReportViewer reportviewer, Locale locale) throws EngineException, BirtException, ParseException
  {
    LOGGER.debug("Starting running report : " + reportviewer.toString());
    IRunTask runTask = null;
    IReportDocument reportDocument = null;
    ViewingSession session = null;
    try {
      // Open the report
      String reportpath = reportviewer.getFilePath();
      IReportRunnable reportRun = BirtEngineHelper.getReportEngine().openReportDesign(reportpath);

      runTask = BirtEngineHelper.getReportEngine().createRunTask(reportRun);
      runTask.setLocale(locale);
      runTask.getAppContext().put(DataEngine.DATA_SET_CACHE_ROW_LIMIT, -1);

      // set parameters values in the IRunTask with values contains in
      // ReportViewForSlideShow
      BirtEngineUtils.setValueInEngineTask(runTask, reportviewer);

      // getpath where rptdocument file should be created
      ViewingSessionManager sessionmanager = ViewingSessionManager.getInstance();
      session = sessionmanager.getSession(reportviewer.getViewingSessionId());
      session.lock();
      String documentPath = session.getDocumentPath();
      runTask.run(documentPath);

      if (runTask.getStatus() != IRunTask.STATUS_SUCCEEDED) {
        String msg = "Session : " + reportviewer.getViewingSessionId() + " - Running report template " + reportviewer.getId()
            + " failed with status " + BirtEngineUtils.getTaskStatus(runTask.getStatus());
        for (Object obj : runTask.getErrors()) {
          msg += "\n\t- " + obj.toString();
        }
        LOGGER.warn(msg);
        runTask.cancel();
        runTask.close();
      }
      runTask.close();
      reportDocument = BirtEngineHelper.getReportEngine().openReportDocument(documentPath);
      reportDocument.close();
      LOGGER.debug("End running report");
      return runTask.getStatus();
    }
    finally {
      ViewingSessionManager.getInstance().refresh(session.getId(), session.getTimeout());
      if (session != null) {
        session.unlock();
      }
      if (runTask != null) {
        runTask.close();
        runTask = null;
      }
      if (reportDocument != null) {
        reportDocument.close();
        reportDocument = null;
      }
    }
  }

  /**
   * generate an html file for given page number of report
   * 
   * @param rv
   *          represent a view of report
   * @param pageNumber
   *          page number to be generate
   * @param imageUrl
   *          base image url use in html option
   * @return : total page of report render
   * @throws BirtException
   * @throws EngineException
   */
  @SuppressWarnings("unchecked")
  public int renderHtmlPage(String webAppBaseUrl, ReportViewer reportviewer, int pagenumber, String baseurl, String imageurl, Locale locale)
      throws EngineException, BirtException {

    IReportDocument reportDocument = null;
    IRenderTask renderTask = null;
    HTMLRenderOption htmlOption = null;

    LOGGER.debug(" Starting rendering report in html format: report="+reportviewer.toString()+", page number="+pagenumber);
    ViewingSession session = ViewingSessionManager.getInstance().getSession(reportviewer.getViewingSessionId());
    session.lock();
    String documentPath = session.getDocumentPath();
    String reportPath = session.getReportPath(pagenumber);
    EmitterInfo ei = EmitterUtil.getEmitterInfo("html");
    try {
      reportDocument = BirtEngineHelper.getReportEngine().openReportDocument(documentPath);
      renderTask = BirtEngineHelper.getReportEngine().createRenderTask(reportDocument);
      LOGGER.debug("RenderTask created");
      renderTask.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, Thread.currentThread().getContextClassLoader());
      htmlOption = new HTMLRenderOption();
      htmlOption.setEmitterID(ei.getID());
      htmlOption.setEmbeddable(false);
      htmlOption.setImageDirectory(session.getImageFolder());
      htmlOption.setSupportedImageFormats("PNG;GIF;JPG;BMP");
      htmlOption.setHtmlPagination(true);
      htmlOption.setMasterPageContent(true);
      htmlOption.setImageHandler(new HTMLServerImageHandler());
      htmlOption.setBaseImageURL(imageurl);
      htmlOption.setBaseURL(baseurl);
      htmlOption.setOutputFormat(IRenderOption.OUTPUT_FORMAT_HTML);
      htmlOption.setActionHandler(new MyHTMLActionHandler(webAppBaseUrl));
      htmlOption.closeOutputStreamOnExit(true);
      htmlOption.setOutputFileName(reportPath);
      renderTask.setRenderOption(htmlOption);
      renderTask.setPageNumber(pagenumber);
      renderTask.setLocale(locale);
      LOGGER.debug("Starting rendering with report engine.");
      renderTask.render();
      if (renderTask.getStatus() != IRunTask.STATUS_SUCCEEDED) {
        String msg = "Session : " + reportviewer.getViewingSessionId() + " - Rendering report template " + reportviewer.getId()
            + " failed with status " + BirtEngineUtils.getTaskStatus(renderTask.getStatus());
        for (Object obj : renderTask.getErrors()) {
          msg += "\n\t- " + obj.toString();
        }
        LOGGER.warn(msg);
      }
      LOGGER.debug("End rendering html page : report=" + reportviewer.toString() + ", page number=" + pagenumber + ", output file path=" + reportPath);
      return renderTask.getStatus();
    }
    finally {
      ViewingSessionManager.getInstance().refresh(session.getId(), session.getTimeout());
      session.unlock();
      if (renderTask != null) {
        renderTask.close();
      }
      if (reportDocument != null) {
        reportDocument.close();
      }
      if (htmlOption != null) {
        htmlOption = null;
      }
    }
  }

  /**
   * Generate a report in a given format. This function is used to export report
   * 
   * @param reportviewer
   *          ReportView object
   * @param pageRange
   *          Page range for
   * @param format
   *          format to render report
   * @param imageUrl
   *          URL of image contained in the report. It is only used for html
   *          format
   * @param locale
   *          locale to render the report
   * @param sc
   *          Servlet Context
   * @return file path of rendered report
   * @throws BirtException
   * @throws EngineException
   * @throws Exception
   */
  public int renderReport(String webAppBaseUrl, ReportViewer reportviewer, String outputfilepath, String pageRange, String format, String baseUrl,
      String imageUrl, Locale locale) throws EngineException, BirtException {

    IReportDocument reportDocument = null;
    IRenderTask renderTask = null;
    LOGGER.debug("Starting rendering report: report="+reportviewer.toString()+", pageRange="+pageRange+", format="+format+", output file path="+outputfilepath);
    ViewingSession session = ViewingSessionManager.getInstance().getSession(reportviewer.getViewingSessionId());
    session.lock();
    try {
      reportDocument = BirtEngineHelper.getReportEngine().openReportDocument(session.getDocumentPath());
      renderTask = BirtEngineHelper.getReportEngine().createRenderTask(reportDocument);
      BirtEngineUtils.setRenderOption(renderTask, format, outputfilepath, baseUrl, webAppBaseUrl, imageUrl, session.getImageFolder());
      renderTask.setPageRange(pageRange);
      renderTask.setLocale(locale);
      renderTask.render();
      if (renderTask.getStatus() != IRunTask.STATUS_SUCCEEDED) {
        String msg = "Session : " + reportviewer.getViewingSessionId() + " - Rendering report template " + reportviewer.getId()
            + " failed with status " + BirtEngineUtils.getTaskStatus(renderTask.getStatus());
        for (Object obj : renderTask.getErrors()) {
          msg += "\n\t- " + obj.toString();
        }
        LOGGER.warn(msg);
      }
      LOGGER.debug("End rendering report : report="+reportviewer.toString()+", pageRange="+pageRange+", format="+format+", output file path="+outputfilepath);
      return renderTask.getStatus();
    }
    finally {
      if (reportDocument != null) {
        reportDocument.close();
      }
      if (renderTask != null) {
        renderTask.close();
      }
      ViewingSessionManager.getInstance().refresh(session.getId(), session.getTimeout());
      session.unlock();
    }
  }

  /**
   * Set table of content for a ReportView
   * 
   * @param rv
   * @throws BirtException
   * @throws EngineException
   * @throws Exception
   * 
   * @throws MyBirtException
   */
  public void buildTableOfContentForReport(ReportViewer reportviewer, Locale locale) throws EngineException, BirtException {
    IReportDocument reportDocument = null;
    IRenderTask renderTask = null;
    try {
      ViewingSession session = ViewingSessionManager.getInstance().getSession(reportviewer.getViewingSessionId());

      reportDocument = BirtEngineHelper.getReportEngine().openReportDocument(session.getDocumentPath());

      renderTask = BirtEngineHelper.getReportEngine().createRenderTask(reportDocument);

      renderTask.setLocale(locale);
      TOCNode tocNode = renderTask.getTOCTree().getRoot();
      LOGGER.debug("Root TOCNode : " + tocNode.getNodeID() + " - " + tocNode.getDisplayString() + 
        " - " + tocNode.getBookmark() + " - " + tocNode.getChildren().size());
      reportviewer.setToc(buildTableOfContent(tocNode, 0, reportDocument));
    }
    finally {
      if (reportDocument != null) {
        reportDocument.close();
      }
      if (renderTask != null) {
        renderTask.close();
      }
    }
  }

  /**
   * recursive function which return bookmark's hierarchy of generated report
   * 
   * @param tn
   *          : bookmark's root node of report
   * @param level
   *          : variable use for recursive call
   * @param reportDocument
   *          : ReportDocument object associated to a report
   * 
   * @return return table of content which represent bookmark's hierarchy of
   *         report
   */
  private TableOfContent buildTableOfContent(TOCNode tn, int level, IReportDocument reportDocument) {
    TableOfContent toc = null;
    if (tn.getBookmark() == null) {
      toc = new TableOfContent(tn.getNodeID(), tn.getDisplayString(), tn.getBookmark(), 0);
    }
    else {
      toc = new TableOfContent(tn.getNodeID(), tn.getDisplayString(), tn.getBookmark(), (int) reportDocument.getPageNumber(tn.getBookmark()));
    }
    LOGGER.debug(toc.toString());
    List<?> children = tn.getChildren();
    if ((children != null) && (children.size() > 0)) {
      for (int j = 0; j < children.size(); j++) {
        toc.addChild(buildTableOfContent((TOCNode) children.get(j), level + 1, reportDocument));
      }
    }
    return toc;
  }

  public String getBaseUrl(HttpServletRequest request) {
    if ((request.getServerPort() == 80) || (request.getServerPort() == 443))
      return request.getScheme() + "://" + request.getServerName() + request.getContextPath();
    else
      return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
  }

  public String getImageUrl(HttpServletRequest request, String imageFolder) {
    String url = request.getRequestURL().toString();
    int i = url.lastIndexOf("/");
    String imageUrl = url.substring(0, i + 1) + "downloadimage?source=" + imageFolder + "\\";
    imageUrl = imageUrl.replaceAll("\\\\", "/");
    return imageUrl;
  }

  @SuppressWarnings("unchecked")
  public void renderReportPerPage(String webAppBaseUrl, ReportViewer reportviewer, String baseUrl, String imageUrl, Locale locale)
      throws EngineException, BirtException, ParseException {
    
    LOGGER.debug("Starting rendering report: report=" + reportviewer.toString());
    ViewingSessionManager sessionmanager = ViewingSessionManager.getInstance();
    ViewingSession session = sessionmanager.getSession(reportviewer.getViewingSessionId());
    session.lock();
    IReportDocument reportDocument = null;
    IRenderTask renderTask = null;
    HTMLRenderOption htmlOption = null;
    String documentPath = session.getDocumentPath();
    String reportPath;
    try {
      reportDocument = BirtEngineHelper.getReportEngine().openReportDocument(documentPath);
      int totalpage = (int) reportDocument.getPageCount();
      for (int i = 1; i <= totalpage; i++)
      {
        reportPath = session.getReportPath(i);
        htmlOption = new HTMLRenderOption();
        htmlOption.setEmbeddable(false);
        htmlOption.setImageDirectory(session.getImageFolder());
        htmlOption.setSupportedImageFormats("PNG;GIF;JPG;BMP");
        htmlOption.setHtmlPagination(true);
        htmlOption.setMasterPageContent(true);
        htmlOption.setImageHandler(new HTMLServerImageHandler());
        htmlOption.setBaseImageURL(imageUrl);
        htmlOption.setBaseURL(baseUrl);
        htmlOption.setOutputFormat(IRenderOption.OUTPUT_FORMAT_HTML);
        htmlOption.setActionHandler(new MyHTMLActionHandler(webAppBaseUrl));
        htmlOption.closeOutputStreamOnExit(true);
        htmlOption.setOutputFileName(reportPath);
        renderTask = BirtEngineHelper.getReportEngine().createRenderTask(reportDocument);
        renderTask.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, Thread.currentThread().getContextClassLoader());
        renderTask.setRenderOption(htmlOption);
        renderTask.setLocale(locale);
        renderTask.setPageNumber(i);
        renderTask.render();
        if (renderTask.getStatus() != IRunTask.STATUS_SUCCEEDED) {
          String msg = "Session : " + reportviewer.getViewingSessionId() + " - Rendering report template " + reportviewer.getId()
              + " failed with status " + BirtEngineUtils.getTaskStatus(renderTask.getStatus());
          for (Object obj : renderTask.getErrors()) {
            msg += "\n\t- " + obj.toString();
          }
          LOGGER.warn(msg);
        }
        renderTask.close();
      }
      LOGGER.debug("End rendering report: report=" + reportviewer.toString());
    }
    finally {
      if (renderTask != null) {
        renderTask.close();
      }
      if (reportDocument != null) {
        reportDocument.close();
      }
      if (htmlOption != null) {
        htmlOption = null;
      }
      ViewingSessionManager.getInstance().refresh(session.getId(), session.getTimeout());
      session.unlock();
    }
  }
}
