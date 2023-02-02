// Copyright (C) 2009-2023 Lemoine Automation Technologies
//
// SPDX-License-Identifier: EPL-2.0

package com.lemoinetechnologies.pulse.reporting.util;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;
import java.text.*;
import java.util.regex.*;
import java.net.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.api.DocxRenderOption;
import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.EmitterInfo;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IEngineTask;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IParameterGroupDefn;
import org.eclipse.birt.report.engine.api.IParameterSelectionChoice;
import org.eclipse.birt.report.engine.api.IRenderTask;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunTask;
import org.eclipse.birt.report.engine.api.IScalarParameterDefn;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.eclipse.birt.report.model.api.ExpressionHandle;

import com.lemoinetechnologies.pulse.reporting.birt.BirtEngineHelper;
import com.lemoinetechnologies.pulse.reporting.birt.Configuration;
import com.lemoinetechnologies.pulse.reporting.birt.MyHTMLActionHandler;
import com.lemoinetechnologies.pulse.reporting.domain.GroupParameter;
import com.lemoinetechnologies.pulse.reporting.domain.ReportTemplate;
import com.lemoinetechnologies.pulse.reporting.domain.ReportViewer;
import com.lemoinetechnologies.pulse.reporting.domain.ScalarParameter;
import com.lemoinetechnologies.pulse.reporting.exception.ReportTemplateNotFoundException;
import com.lemoinetechnologies.pulse.reporting.messages.I18nPulseReporting;

public class BirtEngineUtils
{
  private static Logger LOGGER = LogManager.getLogger(BirtEngineUtils.class);

  /**
   * Set value of parameter given by rp in the task
   * 
   * @param engineTask
   *          An IEngineTask (IGetParameterDefinitionTask, IRunTask,...)
   * @param scalarParameter
   * @throws ParseException
   */
  private static void setValueInEngineTaskFromScalarParameter(IEngineTask engineTask, ScalarParameter scalarParameter) throws ParseException
  {
    // decode IS already done ! 
    String[] value = scalarParameter.getValue();
    if (value == null || value.length == 0)
      return;

    switch (scalarParameter.getDataType()) {
      case STRING:
        if (scalarParameter.isMultiSelect()) {
          engineTask.setParameterValue(scalarParameter.getName(), value);
        } else {
          engineTask.setParameterValue(scalarParameter.getName(), value[0]);
        }
        break;
      case INTEGER:
        if (scalarParameter.isMultiSelect()) {
          Integer[] tab = new Integer[value.length];
          for (int i = 0; i < value.length; i++) {
            Double d = Double.parseDouble(value[i]);
            tab[i] = Integer.valueOf(d.intValue());
          }
          engineTask.setParameterValue(scalarParameter.getName(), tab);
        } else {
          Double d = Double.parseDouble(value[0]);
          engineTask.setParameterValue(scalarParameter.getName(), Integer.valueOf(d.intValue()));
        }
        break;
      case FLOAT:
        if (scalarParameter.isMultiSelect()) {
          Float[] tab = new Float[value.length];
          for (int i = 0; i < value.length; i++) {
            tab[i] = Float.parseFloat(value[i]);
          }
          engineTask.setParameterValue(scalarParameter.getName(), tab);
        } else {
          engineTask.setParameterValue(scalarParameter.getName(), Float.parseFloat(value[0]));
        }
        break;
      case DECIMAL:
        if (scalarParameter.isMultiSelect()) {
          Double[] tab = new Double[value.length];
          for (int i = 0; i < value.length; i++) {
            tab[i] = Double.parseDouble(value[i]);
          }
          engineTask.setParameterValue(scalarParameter.getName(), tab);
        } else {
          engineTask.setParameterValue(scalarParameter.getName(), Double.parseDouble(value[0]));
        }
        break;
      case BOOLEAN:
        if (scalarParameter.isMultiSelect()) {
          Boolean[] tab = new Boolean[value.length];
          for (int i = 0; i < value.length; i++) {
            tab[i] = Boolean.parseBoolean(value[i]);
          }
          engineTask.setParameterValue(scalarParameter.getName(), tab);
        } else {
          engineTask.setParameterValue(scalarParameter.getName(), Boolean.parseBoolean(value[0]));
        }
        break;
      case DATE:
        if (scalarParameter.isMultiSelect()) {
          java.sql.Date[] tab = new java.sql.Date[value.length];
          for (int i = 0; i < value.length; i++) {
            tab[i] = Utils.parseDate(value[i]);
          }
          engineTask.setParameterValue(scalarParameter.getName(), tab);
        } else {
          engineTask.setParameterValue(scalarParameter.getName(), Utils.parseDate(value[0]));
        }
        break;
      case TIME:
        java.sql.Time time = null;
        if (scalarParameter.isMultiSelect()) {
          java.sql.Time[] tab = new java.sql.Time[value.length];
          for (int i = 0; i < value.length; i++) {
            tab[i] = Utils.parseTime(value[i]);
          }
          engineTask.setParameterValue(scalarParameter.getName(), tab);
        } else {
          engineTask.setParameterValue(scalarParameter.getName(), Utils.parseTime(value[0]));
        }
        break;
      case DATETIME:
        if (scalarParameter.isMultiSelect()) {
          java.sql.Timestamp[] tab = new java.sql.Timestamp[value.length];
          for (int i = 0; i < value.length; i++) {
            tab[i] = Utils.parseDateTime(value[i]);
          }
          engineTask.setParameterValue(scalarParameter.getName(), tab);
        } else {
          engineTask.setParameterValue(scalarParameter.getName(), Utils.parseDateTime(value[0]));
        }
        break;
      case ANY:
        break;
    }
  }

