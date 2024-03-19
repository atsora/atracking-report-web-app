// Copyright (C) 2009-2023 Lemoine Automation Technologies
// Copyright (C) 2023 Atsora Solutions
//
// SPDX-License-Identifier: EPL-2.0

package eu.atsora.tracking.reporting.domain;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.atsora.tracking.reporting.birt.Configuration;
import eu.atsora.tracking.reporting.util.Utils;

/**
 * This class represents a viewing session. A Viewing session is a tab in
 * browser that show an output of a report. A viewing session is identified by its
 * id. httpSessionId represents the http session which create this viewing session.
 */
public class ViewingSession implements Comparable<ViewingSession>, AutoCloseable
{
  /**
   * Give state of a viewing session
   */
  public enum State
  {
    /**
     * State of a viewing session when no function related to this session is
     * running, it can be removed from ViewingSessionManager
     */
    UNLOCK,
    /**
     * State of a viewing session when a function related to this session is
     * running, it can not be removed from ViewingSessionManager
     */
    LOCK
  }

  static SimpleDateFormat s_sdf;
  static {
    s_sdf = new SimpleDateFormat(Configuration.SESSION_ID_DATE_FORMAT);
  }

  static Logger LOGGER = LogManager.getLogger(ViewingSession.class);

  String _id;
  public String getId() { return _id; }

  String _httpId;

  String _reportId;
  public String getReportId() { return _reportId; }

  long _timeout;
  public long getTimeout() { return _timeout; }
  public void setTimeout(long timeout) { _timeout = timeout; }

  State _state;
  public State getState() { return _state; }
  public void setState(State state) { _state = state; }

  ReportViewer _reportViewer;
  public void setReportViewer(ReportViewer reportViewer) { _reportViewer = reportViewer; }
  public ReportViewer getReportViewer() { return _reportViewer; }

  // Constructor
  public ViewingSession(String httpSessionId, String reportId)
  {
    super();
    _id = s_sdf.format(new Date()) + "_" + Thread.currentThread().getId() + "_" + Configuration.atomicInteger.getAndIncrement();
    _httpId = httpSessionId.toUpperCase();
    _reportId = reportId;
    _timeout = Configuration.SESSION_TIMEOUT;
    _state = State.UNLOCK;
  }

  /**
   * set state of session to run. If viewing session state is already to run,
   * return false, else return true.
   * 
   * @return <code>true</code> if viewing session state was previously
   *         <code>STOP</code>, <code>false</code> otherwise
   */
  public boolean lock()
  {
    if (_state == State.LOCK) {
      LOGGER.warn("Session " + this + " is already locked.");
      return false;
    }
    _state = State.LOCK;
    return true;
  }

  /**
   * set state of session to stop. If viewing session is already to stop return
   * false, else return true.
   * 
   * @return <code>true</code> if viewing session state was previously
   *         <code>RUN</code>, <code>false</code> otherwise
   */
  public boolean unlock()
  {
    if (_state == State.UNLOCK) {
      LOGGER.warn("Session " + this + " is already unlocked.");
      return false;
    }
    _state = State.UNLOCK;
    return true;
  }

  /**
   * Get path to the folder containing .rptdocument file associated to this
   * viewing session
   * 
   * @return absolute path to the rptdocument file folder
   */
  String getDocumentFolder()
  {
    return Configuration.DOCUMENTS_FOLDER + File.separator + Configuration.PREFIX_SUB_DOC_FOLDER + _httpId + File.separator + _id;
  }

  /**
   * Get path to the .rptdocument file associated to this viewing session
   * 
   * @return absolute path to the rptdocument file folder
   */
  public String getDocumentPath()
  {
    return getDocumentFolder() + File.separator + _reportId + "." + "rptdocument";
  }

  /**
   * Get prefix path to a html output file generated during this viewing session
   * 
   * @return absolute path prefix to the rptdocument file folder
   */
  public String getReportPathPrefix()
  {
    return getReportFolder() + File.separator + _reportId + "_";
  }

  /**
   * Get path to the folder containing output files generated during this
   * viewing session
   * 
   * @return absolute path to the output file folder
   */
  String getReportFolder()
  {
    return Configuration.REPORT_FOLDER + File.separator + Configuration.PREFIX_SUB_REP_FOLDER + _httpId + File.separator + _id;
  }

  /**
   * Get path to a html output file generated during this viewing session
   * 
   * @param pageNumber
   *          : Number of page which is output
   * @return absolute path to the rptdocument file folder
   */
  public String getReportPath(int pageNumber)
  {
    return getReportFolder() + File.separator + _reportId + "_" + pageNumber + ".html";
  }

