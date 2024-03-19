// Copyright (C) 2009-2023 Lemoine Automation Technologies
// Copyright (C) 2023 Atsora Solutions
//
// SPDX-License-Identifier: EPL-2.0

function ScalarParameter(element) {
  // Properties
  this._element = element;
  this.name = this._element.find('#name').attr('value');
  this.defaultvalue = this._element.find('#defaultvalue').attr('value');
  this.datatype = this._element.find('#datatype').attr('value');
  this.parametertype = this._element.find('#parametertype').attr('value');
  this.required = this._element.find('#required').attr('value');
  this.hidden = this._element.find('#hidden').attr('value') == 'true';
  this.helptext = this._element.find('#helptext').attr('value');
  this.value = null;

  // Methods
  this._setParametersErrorMsg = function () {
    if (this.value == null || this.value.length == 0) {
      if (this.required == 'true') {
        this._element.find('.parametererrormsg').show().html('This parameter must be specified');
      }
    } else {
      for (var i = 0; i < this.value.length; i++) {
        if (!utils.isValidValue(this.value[i], this.datatype)) {
          this._element.find('.parametererrormsg').show().html('Incorrect value');
          break;
        }
      }
    }
  };

  this.initialize = function () {
    switch (this._element.find('#widget').attr('value').toUpperCase()) {
      case 'HIDDEN':
        var value = this._element.find('#' + this.name + '_value').attr('value');
        if (value != '') {
          this.value = new Array();
          this.value[0] = value;
        }
        break;
      case 'TEXTBOX':
        var value = this._element.find('#' + this.name + '_value').val();
        if (value != '') {
          this.value = new Array();
          this.value[0] = value;
        }
        break;
      case 'RADIOBTN':
        var myRadio = this._element.find('input[name=' + this.name + '_value]');
        this.value = new Array();
        this.value[0] = myRadio.filter(':checked').val();
        break;
      case 'DATEBOX': {
        //var date = $("#NEW" + this.name)[0].getValueAsIs();
        let self = this;
        $('x-reportdatetime').each(function () {
          let date = this.getValueAsIs(self.name);
          if (date != null && date != '') { //.isValid()) {
            self.value = new Array();
            self.value[0] = date; //.format('YYYY-MM-DD');
          }
        });
      } break;
      case 'TIMEBOX': {
        var time = $("#NEW" + this.name)[0].getValueAsIs();
        if (time != null && time.isValid()) {
          this.value = new Array();
          this.value[0] = time.format('HH:mm:ss'); // Remove .SSS');
        }
      } break;
      case 'DATETIMEBOX': {
        let self = this;
        $('x-reportdatetime').each(function () {
          let date = this.getValueAsIs(self.name);
          if (date != null && date != '') { //.isValid()) {
            self.value = new Array();
            self.value[0] = date; //.format('YYYY-MM-DD HH:mm:ss'); // Remove .SSS');  Not supported by BIRT
          }
        });
      } break;
      case 'LISTBOX':
        var optionValue;
        if (this.parametertype.toUpperCase() == 'SIMPLE') {
          optionValue = this._element.find("select").val();
          if (optionValue != '__NULL') {
            this.value = new Array();
            this.value[0] = optionValue;
          }
        } else {
          var array = new Array();
          this._element.find("select option:selected").each(function () {
            optionValue = $(this).attr('value');
            if (optionValue != '__NULL')
              array[array.length] = optionValue;
          });
          if (array.length > 0)
            this.value = array;
        }
        break;
    }
    this._setParametersErrorMsg();
  };

  this.initializeTree = function (tree, allSelectedNodes, allPartialSelectedNodes) {
    // parameterkey is the scalar parameter level in the current parameter group
    var parameterkey = this._element.find('#parameterkey').attr('value');
    var valuearray = new Array();
    for (var i = 0; i < allSelectedNodes.length; i++) {
      var node = allSelectedNodes[i];
      if (node.getLevel() > 1) {
        if (node.getLevel() - 1 == parameterkey) {
          if (valuearray.indexOf(node.data.key) < 0) {
            valuearray.push(node.data.key);
          }
        }
      }
    }
    for (var i = 0; i < allPartialSelectedNodes.length; i++) {
      var node = allPartialSelectedNodes[i];
      if (node.getLevel() > 1) {
        if (node.getLevel() - 1 == parameterkey) {
          if (valuearray.indexOf(node.data.key) < 0) {
            valuearray.push(node.data.key);
          }
        }
      }
    }

    // This code block is used to count node for this level
    var nodecount = 0;
    tree.visit(function (node) {
      if (node.getLevel() - 1 == parameterkey)
        nodecount++;
    });
    if (this.parametertype.toUpperCase() == 'SIMPLE') {
      this.value = valuearray;
    } else if (this.parametertype.toUpperCase() == 'MULTI_VALUE') {
      if (valuearray.length == nodecount) {
        this.value = ["-1"];
      } else {
        this.value = valuearray;
      }
    }
  };

  this.initializeDateRange = function (groupName) {
    var array = new Array();
    switch (this._element.find('#parameterkey').attr('value')) {
      case 'MINDATE':
        if (this.datatype == 'DATE') {
          var date = $('#reportdatetimegroup_' + groupName)[0].getMinValueAsIs();
          if (date != null && date != '') { //.isValid()) {
            this.value = new Array();
            this.value[0] = date; //.format('YYYY-MM-DD');
          }
        } else if (this.datatype == 'DATETIME') {
          var datetime = $('#reportdatetimegroup_' + groupName)[0].getMinValueAsIs();
          if (datetime != null && datetime != '') { //.isValid()) {
            this.value = new Array();
            this.value[0] = datetime; //.format('YYYY-MM-DD HH:mm:ss'); // Remove .SSS'); Not supported by BIRT
          }
        }
        break;
      case 'MAXDATE':
        if (this.datatype == 'DATE') {
          var date = $('#reportdatetimegroup_' + groupName)[0].getMaxValueAsIs();
          if (date != null && date != '') { //.isValid()) {
            this.value = new Array();
            this.value[0] = date; //.format('YYYY-MM-DD');
          }
        } else if (this.datatype == 'DATETIME') {
          var datetime = $('#reportdatetimegroup_' + groupName)[0].getMaxValueAsIs();
          if (datetime != null && datetime != '') { //.isValid()) {
            this.value = new Array();
            this.value[0] = datetime; //.format('YYYY-MM-DD HH:mm:ss'); // Remove .SSS'); Not supported by BIRT
          }
        }
        break;
      default:
        // ex = WebAppParamsDate / WebAppParamsDateTime
        if (this.name.includes('WebAppParams')) {
          var webAppParam = $('#reportdatetimegroup_' + groupName)[0].getWebAppRange();
          if (webAppParam != null && webAppParam != '') { //.isValid()) {
            this.value = new Array();
            this.value[0] = webAppParam;
          }
        }
        else {
          this.value = new Array();
          this.value[0] = this._element.find('#' + this.name + "_value").attr('value');
        }
        break;
    }
    this._setParametersErrorMsg();
  };

  this.toString = function () {
    return 'name: ' + this.name +
      ' - value: ' + this.value +
      ' - default value: ' + this.defaultvalue +
      ' - data type: ' + this.datatype +
      ' - parameter type: ' + this.parametertype +
      ' - required: ' + this.required +
      ' - hidden: ' + this.hidden +
      ' - help text: ' + this.helptext;
  };

  this.isValid = function () {
    if (this.value == null || this.value.length == 0) {
      if (this.required == 'true') {
        console.warn(this.name + " has a null value but a value is required.");
        return false;
      } else {
        return true;
      }
    } else {
      if (this.parametertype == 'SIMPLE') {
        if (utils.isValidValue(this.value[0], this.datatype)) {
          return true;
        } else {
          console.warn(this.name + " has an invalid value '" + this.value[0] + "'");
          return false;
        }
      } else if (this.parametertype == 'MULTI_VALUE') {
        for (var i = 0; i < this.value.length; i++) {
          if (!utils.isValidValue(this.value[i], this.datatype)) {
            console.warn(this.name + " has an invalid value '" + this.value[i] + "'");
            return false;
          }
        }
        return true;
      }
    }
  }

  this.validate = function () {
    var isValid = this.hidden || this.isValid();
    if (isValid)
      this._element.find('.parameterContent').removeClass('invalidparameterborder');
    else
      this._element.find('.parameterContent').addClass('invalidparameterborder');
    return isValid;
  };

  this.getQueryParametersString = function () {
    var queryParameters = "";
    if (this.value != undefined && this.value != null && (this.value != '' || this.required == 'true')) {
      for (var i = 0; i < this.value.length; i++) {
        if (this.name == "PulseLogin")
          continue;
        queryParameters += '&' + this.name + '=' + utils.encodeSingleValue(this.value[i]);
      }
    }
    return queryParameters;
  };
}

