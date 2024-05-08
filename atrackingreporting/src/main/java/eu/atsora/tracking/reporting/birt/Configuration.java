// Copyright (C) 2009-2023 Lemoine Automation Technologies
// Copyright (C) 2023 Atsora Solutions
//
// SPDX-License-Identifier: EPL-2.0

package eu.atsora.tracking.reporting.birt;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.atsora.tracking.reporting.domain.ReportTree;
import eu.atsora.tracking.reporting.util.Utils;

public final class Configuration {

  static Logger logger = LogManager.getLogger(Configuration.class);

  /**
   * Application context path 
   */
  public static String CONTEXT_PATH;
  /**
   * Root folder containing report template
   */
  public static String DESIGN_FOLDER;
  /**
   * Folder where are stored rptdocument files
   */
  public static String DOCUMENTS_FOLDER;
  /**
   * Folder where are stored report created by in viewer or slideshow
   */
  public static String REPORT_FOLDER;
  /**
   * Folder where are stored report exported
   */
  public static String EXPORT_FOLDER;
  /**
   * Folder where are stored report previews
   */
  public static String THUMBNAIL_FOLDER;
  /**
   * Folder where are stored image of reports created
   */
  public static String IMAGE_FOLDER;
  /**
   * Folder where are stored logger files of program
   */
  public static String LOG_FOLDER;
  /**
   * Format used to build viewing session id throught the current datetime
   */
  public static String SESSION_ID_DATE_FORMAT;
  /**
   * Prefix of folders created for each http session. Each folder contains
   * sub-folders created by viewing session to store rptdocument files in this
   * http session
   */
  public static String PREFIX_SUB_DOC_FOLDER;
  /**
   * Prefix of folders created for each http session. Each folder contains
   * sub-folders created by viewing session to store report files(HTML) in this
   * http session
   */
  public static String PREFIX_SUB_REP_FOLDER;
  /**
   * Prefix of folders created for each http session. Each folder contains
   * sub-folders created by viewing session to store exported report files in
   * this http session
   */
  public static String PREFIX_SUB_EXP_FOLDER;
  /**
   * Prefix of folders created for each http session. Each folder contains
   * sub-folders created by viewing session to store images of report files in
   * this http session
   */
  public static String PREFIX_SUB_IMG_FOLDER;
  /**
   * Life duration of viewing session, in milliseconds
   */
  public static int SESSION_TIMEOUT;
  /**
   * Log level for BIRT report engine
   */
  public static String BIRT_LOG_LEVEL;
  /**
   * Path to folder where BIRT resources are located
   */
  public static String BIRT_RESOURCE_PATH;
  /**
   * Maximum number of BIRT logger files created before rolling
   */
  public static int BIRT_LOG_MAX_BACKUP_INDEX;
  /**
   * Maximum size of a BIRT logger file
   */
  public static int BIRT_LOG_ROLLING_SIZE;
  /**
   * Default locale used by BIRT
   */
  public static String BIRT_LOCALE;
  /**
   * Web service path, http://hostname:port
   */
  public static String WEB_SERVICE_PATH;
  /**
   * Timeout for calling a web service, in milliseconds
   */
  public static int WEB_SERVICE_TIMEOUT_MS;
  /**
   * integer used to build viewing session and make them different each other
   */
  public static AtomicInteger atomicInteger = new AtomicInteger();

  public static ReportTree reportTree = null;

  private Configuration() {}