  /**
   * Set value of all parameters of report instance used for viewing in the run
   * engine task
   * 
   * @param runTask
   *          : Run engine task
   * @param reportviewer
   *          : report instance
   * @throws ParseException
   */
  public static void setValueInEngineTask(IRunTask runTask, ReportViewer reportviewer) throws ParseException
  {
    // firstly, set value of hidden parameters because they can be used by another one
    for (ScalarParameter scalarparameter : reportviewer.getScalarParameters()) {
      if (scalarparameter.isHidden()) {
        setValueInEngineTaskFromScalarParameter(runTask, scalarparameter);
      }
    }
    for (GroupParameter groupparameter : reportviewer.getGroupParameters()) {
      for (ScalarParameter scalarparameter : groupparameter.getScalarParameterMap().values()) {
        if (scalarparameter.isHidden()) {
          setValueInEngineTaskFromScalarParameter(runTask, scalarparameter);
        }
      }
    }

    // and after, we set value for scalar parameter which is not hidden
    for (ScalarParameter scalarparameter : reportviewer.getScalarParameters()) {
      if (!scalarparameter.isHidden()) {
        setValueInEngineTaskFromScalarParameter(runTask, scalarparameter);
      }
    }
    for (GroupParameter groupparameter : reportviewer.getGroupParameters()) {
      for (ScalarParameter scalarparameter : groupparameter.getScalarParameterMap().values()) {
        if (!scalarparameter.isHidden()) {
          setValueInEngineTaskFromScalarParameter(runTask, scalarparameter);
        }
      }
    }
  }
  
  static boolean isWebserviceReachable()
  {
    String path = Configuration.WEB_SERVICE_PATH + "test";
    try {
      URL testUrl = new URL(path);
      StringBuilder answer = new StringBuilder(100000);

      URLConnection testConnection = testUrl.openConnection();
      testConnection.setConnectTimeout(Configuration.WEB_SERVICE_TIMEOUT_MS);
      testConnection.setReadTimeout(Configuration.WEB_SERVICE_TIMEOUT_MS);
      BufferedReader in = new BufferedReader(new InputStreamReader(testConnection.getInputStream()));
      String inputLine;

      while ((inputLine = in.readLine()) != null) {
        answer.append(inputLine);
        answer.append("\n");
      }
      in.close();
    } catch (Exception e) {
      LOGGER.warn("Warning ! " + path + " is not reachable!");
      return false;
    }
    
    return true;
  }
  
  // https://stackoverflow.com/questions/10500775/parse-json-from-httpurlconnection-object
  static String getJSON(String url)
  {
    HttpURLConnection c = null;
    try {
      URL u = new URL(url);
      c = (HttpURLConnection) u.openConnection();
      c.setRequestMethod("GET");
      c.setRequestProperty("Content-length", "0");
      c.setUseCaches(false);
      c.setAllowUserInteraction(false);
      c.setConnectTimeout(Configuration.WEB_SERVICE_TIMEOUT_MS);
      c.setReadTimeout(Configuration.WEB_SERVICE_TIMEOUT_MS);
      c.connect();
      int status = c.getResponseCode();

      switch (status) {
        case 200:
        case 201:
          BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
          StringBuilder sb = new StringBuilder();
          String line;
          while ((line = br.readLine()) != null) {
              sb.append(line + "\n");
          }
          br.close();
          return sb.toString();
      }
    } catch (MalformedURLException ex) {
        LOGGER.error("getJSON with " + url + ": " + Utils.getStackTraceAsString(ex));
    } catch (IOException ex) {
        LOGGER.error("getJSON with " + url + ": " + Utils.getStackTraceAsString(ex));
    } finally {
       if (c != null) {
          try {
              c.disconnect();
          } catch (Exception ex) {
             LOGGER.error("getJSON with " + url + ": " + Utils.getStackTraceAsString(ex));
          }
       }
    }
    return null;
  }
  
  static String getJSONParam(String json, String param)
  {
    Pattern pattern = Pattern.compile("\"" + param + "\":\"([^\"]*)\"");
    Matcher matcher = pattern.matcher(json);
    return matcher.find() ? matcher.group(1) : "";
  }

  static String getNow(boolean isDate)
  {
    Date d = new Date(); // Now
    return isDate ? new SimpleDateFormat("yyyy-MM-dd").format(d) : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(d);
  }
  
  static void CompleteDateTimeParams(Map<String, String[]> parameterMap,
    Map<String, String[]> queryParameterMap, String newParam, String oldParamMin, String oldParamMax)
  {
    // Maybe the new param is already specified, in that case we do nothing
    if (queryParameterMap.containsKey(newParam)) {
      String[] paramValues = queryParameterMap.get(newParam);
      if (paramValues != null) {
        for (String paramValue : paramValues)
          if (paramValue != null && !paramValue.equals(""))
            return;
      }
    }

    // Check that "oldParamMin" is specified and possibly "oldParamMax" that comes along
    String oldParamMinValue = "";
    String oldParamMaxValue = "";
    if (queryParameterMap.containsKey(oldParamMin)) {
      String[] paramValues = queryParameterMap.get(oldParamMin);
      if (paramValues != null) {
        for (String paramValue : paramValues) {
          if (paramValue != null && !paramValue.equals("")) {
            oldParamMinValue = paramValue;
            break;
          }
        }
      }
    }
    if (queryParameterMap.containsKey(oldParamMax)) {
      String[] paramValues = queryParameterMap.get(oldParamMax);
      if (paramValues != null) {
        for (String paramValue : paramValues) {
          if (paramValue != null && !paramValue.equals("")) {
            oldParamMaxValue = paramValue;
            break;
          }
        }
      }
    }
    
    if (!oldParamMinValue.equals("")) {
      if (!oldParamMaxValue.equals("")) {
        // Complete the parameters with from...to...
        parameterMap.put(newParam, new String[]{ "explicit_" + oldParamMinValue + "_" + oldParamMaxValue });
      } else {
        // Complete the parameters with since...
        parameterMap.put(newParam, new String[]{ "since_" + oldParamMinValue + "_" });
      }
    }
  }
  