////////// ////////// ////////// ////////// ////////// ////////// 

function GroupParameter(element) {
  // Properties
  this._element = element;
  this.name = this._element.find('#groupname').attr('value');
  this.displayform = this._element.find('#displayform').attr('value');
  this.helptext = this._element.find('#helptext').attr('value');
  this._keys = new Array();
  this._scalarparameters = new Array();
  this._errorMessage = "";

  // Methods
  this._initialize = function () {
    var groupParameter = this;
    switch (this.displayform) {
      case 'DATERANGE':
        var groupName = this.name;
        this._element.find('.parameter').each(function () {
          var aScalarParameter = new ScalarParameter($(this));
          aScalarParameter.initializeDateRange(groupName);
          groupParameter._addScalarParameter($(this).find('#parameterkey').attr('value'), aScalarParameter);
        });
        break;
      case 'TREEVIEW': case 'TREEVIEW_BY_LEAF':
        var tree;
        var allSelectedNodes;
        if (this._element.find('.treeviewparameter').length > 0) {
          tree = this._element.find('.treeviewparameter').dynatree("getTree");
          allSelectedNodes = this._element.find('.treeviewparameter').dynatree("getSelectedNodes");
        } else {
          tree = this._element.find('.treeviewparametersingle').dynatree("getTree");
          allSelectedNodes = this._element.find('.treeviewparametersingle').dynatree("getSelectedNodes");
        }
        var allPartialSelectedNodes = new Array();
        this._element.find(".dynatree-partsel").each(function () {
          allPartialSelectedNodes[allPartialSelectedNodes.length] = $.ui.dynatree.getNode(this);
        });
        this._element.find('.parameter').each(function () {
          var aScalarParameter = new ScalarParameter($(this));
          aScalarParameter.initializeTree(tree, allSelectedNodes, allPartialSelectedNodes);
          groupParameter._addScalarParameter($(this).find('#parameterkey').attr('value'), aScalarParameter);
        });
        break;
      default:
        this._element.find('.parameter').each(function () {
          var aScalarParameter = new ScalarParameter($(this));
          aScalarParameter.initialize();
          groupParameter._addScalarParameter(aScalarParameter.name, aScalarParameter);
        });
        break;
    }
  };

  this.toString = function () {
    var str = 'name: ' + this.name + ' - display form: ' + this.displayform;
    for (var i = 0; i < this._keys.length; i++)
      str += '\n * ' + this._keys[i] + ': ' + this._scalarparameters[i].toString();

    str += ' - help text: ' + this.helptext;
    return str;
  };

  this._addScalarParameter = function (key, scalarParameter) {
    this._keys[this._keys.length] = key;
    this._scalarparameters[this._scalarparameters.length] = scalarParameter;
  };

  this.getScalarParameter = function (key) {
    var index = this._keys.indexOf(key);
    return (index >= 0) ? this._scalarparameters[index] : null;
  };

  this.isValid = function () {
    this._errorMessage = "";
    switch (this.displayform) {
      case 'DATERANGE':
        // Min and max date must be provided
        var mindateparameter = this.getScalarParameter('MINDATE');
        var maxdateparameter = this.getScalarParameter('MAXDATE');
        var minDateInvalid = mindateparameter == null || mindateparameter.value == null || !mindateparameter.isValid();
        var maxDateInvalid = maxdateparameter == null || maxdateparameter.value == null || !maxdateparameter.isValid();

        if (minDateInvalid && maxDateInvalid) {
          this._errorMessage = 'Start and end dates must be provided.';
          return false;
        } else if (minDateInvalid) {
          this._errorMessage = 'Start date must be provided.';
          return false;
        } else if (maxDateInvalid) {
          this._errorMessage = 'End date must be provided.';
          return false;
        } else if (moment(maxdateparameter.value[0]).isBefore(mindateparameter.value[0])) {
          this._errorMessage = 'Start date should come before end date.';
          return false;
        }
        break;
      case 'MACHINES': case 'ACCORDION':
        // Check all individual parameters
        for (var i = 0; i < this._scalarparameters.length; i++) {
          var parameter = this._scalarparameters[i];
          if (!parameter.isValid())
            return false;
          else if (i == this._scalarparameters.length - 1) {
            // If a unique element is needed, it must be specified
            if (parameter.parametertype == "SIMPLE" &&
              (parameter.value[0] == "_NULL" || parameter.value[0] == -1)) {
              this._errorMessage = (this.displayform == "MACHINES" ? "A machine" : "An element") + " must be selected";
              return false;
            }
          }
        }
        break;
      default:
        for (var i = 0; i < this._scalarparameters.length; i++)
          if (!this._scalarparameters[i].isValid())
            return false;
        break;
    }
    return true;
  };

  this.validate = function () {
    var valid = this.isValid();
    if (valid) {
      this._element.find('.parameterGroupContent').removeClass('invalidparameterborder');
      this._element.find('.parametergrouperrormsg').hide();
    } else {
      this._element.find('.parameterGroupContent').addClass('invalidparameterborder');
      if (this._errorMessage != "") {
        this._element.find('.parametergrouperrormsg').show();
        this._element.find('.parametergrouperrormsg').text(this._errorMessage);
      } else
        this._element.find('.parametergrouperrormsg').hide();
    }
    return valid;
  };

  this.getQueryParametersString = function () {
    var queryParameters = "";
    for (var i = 0; i < this._scalarparameters.length; i++)
      queryParameters += this._scalarparameters[i].getQueryParametersString();
    return queryParameters;
  }

  // Initialization of a group and validation
  this._initialize();
}

