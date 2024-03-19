// Copyright (C) 2009-2023 Lemoine Automation Technologies
// Copyright (C) 2023 Atsora Solutions
//
// SPDX-License-Identifier: EPL-2.0

package eu.atsora.tracking.reporting.birt;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.birt.report.engine.api.IAction;
import org.eclipse.birt.report.engine.api.IHTMLActionHandler;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.eclipse.birt.report.model.api.util.ParameterValidationUtil;

import com.ibm.icu.math.BigDecimal;
import eu.atsora.tracking.reporting.util.Utils;

public class MyHTMLActionHandler implements IHTMLActionHandler {

  static Logger LOGGER = LogManager.getLogger(MyHTMLActionHandler.class);
  String baseUrl;

  public MyHTMLActionHandler(String baseUrl) {
    super();
    this.baseUrl = baseUrl;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  /**
   * Get URL of the action.
   * 
   * @param actionDefn
   * @param context
   * @return URL
   */
  public String getURL(IAction actionDefn, IReportContext context) {
    Object renderContext = getRenderContext(context);
    return getURL(actionDefn, renderContext);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.birt.report.engine.api2.IHTMLActionHandler#getURL(org.eclipse
   * .birt.report.engine.api2.IAction, java.lang.Object)
   */
  public String getURL(IAction actionDefn, Object context) {
    if (actionDefn == null) {
      return null;
    }
    String url = null;
    switch (actionDefn.getType()) {
      case IAction.ACTION_BOOKMARK:
        if (actionDefn.getActionString() != null) {
          url = "#" + actionDefn.getActionString();
        }
        break;
      case IAction.ACTION_HYPERLINK:
        url = actionDefn.getActionString();
        break;
      case IAction.ACTION_DRILLTHROUGH:
        url = buildDrillAction(actionDefn, context);
        break;
      default:
        return null;
    }
    
    //Utils.encodeFullUrl(url); Already done in buildDrillAction !!!
    return url;
    
  }

  /**
   * builds URL for drillthrough action
   * 
   * @param action
   *          instance of the IAction instance
   * @param context
   *          the context for building the action string
   * @return a URL
   */
  @SuppressWarnings({ "rawtypes" })
  protected String buildDrillAction(IAction action, Object context) {
    String baseURL = ((RenderOption) context).getBaseURL();
    String format = ((RenderOption) context).getOutputFormat();

    if (baseURL == null) {
      baseURL = baseUrl;
      LOGGER.warn("Usage of default base URL : " + baseURL);
    }

    StringBuilder link = new StringBuilder();
    String reportName = getReportName(action);
    LOGGER.debug("output format = " + ((RenderOption) context).getOutputFormat() + " for report " + reportName);

    try {
      reportName = URLDecoder.decode(reportName, "UTF-8");
    }
    catch (UnsupportedEncodingException e3) {
    	LOGGER.warn(Utils.getStackTraceAsString(e3));
    }

    if(format.equalsIgnoreCase("xls_atsora")){
      format = "xls";
    }
    
    if (reportName != null && !reportName.equals("")) {
      if (!"html".equalsIgnoreCase(format)) {
        link.append(baseURL + "/export?__format=" + format + "&__report=");
      }
      else {
        link.append(baseURL.concat("/viewer?__report="));
      }

      try {
        link.append(URLEncoder.encode(reportName, "UTF-8"));
      }
      catch (UnsupportedEncodingException e1) {
        // It should not happen. Does nothing
      	LOGGER.warn(Utils.getStackTraceAsString(e1));
      }

      // Adds the parameters
      if (action.getParameterBindings() != null) {
        Iterator paramsIte = action.getParameterBindings().entrySet().iterator();
        while (paramsIte.hasNext()) {
          Map.Entry entry = (Map.Entry) paramsIte.next();
          try {
            String key = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if (valueObj != null) {
              if (valueObj instanceof List) {
                if (((List) valueObj).size() == 1) {
                  valueObj = ((List) valueObj).get(0);
                }
                else {
                  valueObj = ((List) valueObj).toArray();
                }
              }

              Object[] values;
              if (valueObj instanceof Object[]) {
                values = (Object[]) valueObj;
              }
              else {
                values = new Object[1];
                values[0] = valueObj;
              }

              for (int i = 0; i < values.length; i++) {
                String value = getDisplayValue(values[i]);
                if (value != null) {
                  link.append("&" + URLEncoder.encode(key, "UTF-8") + "=" 
                    + Utils.encodeValue(value));
                }
              }
            }
          }
          catch (UnsupportedEncodingException e) {
            LOGGER.warn(Utils.getStackTraceAsString(e));
          }
        }
      }
    }
    try {
      String url = URLDecoder.decode(link.toString(), "UTF-8");
      LOGGER.debug("URL=" + url);
      return url;
      //return URLDecoder.decode(link.toString(), "UTF-8");
    }
    catch (UnsupportedEncodingException e) {
      LOGGER.error(Utils.getStackTraceAsString(e));
      return null;
    }
  }

  /**
   * Append report design name into a StringBuffer.
   * 
   * @param buffer
   * @param reportName
   */
  protected void appendReportDesignName(StringBuffer buffer, String reportName) {
    buffer.append("?__report="); //$NON-NLS-1$
    try {
      buffer.append(URLEncoder.encode(reportName, "UTF-8")); //$NON-NLS-1$
    }
    catch (UnsupportedEncodingException e) {
    	LOGGER.warn(Utils.getStackTraceAsString(e));
    }
  }

  /**
   * Append format.
   * 
   * @param buffer
   * @param format
   */
  protected void appendFormat(StringBuffer buffer, String format) {
    if (format != null && format.length() > 0) {
      if(format.equalsIgnoreCase("xls_atsora")){
        format = "xls";
      }
      buffer.append("&__format=" + format);//$NON-NLS-1$
    }
  }

  /**
   * Append parameter.
   * 
   * @param buffer
   * @param key
   * @param valueObj
   */
  protected void appendParamter(StringBuffer buffer, String key, Object valueObj) {
    if (valueObj != null) {
      try {
        key = URLEncoder.encode(key, "UTF-8");
        String value = valueObj.toString();
        value = Utils.encodeValue(value); // URLEncoder.encode(value, "UTF-8");
        buffer.append("&");
        buffer.append(key);
        buffer.append("=");
        buffer.append(value);
      }
      catch (UnsupportedEncodingException e) {
      	LOGGER.warn(Utils.getStackTraceAsString(e));
      }
    }
  }

  /**
   * Append bookmark as parameter .
   * 
   * @param buffer
   * @param bookmark
   */
  protected void appendBookmarkAsParamter(StringBuffer buffer, String bookmark) {
    try {
      if (bookmark != null && bookmark.length() != 0) {
        bookmark = URLEncoder.encode(bookmark, "UTF-8");
        buffer.append("&__bookmark=");//$NON-NLS-1$
        buffer.append(bookmark);
      }
    }
    catch (UnsupportedEncodingException e) {
    	LOGGER.warn(Utils.getStackTraceAsString(e));
    }
  }

  /**
   * Append bookmark.
   * 
   * @param buffer
   * @param bookmark
   */
  protected void appendBookmark(StringBuffer buffer, String bookmark) {
    try {
      if (bookmark != null && bookmark.length() != 0) {
        bookmark = URLEncoder.encode(bookmark, "UTF-8");
        buffer.append("#");//$NON-NLS-1$
        buffer.append(bookmark);
      }
    }
    catch (UnsupportedEncodingException e) {
    	LOGGER.warn(Utils.getStackTraceAsString(e));
    }
  }

  /**
   * Get report name.
   * 
   * @param action
   * @return
   */
  String getReportName(IAction action) {
    String systemId = action.getSystemId();
    String reportName = action.getReportName();

    if (systemId == null) {
      return reportName;
    }
    // if the reportName is an URL, use it directly
    try {
      URL url = new URL(reportName);
      if ("file".equals(url.getProtocol())) {
        return url.getFile();
      }
      return url.toExternalForm();
    }
    catch (MalformedURLException ex) {
    	//LOGGER.warn(Utils.getStackTraceAsString(ex));
    }
    // if the system id is the URL, merge the report name with it
    try {
      URL root = new URL(systemId);
      URL url = new URL(root, reportName);
      if ("file".equals(url.getProtocol())) {
        return url.getFile();
      }
      return url.toExternalForm();
    }
    catch (MalformedURLException ex) {
    	//LOGGER.warn(Utils.getStackTraceAsString(ex));
    }
    // now the root should be a file and the report name is a file also
    File file = new File(reportName);
    if (file.isAbsolute()) {
      return reportName;
    }

    try {
      URL root = new File(systemId).toURI().toURL();
      URL url = new URL(root, reportName);
      assert "file".equals(url.getProtocol());
      return url.getFile();
    }
    catch (MalformedURLException ex) {
    	//LOGGER.warn(Utils.getStackTraceAsString(ex));
    }
    return reportName;
  }

  /**
   * Get render context.
   * 
   * @param context
   * @return
   */
  protected Object getRenderContext(IReportContext context) {
    if (context == null) {
      return null;
    }
    return context.getRenderOption();
  }

  /**
   * Get display value.
   * 
   * @param value
   * @return
   */
  String getDisplayValue(Object value) {
    if (value == null)
      return null;

    if (value instanceof Float || value instanceof Double || value instanceof BigDecimal) {
      return value.toString();
    }
    return ParameterValidationUtil.getDisplayValue(value);
  }

  String buildBookmarkAction(String bookmark) {
    String url = "javascript:try{catchBookmark(" + "\'" + bookmark + "\');}catch(e){parent.catchBookmark(\'" + bookmark + "\');};";
    return url;
  }

}