  static boolean UpdateRange(Map<String, String[]> parameterMap, String keyToFind, boolean isDate, String paramMin, String paramMax, boolean isWReachable)
  {
    if (parameterMap.get(keyToFind) == null) {
      return false;
    }
    
    // Update min / max if the new format with current / past / since / explicit is specified
    String[] parts = parameterMap.get(keyToFind)[0].split("_");
    boolean withResult = true;
    if (parts.length == 3) {
      if (parts[0].equals("past") || parts[0].equals("current")) {
        if (isWReachable) {
          // Get the range
          String webServicePath = Configuration.WEB_SERVICE_PATH + "Time/" + (parts[0].equals("past") ? "PastRange" : "CurrentRange") + "/" + parts[1] + "_" + parts[2];
          String[] currentDate = parameterMap.get("currentdate");
          if (currentDate != null && currentDate.length == 1)
            webServicePath += "?CurrentDate=" + currentDate[0];
          String json = getJSON(webServicePath);
          if (json != null) {
            String result = getJSONParam(json, isDate ? "DayRange" : "LocalDateTimeRange");

            // Store it
            String[] elements = result.replaceAll("[\\(\\[\\]\\)Z]", "").replaceAll("T", " ").split(",");
            LOGGER.info((isDate ? "date" : "datetime") + " range computed with " + parameterMap.get(keyToFind)[0] + " is: " + elements[0] + " => " + elements[1]);
            parameterMap.put(paramMin, new String[] {elements[0] + (isDate ? "" : ".000")});
            parameterMap.put(paramMax, new String[] {elements[1] + (isDate ? "" : ".000")});
          } else {
            LOGGER.error("Got no data from url '" + webServicePath + "'");
            withResult = false;
          
            // Add missing parameters
            String now = getNow(isDate);
            if (parameterMap.get(paramMin) == null)
                parameterMap.put(paramMin, new String[] { now });
            if (parameterMap.get(paramMax) == null)
                parameterMap.put(paramMax, new String[] { now });
          }
        } 
        else{
          return true; // = is mandatory
        }
      } else if (parts[0].equals("explicit")) {
        parameterMap.put(paramMin, new String[] { parts[1] });
        parameterMap.put(paramMax, new String[] { parts[2] });
      } else if (parts[0].equals("since")) {
        parameterMap.put(paramMin, new String[] { parts[1] });
        parameterMap.put(paramMax, new String[] { getNow(isDate) });
      }
    } else if (parts.length == 2 && parts[0].equals("since")) {
      parameterMap.put(paramMin, new String[] { parts[1] });
      parameterMap.put(paramMax, new String[] { getNow(isDate) });
    }
    return false; // = is NOT mandatory
  }

  static void ProcessParameters(Map<String, String[]> parameterMap)
  {
    // PulseMachines
    if (parameterMap.get("PulseMachine") != null && parameterMap.get("PulseMachines") == null)
      parameterMap.put("PulseMachines", parameterMap.get("PulseMachine"));
    if (parameterMap.get("PulseCompanyCategoryMachines") != null && parameterMap.get("PulseMachines") == null)
      parameterMap.put("PulseMachines", parameterMap.get("PulseCompanyCategoryMachines"));
    if (parameterMap.get("PulseCompanyDepartmentMachines") != null && parameterMap.get("PulseMachines") == null)
      parameterMap.put("PulseMachines", parameterMap.get("PulseCompanyDepartmentMachines"));
    if (parameterMap.get("PulseCompanyMachines") != null && parameterMap.get("PulseMachines") == null)
      parameterMap.put("PulseMachines", parameterMap.get("PulseCompanyMachines"));
    if (parameterMap.get("PulseCategoryMachines") != null && parameterMap.get("PulseMachines") == null)
      parameterMap.put("PulseMachines", parameterMap.get("PulseCategoryMachines"));
    
    // PulseCompanies
    if (parameterMap.get("PulseCompany") != null && parameterMap.get("PulseCompanies") == null)
      parameterMap.put("PulseCompanies", parameterMap.get("PulseCompany"));
    if (parameterMap.get("PulseMachineCompany") != null && parameterMap.get("PulseCompanies") == null)
      parameterMap.put("PulseCompanies", parameterMap.get("PulseMachineCompany"));
    
    // PulseDepartments
    if (parameterMap.get("PulseDepartment") != null && parameterMap.get("PulseDepartments") == null)
      parameterMap.put("PulseDepartments", parameterMap.get("PulseDepartment"));
    if (parameterMap.get("PulseCompanyDepartment") != null && parameterMap.get("PulseDepartments") == null)
      parameterMap.put("PulseDepartments", parameterMap.get("PulseCompanyDepartment"));
    if (parameterMap.get("PulseMachineDepartment") != null && parameterMap.get("PulseDepartments") == null)
      parameterMap.put("PulseDepartments", parameterMap.get("PulseMachineDepartment"));
    
    // PulseCategories
    if (parameterMap.get("PulseCategory") != null && parameterMap.get("PulseCategories") == null)
      parameterMap.put("PulseCategories", parameterMap.get("PulseCategory"));
    if (parameterMap.get("PulseMachineCategory") != null && parameterMap.get("PulseCategories") == null)
      parameterMap.put("PulseCategories", parameterMap.get("PulseMachineCategory"));
    
    // JobComponent
    if (parameterMap.get("PulseComponent") != null && parameterMap.get("PulseJobComponent") == null)
      parameterMap.put("PulseJobComponent", parameterMap.get("PulseComponent"));
    
    // Date / Datetime : use web service to retrieve PulseMinDatexxx et PulseMaxDatexxx dans ParameterMap
    boolean isWReachable = isWebserviceReachable();
    boolean isMandatory = UpdateRange(parameterMap, "WebAppParamsDate", true,
      "PulseMinDate", "PulseMaxDate", isWReachable);
    isMandatory |= UpdateRange(parameterMap, "WebAppParamsDate1", true,
      "PulseMinDate1", "PulseMaxDate1", isWReachable);
    isMandatory |= UpdateRange(parameterMap, "WebAppParamsDate2", true,
      "PulseMinDate2", "PulseMaxDate2", isWReachable);
    isMandatory |= UpdateRange(parameterMap, "WebAppParamsDate3", true,
      "PulseMinDate3", "PulseMaxDate3", isWReachable);
    isMandatory |= UpdateRange(parameterMap, "WebAppParamsDateTime", false,
      "PulseMinDateTime", "PulseMaxDateTime", isWReachable);
    isMandatory |= UpdateRange(parameterMap, "WebAppParamsDateTime1", false,
      "PulseMinDateTime1", "PulseMaxDateTime1", isWReachable);
    isMandatory |= UpdateRange(parameterMap, "WebAppParamsDateTime2", false,
      "PulseMinDateTime2", "PulseMaxDateTime2", isWReachable);
    isMandatory |= UpdateRange(parameterMap, "WebAppParamsDateTime3", false,
      "PulseMinDateTime3", "PulseMaxDateTime3", isWReachable);

    if ( isMandatory && !isWReachable ) {
      // Get the existing messages, if any
      String[] array1 = new String[] {};
      if (parameterMap.get("PulseInfoMessage") != null)
        array1 = parameterMap.get("PulseInfoMessage");
      
      // Append a new message
      String[] newArray = new String[array1.length + 1];
      if (array1.length > 0)
        System.arraycopy(array1, 0, newArray, 0, array1.length);
      newArray[array1.length] = "Periods might be wrong in this report because LemWebService is not reachable.";
      
      // Store them
      parameterMap.put("PulseInfoMessage", newArray);
    }
    else {
      /// REMOVE PulseInfoMessage
      if (parameterMap.get("PulseInfoMessage") != null) {
        parameterMap.put("PulseInfoMessage", null);
      }
    }
  }