  /**
   * Initialize application settings with a properties files
   * 
   * @param properties
   *          properties which define our application settings
   * @param contextPath
   *          context path of our application
   * @throws IOException
   */
  public static void initConfigSettings(Properties properties, String contextPath)
  {
    File file;
    String temp;
    
    logger.debug(displayProperties(properties));
    
    CONTEXT_PATH = contextPath;
    String value = properties.getProperty("DESIGN_FOLDER");
    try {
      if (value == null) {
        value = "";
      }
      temp = value;
      file = new File(temp);
      if (!file.isAbsolute()) {
        temp = contextPath + value;
        file = new File(temp);
      }
      if (file.exists()) {
        DESIGN_FOLDER = file.getCanonicalPath();
      } else {
        DESIGN_FOLDER = contextPath;
      }
    } catch (IOException e) {
      Utils.getStackTraceAsString(e);
    }

    value = properties.getProperty("DOCUMENTS_FOLDER");
    temp = contextPath + ((value == null) ? "documents" : value);
    file = new File(temp);
    DOCUMENTS_FOLDER = file.getAbsolutePath();
    if (!file.exists()) {
      file.mkdir();
    } else {
      Utils.deleteDirectoryContent(file);
    }

    value = properties.getProperty("REPORT_FOLDER");
    temp = contextPath + ((value == null) ? "reports" : value);
    file = new File(temp);
    REPORT_FOLDER = file.getAbsolutePath();
    if (!file.exists()) {
      file.mkdir();
    } else {
      Utils.deleteDirectoryContent(file);
    }

    value = properties.getProperty("EXPORT_FOLDER");
    temp = contextPath + ((value == null) ? "exports" : value);
    file = new File(temp);
    EXPORT_FOLDER = file.getAbsolutePath();
    if (!file.exists()) {
      file.mkdir();
    } else {
      Utils.deleteDirectoryContent(file);
    }

    value = properties.getProperty("THUMBNAIL_FOLDER");
    THUMBNAIL_FOLDER = value;
    if ( value != null && value != "") {
      file = new File(THUMBNAIL_FOLDER);
      if (!file.exists()) {
        if ( !file.mkdir() ){
          logger.warn("Create THUMBNAIL_FOLDER FAILED ("+ value +") - ignore it");
          THUMBNAIL_FOLDER = "";
        }
      }
    }
    
    value = properties.getProperty("IMAGE_FOLDER");
    IMAGE_FOLDER = contextPath + ((value == null) ? "reports_images" : value);
    file = new File(IMAGE_FOLDER);
    if (!file.exists()) {
      file.mkdir();
    } else {
      Utils.deleteDirectoryContent(file);
    }

    value = properties.getProperty("PREFIX_SUB_DOC_FOLDER");
    PREFIX_SUB_DOC_FOLDER = (value == null) ? "PWR_DOC" : value;

    value = properties.getProperty("WEB_SERVICE_PATH");
    WEB_SERVICE_PATH = (value == null) ? "http://lctr:5000/" : value;
    if (!WEB_SERVICE_PATH.endsWith("/"))
      WEB_SERVICE_PATH += "/";

    value = properties.getProperty("PREFIX_SUB_REP_FOLDER");
    PREFIX_SUB_REP_FOLDER = (value == null) ? "PWR_REP" : value;

    value = properties.getProperty("PREFIX_SUB_EXP_FOLDER");
    PREFIX_SUB_EXP_FOLDER = (value == null) ? "PWR_EXP" : value;

    value = properties.getProperty("PREFIX_SUB_IMG_FOLDER");
    PREFIX_SUB_IMG_FOLDER = (value == null) ? "PWR_IMG" : value;

    value = properties.getProperty("LOG_FOLDER");
    LOG_FOLDER = (value == null) ? "logs" : value;

    value = properties.getProperty("SESSION_ID_DATE_FORMAT");
    SESSION_ID_DATE_FORMAT = (value == null) ? "yyyyMMdd_HHmmss_SSS" : value;

    value = properties.getProperty("SESSION_TIMEOUT"); // given in minute
    if (value == null) {
      SESSION_TIMEOUT = 3600000; // corresponding to one hour
    } else {
      try {
        SESSION_TIMEOUT = Integer.parseInt(value) * 60000;
      } catch (NumberFormatException e) {
        SESSION_TIMEOUT = 3600000;
      }
    }
    
    value = properties.getProperty("WEB_SERVICE_TIMEOUT_MS"); // given in ms
    if (value == null) {
      WEB_SERVICE_TIMEOUT_MS = 5000; // corresponding to 2 seconds
    } else {
      try {
        WEB_SERVICE_TIMEOUT_MS = Integer.parseInt(value);
      } catch (NumberFormatException e) {
        WEB_SERVICE_TIMEOUT_MS = 5000;
      }
    }

    value = properties.getProperty("BIRT_LOG_LEVEL");
    BIRT_LOG_LEVEL = (value == null) ? "INFO" : value;

    value = properties.getProperty("BIRT_RESOURCE_PATH");
    if (value == null) {
      BIRT_RESOURCE_PATH = DESIGN_FOLDER;
    } else {
      file = new File(value);
      if (file.exists()) {
        BIRT_RESOURCE_PATH = file.getAbsolutePath();
      } else {
        temp = contextPath + value;
        file = new File(temp);
        if (file.exists()) {
          BIRT_RESOURCE_PATH = file.getAbsolutePath();
        } else {
          BIRT_RESOURCE_PATH = DESIGN_FOLDER;
        }
      }
    }

    value = properties.getProperty("BIRT_RESOURCE_PATH");
    try {
      if (value == null) {
        value = "";
      }
      temp = value;
      file = new File(temp);
      if (!file.isAbsolute()) {
        temp = contextPath + value;
        file = new File(temp);
      }

      if (file.exists()) {
        BIRT_RESOURCE_PATH = file.getCanonicalPath();
      } else {
        DESIGN_FOLDER = contextPath;
      }
    } catch (IOException e) {
      Utils.getStackTraceAsString(e);
    }

    value = properties.getProperty("BIRT_LOG_ROLLING_SIZE");
    if (value == null) {
      BIRT_LOG_ROLLING_SIZE = 4194304;
    } else {
      try {
        BIRT_LOG_ROLLING_SIZE = Integer.parseInt(value);
      } catch (NumberFormatException e) {
        BIRT_LOG_ROLLING_SIZE = 4194304;
      }
    }

    value = properties.getProperty("BIRT_LOG_MAX_BACKUP_INDEX");
    if (value == null) {
      BIRT_LOG_MAX_BACKUP_INDEX = 20;
    } else {
      try {
        BIRT_LOG_MAX_BACKUP_INDEX = Integer.parseInt(value);
      } catch (NumberFormatException e) {
        BIRT_LOG_MAX_BACKUP_INDEX = 20;
      }
    }
    Locale locale = Utils.getLocale(properties.getProperty("BIRT_LOCALE"));
    BIRT_LOCALE = locale.toString();
    logger.debug(getConfigurationSetting());
  }

