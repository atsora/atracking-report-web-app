// Copyright (C) 2009-2023 Lemoine Automation Technologies
//
// SPDX-License-Identifier: EPL-2.0

package com.lemoinetechnologies.pulse.reporting.domain;

import com.lemoinetechnologies.pulse.reporting.util.Utils;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ReportTemplate extends Report
{
  Logger logger = LogManager.getLogger(getClass());
  
  /**
   * List of scalar parameter
   */
  List<ScalarParameter> _scalarParameters = new LinkedList<ScalarParameter>();
  public List<ScalarParameter> getScalarParameters() { return _scalarParameters; }
  public void setScalarParameters(List<ScalarParameter> scalarParameters) { _scalarParameters = scalarParameters; }

  /**
   * List of group parameters
   */
  List<GroupParameter> _groupParameters = new LinkedList<GroupParameter>();
  public List<GroupParameter> getGroupParameters() { return _groupParameters; }
  public void setGroupParameters(List<GroupParameter> groupParameters) { _groupParameters = groupParameters; }

  /**
   * Constructors
   */
  public ReportTemplate(String reportId)
  {
    super(reportId);
  }

  public ReportTemplate(ReportTemplate report)
  {
    super(report);

    // Copy all scalar parameters and groups
    for (ScalarParameter scalarParameter : report.getScalarParameters())
      _scalarParameters.add(new ScalarParameter(scalarParameter));

    for (GroupParameter groupParameter : report.getGroupParameters())
      _groupParameters.add(new GroupParameter(groupParameter));
  }

  /**
   * Search parameter which given name and set his value
   * 
   * @param name
   *            parameter name
   * @param value
   *            parameter value
   */  
  public void setParameterValue(String name, String[] value)
  {  
    // The value is possibly empty
    if (value != null && value.length == 1 && value[0].equals(""))
      value = null;
    
    // Searching parameter in the scalar parameter list if found, exit the function
    for (ScalarParameter scalarparameter : _scalarParameters)
    {
      if (scalarparameter.getName().equals(name))
      {
        scalarparameter.setValue(value);
        return;
      }
    }

    // Searching parameter in the group parameter list if found, exit the function
    for (GroupParameter groupparameter : _groupParameters)
    {
      for (ScalarParameter scalarparameter : groupparameter.getScalarParameterMap().values())
      {
        if (scalarparameter.getName().equals(name))
        {
          scalarparameter.setValue(value);
          return;
        }
      }
    }
  }

  /**
   * Set value of report parameters which have name and values in the map.
   * 
   * @param parametersNameToValue
   *            Map which contains parameters name as key and theirs value in
   *            the value field.
   */
  public void setParameterValue(Map<String, String[]> parametersNameToValue)
  {
    for (String paramName : parametersNameToValue.keySet())
    {
      setParameterValue(paramName, parametersNameToValue.get(paramName));
    }
  }
  
  public ScalarParameter getScalarParameter(String name)
  {
    for (ScalarParameter scalarparameter : _scalarParameters)
    {
      if (scalarparameter.getName().equals(name))
      {
        return scalarparameter;
      }
    }
    for (GroupParameter groupparameter : _groupParameters)
    {
      for (ScalarParameter scalarparameter : groupparameter.getScalarParameterMap().values())
      {
        if (scalarparameter.getName().equals(name))
        {
          return scalarparameter;
        }
      }
    }
    return null;
  }

  public String getParametersQueryString(boolean encodeValue)
  {
    String queryparameters = "";
    String parametername;
	  String[] value;
	
    for (ScalarParameter scalarparameter : _scalarParameters) {
      // Some parameters will not be in the url
      parametername = scalarparameter.getName();
      if ("PulseLogin".equals(parametername) || "PulseInfoMessage".equals(parametername))
        continue;
        
      value = encodeValue ? Utils.encodeUrl(scalarparameter.getValue()) 
        : scalarparameter.getValue();
      if (value != null) {
        for (int i = 0; i < value.length; i++) {
          queryparameters += "&" + parametername + "=" + value[i];
        }
      }
    }
    
    for (GroupParameter groupparameter : _groupParameters) {
      for (ScalarParameter scalarparameter : groupparameter.getScalarParameterMap().values()) {
        // Some parameters will not be in the url
        parametername = scalarparameter.getName();
        if ("PulseLogin".equals(parametername) || "PulseInfoMessage".equals(parametername))
          continue;
        
        value = encodeValue ? Utils.encodeUrl(scalarparameter.getValue())
          : scalarparameter.getValue();
        if (value != null) {
          for (int i = 0; i < value.length; i++) {
            queryparameters += "&" + parametername + "=" + value[i];
          }
        }
      }
    }
    
    return (queryparameters.length() > 0) ? queryparameters.substring(1) : queryparameters;
  }

  public String[] validateParameterValue()
  {
    List<String> invalidparameters = new ArrayList<String>();
    for (ScalarParameter scalarparameter : _scalarParameters) {
      if (!scalarparameter.hasValidValue()) {
        String msg = "Invalid parameter value : " + scalarparameter.getName() + " - ";
        if (null != scalarparameter.getValue()) {
          for(int i = 0; i < scalarparameter.getValue().length; i++){
            msg = msg + "  " + scalarparameter.getValue()[i];
          }
        }
        logger.warn(msg);
        invalidparameters.add(scalarparameter.getName());
      }
    }
    
    for (GroupParameter groupparameter : _groupParameters) {
      for (ScalarParameter scalarparameter : groupparameter.getScalarParameterMap().values()) {
        if (!scalarparameter.hasValidValue()) {
          String msg = "Invalid parameter value : " + scalarparameter.getName() + " - ";
          if (null != scalarparameter.getValue()) {
            for (int i = 0; i < scalarparameter.getValue().length; i++) {
              msg = msg + "  " + scalarparameter.getValue()[i];
            }
          }
          logger.warn(msg);
          invalidparameters.add(scalarparameter.getName());
        }
      }
    }

    if (invalidparameters.size() == 0)
      return null;

    return invalidparameters.toArray(new String[invalidparameters.size()]);
  }
  
  @Override public String toString()
  {
    return "ReportTemplate [id=" + _id + ", title=" + _title + ", scalarParameters=" + _scalarParameters + ", groupParameters=" + _groupParameters + "]";
  }
}