  public static ReportTemplate getReportTemplate(String reportId, Map<String, String[]> queryParameterMap, Locale locale)
    throws FileNotFoundException, ReportTemplateNotFoundException, BirtException, IllegalArgumentException, ParseException, UnsupportedEncodingException 
  {
    // Remove the folder path if any (flat structure)
    int slashPos = reportId.replace("\\", "/").lastIndexOf("/");
    if (slashPos != -1)
      reportId = reportId.substring(slashPos + 1);

    // Possibly remove the extension
    if (reportId.endsWith(".rptdesign"))
      reportId = reportId.substring(0, reportId.length() - 10);
    
    // Create the path
    String path = Configuration.DESIGN_FOLDER + File.separator + reportId + ".rptdesign";
    
    IGetParameterDefinitionTask parameterDefinitionTask = null;
    Collection<?> params = null;
    try
    {
      // Test if the given file path exists
      File file = new File(path);
      if (!file.exists()) {
        LOGGER.error("Unable to find following report template: " + reportId);
        throw new FileNotFoundException(I18nPulseReporting.getString("TemplateFileNotFoundMsg", locale));
      }
      
      // Get report engine, ReportRunnable and ParameterDefinitionTask
      IReportEngine reportEngine = BirtEngineHelper.getReportEngine();
      IReportRunnable reportRunnable = reportEngine.openReportDesign(path);
      parameterDefinitionTask = reportEngine.createGetParameterDefinitionTask(reportRunnable);
      parameterDefinitionTask.setLocale(locale);

      // Retrieve general properties of ReportDetails
      ReportTemplate reportTemplate = new ReportTemplate(reportId);
      reportTemplate.setComment((String) reportRunnable.getProperty("comments"));
      reportTemplate.setDescription((String) reportRunnable.getProperty("description"));
      reportTemplate.setTitle((String) reportRunnable.getProperty("title"));

      // Begin the creation of a "parameterMap" that will be used for filling the report parameters
      // First initialize the parameters with the list of all parameter default values
      Map<String, String[]> parameterMap = new TreeMap<String, String[]>();
      IScalarParameterDefn scalarParameterDefn;
      params = parameterDefinitionTask.getParameterDefns(true);
      for (Object param : params) {
        if (param instanceof IParameterGroupDefn) {
          // In case of a group of parameters
          IParameterGroupDefn parameterGroupDefn = (IParameterGroupDefn) param;
          LinkedHashMap<String, ScalarParameter> scalarParams = getGroupParameter(parameterDefinitionTask, parameterGroupDefn).getScalarParameterMap();
          for (Map.Entry<String, ScalarParameter> entry : scalarParams.entrySet()) {
            //String key = entry.getKey();
            ScalarParameter scalarParam = entry.getValue();
            String[] defaultValue = scalarParam.getDefaultValue();
            String paramName = scalarParam.getName();
            if (defaultValue != null && defaultValue.length > 0)
              parameterMap.put(paramName, defaultValue);
          }
        } else if (param instanceof IScalarParameterDefn) {
          // In case of a scalar parameter
          scalarParameterDefn = (IScalarParameterDefn) param;
          ScalarParameter scalarParam = getScalarParameter(parameterDefinitionTask, scalarParameterDefn, null);
          String[] defaultValue = scalarParam.getDefaultValue();
          String paramName = scalarParam.getName();
          if (defaultValue != null && defaultValue.length > 0)
            parameterMap.put(paramName, defaultValue);
        }
      }
      
      // Complete the missing WebAppParamsDate if possible
      // (use PulseMinDate and PulseMaxDate param to fill WebAppParamsDate if empty)
      CompleteDateTimeParams(parameterMap, queryParameterMap, "WebAppParamsDate", "PulseMinDate", "PulseMaxDate");
      CompleteDateTimeParams(parameterMap, queryParameterMap, "WebAppParamsDate1", "PulseMinDate1", "PulseMaxDate1");
      CompleteDateTimeParams(parameterMap, queryParameterMap, "WebAppParamsDate2", "PulseMinDate2", "PulseMaxDate2");
      CompleteDateTimeParams(parameterMap, queryParameterMap, "WebAppParamsDate3", "PulseMinDate3", "PulseMaxDate3");
      CompleteDateTimeParams(parameterMap, queryParameterMap, "WebAppParamsDateTime", "PulseMinDateTime", "PulseMaxDateTime");
      CompleteDateTimeParams(parameterMap, queryParameterMap, "WebAppParamsDateTime1", "PulseMinDateTime1", "PulseMaxDateTime1");
      CompleteDateTimeParams(parameterMap, queryParameterMap, "WebAppParamsDateTime2", "PulseMinDateTime2", "PulseMaxDateTime2");
      CompleteDateTimeParams(parameterMap, queryParameterMap, "WebAppParamsDateTime3", "PulseMinDateTime3", "PulseMaxDateTime3");

      // Extract parameters from the query and complete / override "parameterMap"
      for (String paramName : queryParameterMap.keySet())
      {
        String[] paramValues = queryParameterMap.get(paramName);

        // Url decode
        //paramValues = Utils.decodeUrl(paramValues); is NOT NEEDED - parametermap is not encoded
        
        if (paramValues == null || paramValues.length == 0 || (paramValues.length == 1 && paramValues[0].equals(""))) {
          // Add the value only if it's not already in the map (we keep the default values)
          if (!parameterMap.containsKey(paramName))
            parameterMap.put(paramName, null);
        } else {
          parameterMap.put(paramName, paramValues);
        }
      }

      // Process parameters: translate old parameters AND possibly compute ranges from WebAppParamsDate and adapt min/max parameters
      try
      {
        ProcessParameters(parameterMap);
      } catch (Exception ex) {
        LOGGER.error("ProcessParameters: " + Utils.getStackTraceAsString(ex));
      }

      // Then initialize parameters in BIRT with "parameterMap"
      for (Object param : params) {
        if (param instanceof IScalarParameterDefn) {
          scalarParameterDefn = (IScalarParameterDefn) param;
          ScalarParameter scalarParameter = getScalarParameter(parameterDefinitionTask, scalarParameterDefn, null);
          scalarParameter.setValue(parameterMap.get(scalarParameter.getName()));
          BirtEngineUtils.setValueInEngineTaskFromScalarParameter(parameterDefinitionTask, scalarParameter);
        }
      }

      // Populate "reportTemplate": add groups, parameters and values
      for (Object param : params) {
        // In case of groups of parameters
        if (param instanceof IParameterGroupDefn) {
          IParameterGroupDefn parameterGroupDefn = (IParameterGroupDefn) param;
          reportTemplate.getGroupParameters().add(getGroupParameter(parameterDefinitionTask, parameterGroupDefn));
          // In case of scalar parameters
        } else if (param instanceof IScalarParameterDefn) {
          scalarParameterDefn = (IScalarParameterDefn) param;
          reportTemplate.getScalarParameters().add(getScalarParameter(parameterDefinitionTask, scalarParameterDefn, null));
        }
      }
      for (Map.Entry<String, String[]> entry : parameterMap.entrySet())
        reportTemplate.setParameterValue(entry.getKey(), entry.getValue());

      // Return the configured reportTemplate
      return reportTemplate;
    } finally {
      if (parameterDefinitionTask != null) {
        parameterDefinitionTask.close();
      }
    }
  }