  /**
   * Get String representation used to display application settings
   * 
   * @return list of application settings
   */
  public static String getConfigurationSetting()
  {
    String s = "\n";
    s += "DESIGN_FOLDER : " + DESIGN_FOLDER + "\n";
    s += "DOCUMENTS_FOLDER : " + DOCUMENTS_FOLDER + "\n";
    s += "REPORT_FOLDER : " + REPORT_FOLDER + "\n";
    s += "IMAGE_FOLDER : " + IMAGE_FOLDER + "\n";
    s += "EXPORT_FOLDER : " + EXPORT_FOLDER + "\n";
    s += "THUMBNAIL_FOLDER : " + THUMBNAIL_FOLDER + "\n";
    s += "LOG_FOLDER : " + LOG_FOLDER + "\n";
    s += "PREFIX_SUB_DOC_FOLDER : " + PREFIX_SUB_DOC_FOLDER + "\n";
    s += "PREFIX_SUB_REP_FOLDER : " + PREFIX_SUB_REP_FOLDER + "\n";
    s += "PREFIX_SUB_IMG_FOLDER : " + PREFIX_SUB_IMG_FOLDER + "\n";
    s += "PREFIX_SUB_EXP_FOLDER : " + PREFIX_SUB_EXP_FOLDER + "\n";
    s += "SESSION_ID_DATE_FORMAT : " + SESSION_ID_DATE_FORMAT + "\n";
    s += "SESSION_TIMEOUT : " + SESSION_TIMEOUT + "\n";
    s += "BIRT_LOG_LEVEL : " + BIRT_LOG_LEVEL + "\n";
    s += "BIRT_LOG_ROLLING_SIZE : " + BIRT_LOG_ROLLING_SIZE + "\n";
    s += "BIRT_LOG_MAX_BACKUP_INDEX : " + BIRT_LOG_MAX_BACKUP_INDEX + "\n";
    s += "BIRT_LOCALE : " + BIRT_LOCALE + "\n";
    s += "BIRT_RESOURCE_PATH : " + BIRT_RESOURCE_PATH + "\n";
    s += "WEB_SERVICE_PATH : " + WEB_SERVICE_PATH + "\n";
    s += "WEB_SERVICE_TIMEOUT_MS : " + WEB_SERVICE_TIMEOUT_MS + "\n";
    return s;
  }
  
