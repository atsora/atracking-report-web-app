// Copyright (C) 2009-2023 Lemoine Automation Technologies
// Copyright (C) 2023 Atsora Solutions
//
// SPDX-License-Identifier: EPL-2.0

package eu.atsora.tracking.reporting.domain;

import java.io.Serializable;
import java.util.LinkedHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.birt.report.engine.api.IScalarParameterDefn;

import eu.atsora.tracking.reporting.util.Utils;

public class ScalarParameter implements Serializable, Comparable<ScalarParameter>
{
  private static final long serialVersionUID = -568496278919298467L;
  static Logger logger = LogManager.getLogger(ScalarParameter.class.getName());

  /**
   * Give which kind of widget should be used to represent this scalar parameter
   */
  public enum ControlType {
    TEXT_BOX,
    LIST_BOX,
    CHECK_BOX,
    RADIO_BTN
  };

  /**
   * Data displayForm of value for a scalar parameter
   */
  public enum DataType {
    STRING,
    INTEGER,
    FLOAT,
    DECIMAL,
    DATE,
    TIME,
    DATETIME,
    BOOLEAN,
    ANY
  };

  /**
   * Different displayForm of scalar parameter. Most often, scalar parameter has SIMPLE displayForm
   */
  public enum ParameterType {
    SIMPLE, // The scalar parameter has an unique value
    MULTI_VALUE, // The scalar parameter can have a set of value
    AD_HOC // Another mystery in life
  };

  // Name of the parameter
  String _name;
  public String getName() { return _name; }
  public void setName(String name) { _name = name; }

  // Default value set in the report
  String[] _defaultValue;
  public String[] getDefaultValue() { return _defaultValue; }
  public void setDefaultValue(String[] defaultValues) { _defaultValue = fixValues(defaultValues); }
  public boolean hasdefaultvalue() { return ( _defaultValue != null && _defaultValue[0] != "undefined" ); }

  // Current value set by the user
  String[] _value;
  public String[] getValue() { return _value; }
  public void setValue(String[] values) { _value = fixValues(values); }

  // Define the way the parameter will be editable on the parameter panel
  String _displayFormat;
  public String getDisplayFormat() { return _displayFormat; }
  public void setDisplayFormat(String displayFormat) { _displayFormat = displayFormat; }

  String _promptText;
  public String getPromptText() { return _promptText; }
  public void setPromptText(String promptText) { _promptText = promptText; }

  // True if the parameter must be hidden to the user on the parameter panel
  boolean _hidden;
  public boolean isHidden() { return _hidden; }
  public void setHidden(boolean hidden) { _hidden = hidden; }

  // True if the user must choose a value before viewing a report
  boolean _required;
  public boolean isRequired() { return _required; }
  public void setRequired(boolean required) { _required = required; }

  ControlType _controlType;
  public ControlType getControlType() { return _controlType; }
  public void setControlType(ControlType controlType) { _controlType = controlType; }

  // Kind of data we are dealing with
  DataType _dataType;
  public DataType getDataType() { return _dataType; }
  public void setDataType(DataType dataType) { _dataType = dataType; }

  // Defines if there is only one value that can be chosen
  ParameterType _parameterType;
  public ParameterType getParameterType() { return _parameterType; }
  public void setParameterType(ParameterType parameterType) { _parameterType = parameterType; }

  // Help Text
  String _helpText;
  public String getHelpText() { return _helpText; }
  public void setHelpText(String helpText) { _helpText = helpText; }

  // Scalar parameter group if it belongs in a group of parameter, else this field is null
  String _groupName;
  public String getGroupName() { return _groupName; }
  public void setGroupName(String groupName) { _groupName = groupName; }

  // Map for parameter value selection. It contains a list of parameter values and label values can be retrieved.
  LinkedHashMap<String, String> _selectionList;
  public LinkedHashMap<String, String> getSelectionList() { return _selectionList; }
  public void setSelectionList(LinkedHashMap<String, String> selectionList) { _selectionList = selectionList; }

  // Constructors
  public ScalarParameter() {}

  public ScalarParameter(String name)
  {
    _name = name;
    _selectionList = new LinkedHashMap<String, String>();
  }