  /**
   * get detailed description of a scalar parameter of report
   * 
   * @param parameterDefinitionTask
   *          IGetParameterDefinitionTask object containing definition of report
   *          parameters
   * @param scalar
   *          : Object describing scalar parameter
   * @param group
   *          : Object describing group in which parameter belongs or
   *          <code>null</code> if parameter is not in any group
   * @return a scalar parameter
   * @throws UnsupportedEncodingException
   */
  private static ScalarParameter getScalarParameter(IGetParameterDefinitionTask parameterDefinitionTask, IScalarParameterDefn scalar, IParameterGroupDefn group) throws UnsupportedEncodingException
  {
    ScalarParameter scalarParameter = new ScalarParameter(scalar.getName());
    scalarParameter.setDisplayFormat(scalar.getDisplayFormat());
    scalarParameter.setPromptText(scalar.getPromptText());
    scalarParameter.setGroupName((group == null) ? null : group.getName());
    scalarParameter.setHidden(scalar.isHidden());
    scalarParameter.setRequired(scalar.isRequired());
    scalarParameter.setControlType(ScalarParameter.getControlType(scalar.getControlType()));
    scalarParameter.setDataType(ScalarParameter.getDataType(scalar.getDataType()));
    scalarParameter.setParameterType(ScalarParameter.getParameterType(scalar.getScalarParameterType()));
    scalarParameter.setHelpText(scalar.getHelpText());

    try {
      if (null == parameterDefinitionTask.getDefaultValue(scalar)) {
        scalarParameter.setDefaultValue(null);
        scalarParameter.setValue(null);
      } else if (parameterDefinitionTask.getDefaultValue(scalar) instanceof Object[]) {
        Object[] defaultValue = (Object[]) parameterDefinitionTask.getDefaultValue(scalar);
        String[] tab = new String[defaultValue.length];
        for(int i = 0; i < defaultValue.length; i++)
          tab[i] = defaultValue[i].toString();
        scalarParameter.setDefaultValue(tab);
        scalarParameter.setValue(tab);
      } else {
        Object defaultValue = parameterDefinitionTask.getDefaultValue(scalar);
        String[] tab = new String[] { defaultValue.toString() };
        scalarParameter.setDefaultValue(tab);
        scalarParameter.setValue(tab);
      }
    }
    catch (Exception e) {
      LOGGER.error(Utils.getStackTraceAsString(e));
    }

    // Retrieve the selection list
    Collection<?> selectionList = parameterDefinitionTask.getSelectionList(scalar.getName());
    if (selectionList != null) {
      for (Object item : selectionList) {
        IParameterSelectionChoice selectionItem = (IParameterSelectionChoice) item;
        scalarParameter.getSelectionList().put(selectionItem.getValue().toString(), selectionItem.getLabel());
      }
    }

    return scalarParameter;
  }