  public static String displayProperties(Properties prop)
  {
    String s = "\n";
    s += "BIRT_LOG_ROLLING_SIZE : " + prop.getProperty("BIRT_LOG_ROLLING_SIZE") + "\n";
    s += "BIRT_LOG_MAX_BACKUP_INDEX : " + prop.getProperty("BIRT_LOG_MAX_BACKUP_INDEX") + "\n";
    s += "BIRT_LOG_LEVEL : " + prop.getProperty("BIRT_LOG_LEVEL") + "\n";
    s += "BIRT_RESOURCE_PATH : " + prop.getProperty("BIRT_RESOURCE_PATH") + "\n";
    s += "BIRT_LOCALE : " + prop.getProperty("BIRT_LOCALE") + "\n";
    s += "DESIGN_FOLDER : " + prop.getProperty("DESIGN_FOLDER") + "\n";
    s += "DOCUMENTS_FOLDER : " + prop.getProperty("DOCUMENTS_FOLDER") + "\n";
    s += "EXPORT_FOLDER : " + prop.getProperty("EXPORT_FOLDER") + "\n";
    s += "THUMBNAIL_FOLDER : " + prop.getProperty("THUMBNAIL_FOLDER") + "\n";
    s += "IMAGE_FOLDER : " + prop.getProperty("IMAGE_FOLDER") + "\n";
    s += "LOG_FOLDER : " + prop.getProperty("LOG_FOLDER") + "\n";
    s += "REPORT_FOLDER : " + prop.getProperty("REPORT_FOLDER") + "\n";
    s += "SESSION_ID_DATE_FORMAT : " + prop.getProperty("SESSION_ID_DATE_FORMAT") + "\n";
    s += "PREFIX_SUB_DOC_FOLDER : " + prop.getProperty("PREFIX_SUB_DOC_FOLDER") + "\n";
    s += "PREFIX_SUB_IMG_FOLDER : " + prop.getProperty("PREFIX_SUB_IMG_FOLDER") + "\n";
    s += "PREFIX_SUB_REP_FOLDER : " + prop.getProperty("PREFIX_SUB_REP_FOLDER") + "\n";
    s += "PREFIX_SUB_EXP_FOLDER : " + prop.getProperty("PREFIX_SUB_EXP_FOLDER") + "\n";
    s += "SESSION_TIMEOUT : " + prop.getProperty("SESSION_TIMEOUT") + "\n";
    s += "WEB_SERVICE_PATH : " + prop.getProperty("WEB_SERVICE_PATH") + "\n";
    s += "WEB_SERVICE_TIMEOUT_MS : " + prop.getProperty("WEB_SERVICE_TIMEOUT_MS") + "\n";
    return s;
  }
}