  public ScalarParameter(ScalarParameter sp)
  {
    _name = sp._name;
    _defaultValue = sp._defaultValue;
    _value = sp._value;
    _displayFormat = sp._displayFormat;
    _groupName = sp._groupName;
    _promptText = sp._promptText;
    _hidden = sp._hidden;
    _required = sp._required;
    _controlType = sp._controlType;
    _dataType = sp._dataType;
    _parameterType = sp._parameterType;
    _selectionList = new LinkedHashMap<String, String>(sp._selectionList.size());
    for (String key : sp._selectionList.keySet()) {
      _selectionList.put(key, sp._selectionList.get(key));
    }
  }

  String[] fixValues(String[] values)
  {
    // Maybe nothing to return
    if (values == null || values.length == 0 || (values.length == 1 && values[0].equals("")))
      return null;

    // Or adapt the values according to the expected type
    switch (_dataType)
    {
      case INTEGER:
        for (int i = 0; i < values.length; i++) {
          try {
            Double d = Double.parseDouble(values[i]);
            values[i] = Integer.toString(d.intValue());
          } catch (Exception e) {}
        }
        break;
      case FLOAT:
        for (int i = 0; i < values.length; i++) {
          try {
            Float f = Float.parseFloat(values[i]);
            values[i] = f.toString();
          } catch (Exception e) {}
        }
        break;
      case DECIMAL:
        for (int i = 0; i < values.length; i++) {
          try {
            Double d = Double.parseDouble(values[i]);
            values[i] = d.toString();
          } catch (Exception e) {}
        }
        break;
      case BOOLEAN:
        for (int i = 0; i < values.length; i++) {
          try {
            Boolean b = Boolean.parseBoolean(values[i]);
            values[i] = b.toString();
          } catch (Exception e) {}
        }
        break;
      case STRING: case DATE: case TIME: case DATETIME: case ANY: default:
        break;
    }

    return values;
  }

  /**
   * Tell whether this parameter is multi-valued
   */
  public boolean isMultiSelect()
  {
    return _parameterType == ParameterType.MULTI_VALUE;
  }

  /**
   * Return control displayForm of a scalar parameter given an integer
   * 
   * @param controlType
   * @return control displayForm of a scalar parameter
   */
  public static ScalarParameter.ControlType getControlType(int controlType)
  {
    switch (controlType) {
      case IScalarParameterDefn.TEXT_BOX:
        return ScalarParameter.ControlType.TEXT_BOX;
      case IScalarParameterDefn.LIST_BOX:
        return ScalarParameter.ControlType.LIST_BOX;
      case IScalarParameterDefn.CHECK_BOX:
        return ScalarParameter.ControlType.CHECK_BOX;
      case IScalarParameterDefn.RADIO_BUTTON:
        return ScalarParameter.ControlType.RADIO_BTN;
      default:
        return ScalarParameter.ControlType.TEXT_BOX;
    }
  }

  /**
   * Return displayForm of value of a scalar parameter given an integer
   * 
   * @param dataType
   * @return displayForm of value of scalar parameter
   */
  public static ScalarParameter.DataType getDataType(int dataType)
  {
    switch (dataType) {
      case IScalarParameterDefn.TYPE_STRING:
        return ScalarParameter.DataType.STRING;
      case IScalarParameterDefn.TYPE_INTEGER:
        return ScalarParameter.DataType.INTEGER;
      case IScalarParameterDefn.TYPE_FLOAT:
        return ScalarParameter.DataType.FLOAT;
      case IScalarParameterDefn.TYPE_DECIMAL:
        return ScalarParameter.DataType.DECIMAL;
      case IScalarParameterDefn.TYPE_DATE:
        return ScalarParameter.DataType.DATE;
      case IScalarParameterDefn.TYPE_TIME:
        return ScalarParameter.DataType.TIME;
      case IScalarParameterDefn.TYPE_DATE_TIME:
        return ScalarParameter.DataType.DATETIME;
      case IScalarParameterDefn.TYPE_BOOLEAN:
        return ScalarParameter.DataType.BOOLEAN;
      default:
        return ScalarParameter.DataType.ANY;
    }
  }