////////// ////////// ////////// ////////// ////////// ////////// 

function ReportTemplate() {
  // Properties (everything should be readonly)
  this.name;
  this.scalarparameters = new Array();
  this.groupparameters = new Array();
  this.isValid = false;

  // Methods
  this._initialize = function () {
    // Reset the error messages
    $('.parametererrormsg').each(function () {
      $(this).css('display', 'none').text('');
    });
    $('.parametergrouperrormsg').each(function () {
      $(this).css('display', 'none').text('');
    });

    // Read the global information
    this.name = $('#reporttemplateid').attr('value');

    try {
      // Read all parameter groups
      var reportTemplate = this;
      $('#parameterslist > .parameterGroup').each(function () {
        reportTemplate.groupparameters[reportTemplate.groupparameters.length] = new GroupParameter($(this));
      });

      // Read all scalar parameters
      $('#parameterslist > .parameter').each(function () {
        var aScalarParameter = new ScalarParameter($(this));
        aScalarParameter.initialize();
        reportTemplate.scalarparameters[reportTemplate.scalarparameters.length] = aScalarParameter;
      });

      this.isValid = true;
    } catch (e) {
      console.error(e.message);
      this.groupparameters = new Array();
      this.scalarparameters = new Array();
      this.isValid = false;
    }
  };

  /**
   * Check if all of the report template have valid values
   * This function highlight parameter which are not valids
   * @param reportTemplate a ReportTemplate object
   * @returns {Boolean}
   */
  this._validate = function () {
    try {
      var error_msg = "Following parameters are not valid:";

      // Loop on all scalar parameters
      for (var i = 0; i < this.scalarparameters.length; i++) {
        if (!this.scalarparameters[i].validate()) {
          this.isValid = false;
          error_msg += "\n - " + this.scalarparameters[i].name;
        }
      }

      // Loop on all parameter groups
      for (var i = 0; i < this.groupparameters.length; i++) {
        if (!this.groupparameters[i].validate()) {
          this.isValid = false;
          error_msg += "\n - " + this.groupparameters[i].name;
        }
      }

      if (!this.isValid) {
        console.error(error_msg);
      }
    } catch (e) {
      console.error(e.message);
      this.isValid = false;
    }
  };

  this.toString = function () {
    var str = 'name: ' + this.name;
    for (var i = 0; i < this.scalarparameters.length; i++)
      str += '\n' + this.scalarparameters[i].toString();
    for (var i = 0; i < this.groupparameters.length; i++)
      str += '\n' + this.groupparameters[i].toString();
    return str;
  };

  this.getQueryParametersString = function () {
    var queryparameters = "";
    for (var i = 0; i < this.scalarparameters.length; i++)
      queryparameters += this.scalarparameters[i].getQueryParametersString();
    for (var i = 0; i < this.groupparameters.length; i++)
      queryparameters += this.groupparameters[i].getQueryParametersString();
    return queryparameters;
  };

  // Initialization of the structure (constructor) and validation
  this._initialize();
  if (this.isValid)
    this._validate();
}