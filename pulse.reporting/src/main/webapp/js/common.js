// Copyright (C) 2009-2023 Lemoine Automation Technologies
//
// SPDX-License-Identifier: EPL-2.0

/*
 * This Block is a workaround to insert indexOf implementation in browser which 
 * do not natively support it. Internet Explorer 8 and earlier do not implement this function.
 */
if (!Array.prototype.indexOf) {
  Array.prototype.indexOf = function (searchElement /*, fromIndex */) {
    "use strict";
    if (this == null) {
      throw new TypeError();
    }
    var t = Object(this);
    var len = t.length >>> 0;
    if (len === 0) {
      return -1;
    }
    var n = 0;
    if (arguments.length > 1) {
      n = Number(arguments[1]);
      if (n != n) { // shortcut for verifying if it's NaN
        n = 0;
      } else if (n != 0 && n != Infinity && n != -Infinity) {
        n = (n > 0 || -1) * Math.floor(Math.abs(n));
      }
    }
    if (n >= len) {
      return -1;
    }
    var k = n >= 0 ? n : Math.max(len - Math.abs(n), 0);
    for (; k < len; k++) {
      if (k in t && t[k] === searchElement) {
        return k;
      }
    }
    return -1;
  };
};

///////////
// Utils //
///////////

var utils = {
  isValidValue: function (value, datatype) {
    var result = false;
    switch (datatype) {
      case 'STRING':
        result = true;
        break;
      case 'FLOAT': case 'DECIMAL':
        if (undefined === value || null === value || '' == value) {
          result = false;
        } else if (typeof value == 'number') {
          result = true;
        } else
          result = !isNaN(value - 0);
        break;
      case 'INTEGER': case 'INT':
        result = this.isValidValue(value, 'FLOAT') && parseFloat(value) % 1 == 0;
        break;
      case 'DATE':
        result = moment(value, "yyyy-MM-dd") != null;
        break;
      case 'TIME':
        result = moment(value, ["HH:mm:ss", "HH:mm:ss.SSS"]) != null;
        break;
      case 'DATETIME':
        result = moment(value, ["YYYY-MM-DD HH:mm:ss", "YYYY-MM-DD HH:mm:ss.SSS"]) != null;
        break;
      case 'BOOLEAN':
        result = value.toUpperCase() == 'TRUE' || value.toUpperCase() == 'FALSE';
        break;
    }
    return result;
  },
  getLogin: function () {
    // Maybe the login is forced in the URL
    let getParam = function (paramName) {
      return (location.search.split(paramName + '=')[1] || '').split('&')[0];
    };
    let urlLogin = getParam('PulseLogin');
    if (urlLogin != '')
      return urlLogin;

    // Otherwise try to find it in the cookies
    let name = 'PulseLogin=';
    let decodedCookie = decodeURIComponent(document.cookie);
    let ca = decodedCookie.split(';');
    for (let i = 0; i < ca.length; i++) {
      let c = ca[i];
      while (c.charAt(0) == ' ') {
        c = c.substring(1);
      }
      if (c.indexOf(name) == 0) {
        return c.substring(name.length, c.length);
      }
    }
    return '';
  },
  insertLoginInQueryString: function (queryString) {
    // Concat all existing values without the login
    let keyValues = (queryString != null ? queryString : '').split('&');
    let result = '';
    for (let i = 0; i < keyValues.length; i++) {
      let keyValue = keyValues[i];
      let split = keyValue.split('=');
      if (split.length == 2) {
        let key = split[0];
        if (key.startsWith('?'))
          key = key.substring(1);
        let value = split[1];
        if (key != 'PulseLogin')
          result += '&' + key + '=' + value;
      }
    }

    let login = this.getLogin();
    if ('dev' != login && 'support' != login
      && 'Dev' != login && 'Support' != login) {
      // Add the login at the end
      result += '&PulseLogin=' + login;
    }

    // Remove the first '&' and return the result
    return result.substring(1);
  },
  encodeFullQueryString: function (queryString) {
    // Split + encode only values
    let keyValues = (queryString != null ? queryString : '').split('&');
    let result = '';
    for (let i = 0; i < keyValues.length; i++) {
      let keyValue = keyValues[i];
      let split = keyValue.split('=');
      if (split.length == 2) {
        let key = split[0];
        if (key.startsWith('?'))
          key = key.substring(1);
        let value = split[1];
        result += '&' + key + '=' + this.encodeSingleValue(value);
      }
    }
    return result;
  },
  encodeSingleValue: function (str) {
    // WARNING ! '+' and  '%20' difference between js and java for coding ' '
    var retVal = encodeURIComponent(str); // encodeURI(str); is not enough
    // Possible chars can be founded here :
    // https://www.w3docs.com/snippets/javascript/how-to-encode-javascript-url.html
    return retVal;
  },
  decodeSingleValue: function (str) {
    //let tmp = str.replace('+', '%20'); // Not needed here ? Maybe for compatibility
    var retVal = decodeURI(str);
    return retVal;
  }
}