  /**
   * Get ReportGroupParameter object which is our representation of parameter
   * group
   * 
   * @param parameterDefinitionTask
   *          IGetParameterDefinitionTask object used to retrieve parameter
   *          information
   * @param parameterGroupDefn
   *          Object representing parameter group of report template
   * @return ReportGroupParameter object
   * @throws UnsupportedEncodingException
   */
  private static GroupParameter getGroupParameter(IGetParameterDefinitionTask parameterDefinitionTask, IParameterGroupDefn parameterGroupDefn)
      throws UnsupportedEncodingException
  {
    // Retrieve the display form of the current group parameter.
    // Displayform is used to distinguish the kind of parameter groups, so that the corresponding widget is associated
    ExpressionHandle expressionHandle = parameterGroupDefn.getHandle().getExpressionProperty("DISPLAYFORM");

    // If expressionHandle is null, it means that this Group of parameters has default form
    String displayForm;
    if (null != expressionHandle) {
      displayForm = expressionHandle.getStringValue();
    } else {
      displayForm = "DEFAULT";
    }

    // For specific format, we verify that inner scalar parameter have corrects attributes
    ArrayList<?> scalarParameterDefns = parameterGroupDefn.getContents();
    IScalarParameterDefn scalarParameterDefn;
    
    if (displayForm.equals("TREEVIEW") || displayForm.equals("TREEVIEW_BY_LEAF")) {
      int size = scalarParameterDefns.size();
      int level;

      // test = (2 pow size) - 1
      // if size = 4, then test = 1111 in binary representation
      int test = ((1 << size) - 1);
      for (int i = 0; i < size; i++) {
        scalarParameterDefn = (IScalarParameterDefn) scalarParameterDefns.get(i);
        expressionHandle = scalarParameterDefn.getHandle().getExpressionProperty("GROUPPOSITION");
        String groupPosition = (expressionHandle != null) ? expressionHandle.getStringValue() : "0";
        level = Integer.parseInt(groupPosition);
        try {
          level = Integer.parseInt(groupPosition);
        } catch (NumberFormatException e) {
          level = 0;
        }
        if ((level >= 1) && (level <= size)) {
          test = test ^ (1 << (level - 1));
        }
      }

      // It means that at least one parameter do not have expected value at expression property
      if (test != 0 || (displayForm.equals("TREEVIEW_BY_LEAF") && size != 3)) {
        displayForm = "DEFAULT";
        LOGGER.warn("Display form of group of parameters " + parameterGroupDefn.getDisplayName() + " is set to default");
      }
    } else if (displayForm.equals("DATERANGE")) {
        int test = 3;
        for (int i = 0; i < scalarParameterDefns.size(); i++) {
          scalarParameterDefn = (IScalarParameterDefn) scalarParameterDefns.get(i);
          expressionHandle = scalarParameterDefn.getHandle().getExpressionProperty("GROUPPOSITION");
          String groupPosition = (expressionHandle != null) ? expressionHandle.getStringValue().toUpperCase() : "DEFAULT";
          if (groupPosition.equals("MAXDATE")) {
            test = test ^ 2;
          } else if (groupPosition.equals("MINDATE")) {
            test = test ^ 1;
          }
        }

        // If at least one of the property is missing
        if (test != 0)
          displayForm = "DEFAULT";
    }

    // Should it be hidden or not?
    boolean isHidden = false;
    expressionHandle = parameterGroupDefn.getHandle().getExpressionProperty("HIDDEN");
    if (null != expressionHandle) {
      isHidden = expressionHandle.getStringValue().toLowerCase().equals("true");
    }

    GroupParameter groupParameter = new GroupParameter(parameterGroupDefn.getName(), displayForm, isHidden);
    groupParameter.setPromptText(parameterGroupDefn.getPromptText());
    groupParameter.setDisplayName(parameterGroupDefn.getDisplayName());
    groupParameter.setHelpText(parameterGroupDefn.getHelpText());

    // Thereafter, we re-list scalar parameter and we add them to parameter's group list of parameters
    for (int i = 0; i < scalarParameterDefns.size(); i++) {
      scalarParameterDefn = (IScalarParameterDefn) scalarParameterDefns.get(i);
      ScalarParameter scalarParameter = getScalarParameter(parameterDefinitionTask, scalarParameterDefn, parameterGroupDefn);

      String groupPosition = (displayForm.equals("TREEVIEW") || displayForm.equals("TREEVIEW_BY_LEAF") || displayForm.equals("DATERANGE") || displayForm.equals("MACHINES")) ?
        scalarParameterDefn.getHandle().getExpressionProperty("GROUPPOSITION").getStringValue().toUpperCase() :
        scalarParameter.getName();

      groupParameter.getScalarParameterMap().put(groupPosition, scalarParameter);
    }

    return groupParameter;
  }