  /**
   * Return parameter displayForm of a scalar parameter given a string
   * 
   * @param parameterType
   * @return parameter displayForm of a scalar parameter
   */
  public static ScalarParameter.ParameterType getParameterType(String parameterType)
  {
    if (parameterType.equalsIgnoreCase("simple"))
      return ScalarParameter.ParameterType.SIMPLE;
    if (parameterType.equalsIgnoreCase("multi-value"))
      return ScalarParameter.ParameterType.MULTI_VALUE;
    return ScalarParameter.ParameterType.AD_HOC;
  }

  public String determineWidget()
  {
    // ListBox widget?
    if (_controlType == ControlType.LIST_BOX || _controlType == ControlType.RADIO_BTN)
      return "listbox";
    
    // TextBox widget?
    if (_controlType == ControlType.TEXT_BOX && (_dataType == DataType.DECIMAL || _dataType == DataType.FLOAT || _dataType == DataType.INTEGER || _dataType == DataType.STRING))
      return "textbox";
    
    // RadioBtn widget?
    if (_controlType == ControlType.CHECK_BOX && _dataType == DataType.BOOLEAN)
      return "radiobtn";
    
    // DateTime widget?
    if (_controlType == ControlType.TEXT_BOX && _dataType == DataType.DATETIME)
      return "datetimebox";
    
    // Date widget?
    if (_controlType == ControlType.TEXT_BOX && _dataType == DataType.DATE)
      return "datebox";
    
    // Time widget?
    if (_controlType == ControlType.TEXT_BOX && _dataType == DataType.TIME)
      return "timebox";
    
    logger.warn("Unable to determine widget relate to parameter " + _name);
    return "textbox";
  }

  /**
   * Tell whether given value is correct according to given data type
   * 
   * @param dataType
   *          data type
   * @param value
   *          parameter value
   * @return true given value is compatible with given data type
   */
  public static boolean validateValue(DataType dataType, String value)
  {
    boolean isvalid = true;
    switch (dataType) {
      case STRING:
        break;
      case INTEGER:
        try {
          Double d = Double.parseDouble(value);
          return (d == d.intValue());
        }
        catch (NumberFormatException e) {
          isvalid = false;
        }
        break;
      case FLOAT:
        try {
          Float.parseFloat(value);
        }
        catch (NumberFormatException e) {
          isvalid = false;
        }
        break;
      case DECIMAL:
        try {
          Double.parseDouble(value);
        }
        catch (NumberFormatException e) {
          isvalid = false;
        }
        break;
      case DATE:
        isvalid = (value == null || value.isEmpty() || Utils.parseDate(value) != null);
        break;
      case TIME:
        isvalid = (value == null || value.isEmpty() || Utils.parseTime(value) != null);
        break;
      case DATETIME:
        isvalid = (value == null || value.isEmpty() || Utils.parseDateTime(value) != null);
        break;
      default:
        break;
    } // end of instruction switch
    return isvalid;
  }

  /**
   * Tell whether value assigned to the parameter is valid according his data
   * type and his others properties (required, control type,...)
   * 
   * 
   * @return true if assigned value is valid, false otherwise.
   */
  public boolean hasValidValue()
  {
    if (_value == null || _value.length == 0) {
      if (_hidden || !_required) {
        return true;
      } else if (_required) {
        logger.warn("Value of this parameter is required!");
        return false;
      }
    }

    boolean res = true;
    for (int i = 0; i < _value.length; i++)
      res &= validateValue(_dataType, _value[i]);
    
    return res;
  }

  @Override
  public int compareTo(ScalarParameter scalarParam) {
    return _name.compareTo(scalarParam._name);
  }

  @Override
  public String toString() {
    return "ScalarParameter [name=" + _name + ", defaultValue=" + Utils.formatArray(_defaultValue) +
     ", value=" + Utils.formatArray(_value) + ", groupName=" + _groupName +
     ", selectionList=" + _selectionList + ", hidden=" + _hidden + ", required=" + _required + ", controlType=" + _controlType +
     ", dataType=" + _dataType + ", parameterType=" + _parameterType + "]";
  }
}