/////////////
// Storage //
/////////////

var storageManager = {
  // These values are kept even if the browser is closed
  write: function (name, value) { localStorage.setItem(name, JSON.stringify(value)); },
  read: function (name) {
    try {
      return JSON.parse(localStorage.getItem(name));
    } catch (e) { }
    return null;
  },
  erase: function (name) { localStorage.removeItem(name); },

  // These values pertain to the current tab and then disappear
  writeLocal: function (name, value) { sessionStorage.setItem(name, JSON.stringify(value)); },
  readLocal: function (name) {
    try {
      return JSON.parse(sessionStorage.getItem(name));
    } catch (e) { }
    return null;
  },
  eraseLocal: function (name) { sessionStorage.removeItem(name); }
}

////////////
// Themes //
////////////

var themeManager = {
  _itemName: "theme",
  _defaultThemeName: "default", // == light (?)
  _version: "",

  load: function (name) {
    var oldTheme = storageManager.read(this._itemName);
    if (oldTheme != null && oldTheme != "" && oldTheme != this._defaultThemeName) {
      // Unload the previous theme
      $('link[rel=stylesheet][href*="css/' + oldTheme + '_theme.css"]').remove();
      // Unload for pulse exports
      $('link[rel=stylesheet][href*="css/style_' + 'dark' + '/pulse.exports.light.css"]').remove();
    }
    else {
      // For pulse exports
      $('link[rel=stylesheet][href*="css/style_' + 'light' + '/pulse.exports.light.css"]').remove();
    }

    // Find the version of the js and css if not already read
    if (this._version == null || this._version == "") {
      this._version = $('link[rel=stylesheet][href*="css/style.css"]').attr("href").split('=')[1];
    }

    // Load the new theme 
    if (name != null && name != "" && name != this._defaultThemeName) {
      // For pulse exports (light, before reporting)
      $('head').append('<link rel="stylesheet" type="text/css" href="css/style_' + name
        + '/pulse.exports.light.css?v=' + this._version + '">');
      $('head').append('<link rel="stylesheet" type="text/css" href="css/' + 'dark' + '_theme.css?v=' + this._version + '">');
    }
    else {
      // For pulse exports
      $('head').prepend('<link rel="stylesheet" type="text/css" href="css/style_' + 'light'
        + '/pulse.exports.light.css?v=' + this._version + '">');
    }

    if (name == null || name == "")
      storageManager.write(this._itemName, this._defaultThemeName);
    else
      storageManager.write(this._itemName, name);
  },
  initialize: function () {
    this.load(this.current());
  },
  current: function () {
    return storageManager.read(this._itemName);
  }
}

///////////////////////
// Range computation //
///////////////////////

var webServiceManager = {
  // Private attributes
  //_path: "",
  _timeout: 5000, // Default value

  // Initialize the path and then NO callback will be triggered:
  // * first argument is true if we have a timeout to the webservices
  // * second argument is timeout
  initializePath: function (/*path, */timeout) {
    //this._path = path + "/";
    if (timeout != '' && !isNaN(timeout))
      this._timeout = parseInt(timeout);
    //this._path = location.protocol + "//serveraddress:8081/"; // for DEBUG purposes only

    // Test the connection with the function returning "LoginRequired" - NOT Anymore !
  },
  getRange: function (callback, isPast, number, unit, isDate) {
    var minDateTime = moment();
    var maxDateTime = moment();
    var error = '';
    var path = '';
    // Search in URL
    let href = window.location.href;
    let idx = href.indexOf('?path=');
    if (idx == -1)
      idx = href.indexOf('&path=');
    if (idx != -1) {
      let endString = href.slice(idx + 6);
      let splittedArray = endString.split('&');
      path = splittedArray[0];
    }
    // Search in session storage (filled by x-checkpath)
    if (path == '' && typeof sessionStorage != 'undefined') {
      path = sessionStorage.getItem('path');
      if (path != null) {
        path = JSON.parse(path);
      }
    }
    if (path == '') {
      path = 'http://serveraddress:8081/'; // Default
    }
    var url = path //this._path 
      + "Time/" + (isPast ? "PastRange" : "CurrentRange") + "/" + number + "_" + unit;
    $.ajax({
      type: "GET",
      url: url,
      dataType: 'json',
      timeout: this._timeout,
      success: function (data) {
        if (isDate) {
          try {
            var tmp = data.DayRange.replace(/[\[\(\)\]]/g, '').split(',');
            minDateTime = moment(tmp[0], "YYYY-MM-DD");
            maxDateTime = moment(tmp[1], "YYYY-MM-DD");
          } catch (e) {
            console.error("Error when reading a date range: " + error + ", the result of the url '" + url + "' being '" + data.DayRange + "'");
            error = e.message;
          }
        } else {
          try {
            var tmp = data.LocalDateTimeRange.replace(/[\[\(\)\]]/g, '').split(',');
            minDateTime = moment(tmp[0], "YYYY-MM-DDTHH:mm:ss");
            maxDateTime = moment(tmp[1], "YYYY-MM-DDTHH:mm:ss");
          } catch (e) {
            console.error("Error when reading a date range: " + error + ", the result of the url '" + url + "' being '" + data.LocalDateTimeRange + "'");
            error = e.message;
          }
        }
      },
      error: function (jqXHR, textStatus, errorThrown) { error = textStatus; },
      complete: function (jqXHR, textStatus) { callback(minDateTime, maxDateTime, error); }
    });
  }
}