  public static String getTaskStatus(int status)
  {
    switch (status) {
      case IRunTask.STATUS_SUCCEEDED:
        return "SUCCEEDED";
      case IRunTask.STATUS_CANCELLED:
        return "CANCELLED";
      case IRunTask.STATUS_FAILED:
        return "FAILED";
      case IRunTask.STATUS_RUNNING:
        return "RUNNING";
      case IRunTask.STATUS_NOT_STARTED:
        return "NOT_STARTED";
      default:
        return "UNKNOWN";
    }
  }

  public static void setRenderOption(IRenderTask renderTask, String format, String outputfilepath, String baseUrl, String webAppBaseUrl, String imageUrl, String imageFolder)
  {
    EmitterInfo ei = EmitterUtil.getEmitterInfo(format);
    LOGGER.info("Emitter: ID=" + ei.getID() + "  File extension=" + ei.getFileExtension() + "  Format=" + ei.getFormat());
    if ("doc".equalsIgnoreCase(format)) {
      LOGGER.debug("Setting DOC RenderOption");
      RenderOption renderOption = new RenderOption();
      renderOption.setSupportedImageFormats("PNG;GIF;JPG;BMP");
      renderOption.setOutputFormat(ei.getFormat());
      renderOption.setEmitterID(ei.getID());
      renderOption.setOutputFileName(outputfilepath);
      renderOption.setBaseURL(baseUrl);
      renderOption.setActionHandler(new MyHTMLActionHandler(webAppBaseUrl));
      renderTask.setRenderOption(renderOption);
    } else if ("docx".equalsIgnoreCase(format)) {
      LOGGER.debug("Setting DOCX RenderOption");
      DocxRenderOption renderOption = new DocxRenderOption();
      renderOption.setSupportedImageFormats("PNG;GIF;JPG;BMP");
      renderOption.setOutputFormat(ei.getFormat());
      renderOption.setEmitterID(ei.getID());
      renderOption.setOutputFileName(outputfilepath);
      renderOption.setBaseURL(baseUrl);
      renderOption.setActionHandler(new MyHTMLActionHandler(webAppBaseUrl));
      renderTask.setRenderOption(renderOption);
    } else if("html".equalsIgnoreCase(format)) { // html format by default
      LOGGER.debug("Setting HTML RenderOption");
      HTMLRenderOption renderOption = new HTMLRenderOption();
      renderOption.setSupportedImageFormats("PNG;GIF;JPG;BMP");
      renderOption.setOutputFormat(ei.getFormat());
      renderOption.setEmitterID(ei.getID());
      renderOption.setEmbeddable(false);
      renderOption.setImageDirectory(imageFolder);
      renderOption.setHtmlPagination(true);
      renderOption.setMasterPageContent(true);
      renderOption.setImageHandler(new HTMLServerImageHandler());
      renderOption.setBaseImageURL(imageUrl);
      renderOption.setOutputFileName(outputfilepath);
      renderOption.setActionHandler(new MyHTMLActionHandler(webAppBaseUrl));
      renderOption.setBaseURL(baseUrl);
      renderTask.setRenderOption(renderOption);
    } else if ("pdf".equalsIgnoreCase(format)) {
      LOGGER.debug("Setting PDF RenderOption");
      PDFRenderOption renderOption = new PDFRenderOption();
      renderOption.setSupportedImageFormats("PNG;GIF;JPG;BMP");
      renderOption.setOutputFormat(ei.getFormat());
      renderOption.setEmitterID(ei.getID());
      renderOption.setOutputFileName(outputfilepath);
      renderOption.setBaseURL(baseUrl);
      renderOption.setActionHandler(new MyHTMLActionHandler(webAppBaseUrl));
      renderTask.setRenderOption(renderOption);
    } else if ("postscript".equalsIgnoreCase(format)) {
      LOGGER.debug("Setting postscript RenderOption");
      RenderOption renderOption = new RenderOption();
      renderOption.setSupportedImageFormats("PNG;GIF;JPG;BMP");
      renderOption.setOutputFormat(ei.getFormat());
      renderOption.setEmitterID(ei.getID());
      renderOption.setOutputFileName(outputfilepath);
      renderOption.setBaseURL(baseUrl);
      renderOption.setActionHandler(new MyHTMLActionHandler(webAppBaseUrl));
      renderTask.setRenderOption(renderOption);

    } else if ("xls".equalsIgnoreCase(format)) {
      LOGGER.debug("Setting XLS RenderOption");
      EXCELRenderOption renderOption = new EXCELRenderOption();
      renderOption.setOutputFormat(ei.getFormat());
      renderOption.setEmitterID(ei.getID());
      renderOption.setSupportedImageFormats("PNG;GIF;JPG;BMP");
      //renderOption.setEnableMultipleSheet(true);
      renderOption.setOfficeVersion("2007");
      renderOption.setOutputFileName(outputfilepath);
      renderOption.setBaseURL(baseUrl);
      renderOption.setActionHandler(new MyHTMLActionHandler(webAppBaseUrl));
      renderTask.setRenderOption(renderOption);
      LOGGER.debug("ExcelRenderOption: officeversion="+renderOption.getOfficeVersion());
    } else if ("xlsx".equalsIgnoreCase(format)) {
      LOGGER.debug("Setting XLSX RenderOption");
      RenderOption renderOption = new RenderOption();
      renderOption.setOutputFormat(ei.getFormat());
      renderOption.setEmitterID(ei.getID());
      renderOption.setSupportedImageFormats("PNG;GIF;JPG;BMP");
      //renderOption.setEnableMultipleSheet(true);
      //renderOption.setOfficeVersion("office2007");
      renderOption.setOutputFileName(outputfilepath);
      renderOption.setBaseURL(baseUrl);
      renderOption.setActionHandler(new MyHTMLActionHandler(webAppBaseUrl));
      renderTask.setRenderOption(renderOption);
    } else if ("xls_pulse".equalsIgnoreCase(format)) {
      LOGGER.debug("Setting XLS_PULSE RenderOption");
      EXCELRenderOption renderOption = new EXCELRenderOption();
      renderOption.setOutputFormat(ei.getFormat());
      renderOption.setEmitterID(ei.getID());
      renderOption.setSupportedImageFormats("PNG;GIF;JPG;BMP");
      //renderOption.setEnableMultipleSheet(true);
      renderOption.setOfficeVersion("office2007");
      renderOption.setOutputFileName(outputfilepath);
      renderOption.setBaseURL(baseUrl);
      renderOption.setActionHandler(new MyHTMLActionHandler(webAppBaseUrl));
      renderTask.setRenderOption(renderOption);
    } else if ("xls_spudsoft".equalsIgnoreCase(format)) {
      LOGGER.debug("Setting XLS_SPUDSOFT RenderOption");
      RenderOption renderOption = new RenderOption();
      renderOption.setOutputFormat(ei.getFormat());
      renderOption.setEmitterID(ei.getID());
      renderOption.setSupportedImageFormats("PNG;GIF;JPG;BMP");
      //renderOption.setEnableMultipleSheet(true);
      //renderOption.setOfficeVersion("office2003");
      renderOption.setOutputFileName(outputfilepath);
      renderOption.setBaseURL(baseUrl);
      renderOption.setActionHandler(new MyHTMLActionHandler(webAppBaseUrl));
      renderTask.setRenderOption(renderOption);
    } else if ("xls_tribix".equalsIgnoreCase(format)) {
      LOGGER.debug("Setting XLS_TRIBIX RenderOption");
      RenderOption renderOption = new RenderOption();
      renderOption.setOutputFormat("xls");
      renderOption.setEmitterID(ei.getID());
      renderOption.setSupportedImageFormats("PNG;GIF;JPG;BMP");
      //renderOption.setEnableMultipleSheet(true);
      //renderOption.setOfficeVersion("office2003");
      renderOption.setOutputFileName(outputfilepath);
      renderOption.setBaseURL(baseUrl);
      renderOption.setActionHandler(new MyHTMLActionHandler(webAppBaseUrl));
      renderTask.setRenderOption(renderOption);
    } else if ("odp".equalsIgnoreCase(format)) {
      LOGGER.debug("Setting opendocument presentation RenderOption");
      RenderOption renderOption = new RenderOption();
      renderOption.setSupportedImageFormats("PNG;GIF;JPG;BMP");
      renderOption.setOutputFormat(ei.getFormat());
      renderOption.setEmitterID(ei.getID());
      renderOption.setOutputFileName(outputfilepath);
      renderOption.setBaseURL(baseUrl);
      renderOption.setActionHandler(new MyHTMLActionHandler(webAppBaseUrl));
      renderTask.setRenderOption(renderOption);
    } else if ("odt".equalsIgnoreCase(format)) {
      LOGGER.debug("Setting opendocument text RenderOption");
      RenderOption renderOption = new RenderOption();
      renderOption.setSupportedImageFormats("PNG;GIF;JPG;BMP");
      renderOption.setOutputFormat(ei.getFormat());
      renderOption.setEmitterID(ei.getID());
      renderOption.setOutputFileName(outputfilepath);
      renderOption.setBaseURL(baseUrl);
      renderOption.setActionHandler(new MyHTMLActionHandler(webAppBaseUrl));
      renderTask.setRenderOption(renderOption);
    } else if ("ods".equalsIgnoreCase(format)) {
      LOGGER.debug("Setting opendocument spreadsheet RenderOption");
      RenderOption renderOption = new RenderOption();
      renderOption.setSupportedImageFormats("PNG;GIF;JPG;BMP");
      renderOption.setOutputFormat(ei.getFormat());
      renderOption.setEmitterID(ei.getID());
      renderOption.setOutputFileName(outputfilepath);
      renderOption.setBaseURL(baseUrl);
      renderOption.setActionHandler(new MyHTMLActionHandler(webAppBaseUrl));
      renderTask.setRenderOption(renderOption);
    } else { // html format by default
      LOGGER.debug("Setting HTML RenderOption");
      HTMLRenderOption renderOption = new HTMLRenderOption();
      renderOption.setSupportedImageFormats("PNG;GIF;JPG;BMP");
      renderOption.setOutputFormat(ei.getFormat());
      renderOption.setEmitterID(ei.getID());
      renderOption.setEmbeddable(false);
      renderOption.setImageDirectory(imageFolder);
      renderOption.setHtmlPagination(true);
      renderOption.setMasterPageContent(true);
      renderOption.setImageHandler(new HTMLServerImageHandler());
      renderOption.setBaseImageURL(imageUrl);
      renderOption.setOutputFileName(outputfilepath);
      renderOption.setActionHandler(new MyHTMLActionHandler(webAppBaseUrl));
      renderOption.setBaseURL(baseUrl);
      renderTask.setRenderOption(renderOption);
    }
  }
}