  /**
   * Get path to the folder containing output files generated and exported
   * during this viewing session
   * 
   * @return absolute path to the export file folder
   */
  String getExportFolder()
  {
    return Configuration.EXPORT_FOLDER + File.separator + Configuration.PREFIX_SUB_EXP_FOLDER + _httpId + File.separator + _id;
  }

  /**
   * Get path to the folder containing reports preview files
   * 
   * @return absolute path to the thumbnail file folder
   */
  String getThumbnailFolder()
  {
    return Configuration.THUMBNAIL_FOLDER; // + File.separator;
  }

  /**
   * Get path to output files generated and exported during this viewing session
   * 
   * @param format
   *          : define format of output report.
   * 
   * @return absolute path to the rptdocument file folder
   */
  public String getExportPath(String format)
  {
    Date date = new Date();
    String outputFile = _reportId + "_" + s_sdf.format(date);
    
    if ("doc".equalsIgnoreCase(format)) {
      outputFile += ".doc";
    }
    else if ("docx".equalsIgnoreCase(format)) {
      outputFile += ".docx";
    }
    else if ("html".equalsIgnoreCase(format)) {
      outputFile += ".html";
    }
    else if ("odp".equalsIgnoreCase(format)) {
      outputFile += ".odp";
    }
    else if ("ods".equalsIgnoreCase(format)) {
      outputFile += ".ods";
    }
    else if ("odt".equalsIgnoreCase(format)) {
      outputFile += ".odt";
    }
    else if ("pdf".equalsIgnoreCase(format)) {
      outputFile += ".pdf";
    }
    else if ("postscript".equalsIgnoreCase(format)) {
      outputFile += ".ps";
    }
    else if ("ppt".equalsIgnoreCase(format)) {
      outputFile += ".ppt";
    }
    else if ("pptx".equalsIgnoreCase(format)) {
      outputFile += ".pptx";
    }
    else if ("xls".equalsIgnoreCase(format)) {
      outputFile += ".xls";
    }
    else if ("xls_atsora".equalsIgnoreCase(format)) {
      outputFile += ".xls";
    }
    else if ("xls_spudsoft".equalsIgnoreCase(format)) {
      outputFile += ".xls";
    }
    else if ("xls_tribix".equalsIgnoreCase(format)) {
      outputFile += ".xls";
    }
    else if ("xlsx".equalsIgnoreCase(format)) {
      outputFile += ".xlsx";
    }
    return getExportFolder() + File.separator + outputFile;
  }

  /**
   * Get path to the folder containing images used in html output file generated
   * during this session
   * 
   * @return absolute path to the export file folder
   */
  public String getImageFolder()
  {
    return Configuration.IMAGE_FOLDER + File.separator + Configuration.PREFIX_SUB_IMG_FOLDER + _httpId + File.separator + _id;
  }

  /**
   * Destructor is used to delete all files associated with this viewing session
   */
  public void finalize()
  {
    try {
      cleanTemporaryFile();
    }
    catch (Exception ex) {
    }
  }

  public void close()
  {
    cleanTemporaryFile();
  }

  public void cleanTemporaryFile()
  {
    LOGGER.info("Cleaning temporary files related to this viewing session: " + _id);
    deleteTemporyFiles(getDocumentFolder());
    deleteTemporyFiles(getImageFolder());
    deleteTemporyFiles(getReportFolder());
    deleteTemporyFiles(getExportFolder());
  }

  /**
   * Delete files create during this viewing session.
   * 
   * @param path
   *          folder path
   */
  private void deleteTemporyFiles(String path)
  {
    File file = new File(path);
    File parent = new File(file.getParent());
    Utils.deleteDirectory(file);
    File[] children = parent.listFiles();
    if ((children == null) || (children.length == 0)) {
      Utils.deleteDirectory(parent);
    }
  }

  @Override
  public String toString()
  {
    return "ViewingSession [id=" + _id + ", httpId=" + _httpId + ", reportName=" + _reportId + ", timeout=" + _timeout + ", state=" + _state + "]";
  }

  @Override
  public int compareTo(ViewingSession viewingSession)
  {
    if ((viewingSession._id.equals(_id)) && (viewingSession._httpId.equals(_httpId))) {
      return 0;
    }

    try {
      Date date1 = s_sdf.parse(_id);
      Date date2 = s_sdf.parse(viewingSession._id);
      return date1.before(date2) ? -1 : 1;
    }
    catch (ParseException e) {
      LOGGER.error(Utils.getStackTraceAsString(e));
      return 1;
    }
  }
}