///////////////////////
// On document ready //
///////////////////////

function prepareConfigurationPanel() {
  // Open / close right menu
  $(window).click(function () {
    $('.dropdown-content-show').removeClass('dropdown-content-show');
    $('.dropdown-content-shown').removeClass('dropdown-content-shown');
  });
  $(".dropdown-button").click(function (e) {
    $(this).siblings().toggleClass("dropdown-content-show");
    $(this).toggleClass("dropdown-content-shown");
  });
  $('.dropdown-content').click(function (e) { e.stopPropagation(); });
  $('.dropdown-button').click(function (e) { e.stopPropagation(); });

  $("#help-icon").click(function (e) { // $('#help-div').click(function (e) {
    let pathname = window.location.pathname;
    let pdfPath = pathname.substring(0, pathname.lastIndexOf('/') + 1) + 'images/listreport.pdf';
    //let pageName = pulseConfig.getPageName();
    //let pdfPath = pathname.substring(0, pathname.lastIndexOf('/') + 1) + 'help/' + pageName + '.pdf';
    // Open help file (if exist)

    function _fileExists(url) {
      if (url) {
        var req = new XMLHttpRequest();
        req.open('HEAD', url, false); // head is faster than GET
        req.send();
        return req.status == 200;
      } else {
        return false;
      }
    }
    if (_fileExists(pdfPath)) {
      window.open(pdfPath, 'resizable,scrollbars');
    }
    else {
      customDialog.openInfo("File not found !");
    }
  });

  // Create a dialog for the configurations
  customDialog.initialize("#configurationpanel", {
    title: "Configuration",
    autoClose: true,
    cancelButton: "hidden"
  });
  $('#configurationbtn').click(function () { customDialog.open('#configurationpanel'); });

  // Theme switcher (to be adapted if more than 2 themes are available) and initialization
  $('#darkthemebtn').prop('checked', themeManager.current() == "dark");
  $('#darkthemebtn').click(function () { themeManager.load(themeManager.current() == "dark" ? "" : "dark"); });
  themeManager.initialize();

  // "New tab" option
  $('#newtabbtn').prop('checked', storageManager.read("new_tab") != "false");
  $('#newtabbtn').click(function () {
    var currentValue = storageManager.read("new_tab");
    storageManager.write("new_tab", currentValue == "false" ? "true" : "false");
  });
}

$(document).ready(function () {
  // Configuration panel
  prepareConfigurationPanel();

  var openLeftPanel = function () {
    $('#leftpanel').show();
    $("#inner").removeClass('leftpanel-collapsed');
    $("#menubtn").removeClass('disabled');
  };

  var closeLeftPanel = function () {
    $("#inner").addClass('leftpanel-collapsed');
    $("#menubtn").addClass('disabled');
  };

  // Show / hide left panel
  $("#menubtn").click(function (e) {
    if ($("#inner").hasClass('leftpanel-collapsed'))
      openLeftPanel();
    else
      closeLeftPanel();
  });

  // Recall left panel state?
  if (storageManager.readLocal("left_panel_state") == "collapsed")
    closeLeftPanel();
  else if (storageManager.readLocal("left_panel_state") == "expanded")
    openLeftPanel();

  // Remove the memorized panel state
  storageManager.eraseLocal("left_panel_state");

  // Always place the inner div just below the header
  var prevHeight = $('#header').height();
  $("#inner").css({ top: prevHeight + "px" });
  $(window).resize(function () {
    var curHeight = $('#header').height();
    if (prevHeight !== curHeight) {
      $("#inner").css({ top: curHeight + "px" });
      prevHeight = curHeight;
    }
  });
});