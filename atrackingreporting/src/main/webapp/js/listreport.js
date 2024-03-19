// Copyright (C) 2009-2023 Lemoine Automation Technologies
// Copyright (C) 2023 Atsora Solutions
//
// SPDX-License-Identifier: EPL-2.0

var xhr = null;

var uiManager = {
  // Public methods
  showHome: function () {
    this.closeAllDialogs();
    this._setFavoritePanelVisible(true);
  },
  showReport: function (loading) {
    this.closeAllDialogs();
    this._setFavoritePanelVisible(false);
    this._hideButtons();
    if (!loading) {
      $('#addfavoritebtn').show();
      $('#viewbtn').show();
    }
  },
  showFavorite: function (loading) {
    this.closeAllDialogs();
    this._setFavoritePanelVisible(false);
    this._hideButtons();
    if (!loading) {
      $('#updatefavoritebtn').show();
      $('#viewbtn').show();
    }
  },
  closeAllDialogs: function () { customDialog.closeAll(); },

  // Private methods
  _setFavoritePanelVisible: function (visible) {
    if (visible) {
      $('#reportdescription').hide();
      $('#reportactions').hide();
      $('#reportparameters').hide();
      $('#reportfavorites').show();
    } else {
      $('#reportfavorites').hide();
      $('#reportdescription').show();
      $('#reportactions').show();
      $('#reportparameters').show();
    }
  },
  _hideButtons: function () {
    $('#addfavoritebtn').hide();
    $('#updatefavoritebtn').hide();
    $('#viewbtn').hide();
  }
}

var menuManager = {
  // Public methods
  showFavorites: function () {
    if ($('#left-action-favorites').hasClass('enabled'))
      return;

    uiManager.closeAllDialogs();
    $('#tree').hide();
    $('#left-action-list').removeClass('enabled');
    $('#left-favorites').show();
    $('#left-action-favorites').addClass('enabled');
  },
  showList: function () {
    if ($('#left-action-list').hasClass('enabled'))
      return;

    uiManager.closeAllDialogs();
    $('#left-favorites').hide();
    $('#left-action-list').addClass('enabled');
    $('#tree').show();
    $('#left-action-favorites').removeClass('enabled');
  },
  selectFavorite: function (number) {
    this.removeListSelection();

    $(".left-favorite").each(function (i) {
      if ($(this).attr("favoriteNumber") == number) {
        $(this).addClass("enabled");
      } else {
        $(this).removeClass("enabled");
      }
    });
    this.showFavorites();
  },
  removeFavoriteSelection: function () {
    $(".left-favorite").each(function (i) {
      $(this).removeClass("enabled");
    });
  },
  removeListSelection: function () {
    $("#tree .active").removeClass("active");
  },
}

var favoriteManager = {
  // Private attributes
  _favorites: new Array(),

  // Private methods
  _createFavorite: function (title, description, reportName, reportPath, reportParameters) {
    var reportParametersWithoutInfoMessage = reportParameters;
    // Remove PulseInfoMessage
    if (reportParameters != undefined && reportParameters != null && reportParameters != '') {
      var sURLVariables = reportParameters.split('&');
      reportParametersWithoutInfoMessage = '';

      for (var iVar = 0; iVar < sURLVariables.length; iVar++) {
        if (sURLVariables[iVar] == '')
          continue;
        var sParameterName = sURLVariables[iVar].split('=');
        if (sParameterName[0] == 'PulseInfoMessage')
          continue;
        reportParametersWithoutInfoMessage += '&' + sParameterName[0] + '=' + sParameterName[1];
      }
    }
    return {
      title: title,
      description: description,
      reportName: reportName,
      reportPath: reportPath,
      reportParameters: reportParametersWithoutInfoMessage
    };
  },
  _loadFavorites: function () {
    var tmp = storageManager.read("favoriteReports");
    if (tmp != null || tmp != "")
      this._favorites = tmp;
  },
  _saveFavorites: function () { storageManager.write("favoriteReports", this._favorites); },
  _getValueOfParameter: function (parameters, key) {
    var re = new RegExp(".*[?&]" + key + "=([^&]+)(&|$)");
    var match = parameters.match(re);
    return match == null ? null : match[1];
  },
  _getPeriodDescription: function (tmp) {
    var description = "";

    var dateToString = function (date) {
      var tmp = moment(date, ["MM/DD/YYYY HH:mm:ss", "MM/DD/YYYY HH:mm", "MM/DD/YYYY",
        "YYYY-MM-DD HH:mm:ss", "YYYY-MM-DD HH:mm", "YYYY-MM-DD"]);

      if (tmp.hours() == 0 && tmp.minutes() == 0 && tmp.seconds() == 0)
        return tmp.format("MM/DD/YYYY");
      else if (tmp.seconds() == 0)
        return tmp.format("MM/DD/YYYY HH:mm");
      else
        return tmp.format("MM/DD/YYYY HH:mm:ss");
    };
    var periodParam = tmp.split('_');
    if (periodParam.length == 3) {
      switch (periodParam[0]) {
        case "explicit":
          if (periodParam[2] == "")
            description = "since " + dateToString(periodParam[1]);
          else
            description = "from " + dateToString(periodParam[1]) + " to " + dateToString(periodParam[2]);
          break;
        case "past":
          if (periodParam[1] == "1") {
            if (periodParam[2] == "day")
              description = "yesterday";
            else
              description = "last " + periodParam[2];
          } else
            description = "last " + periodParam[1] + " " + periodParam[2] + "s";
          break;
        case "current":
          if (periodParam[1] == "1") {
            if (periodParam[2] == "day")
              description = "today";
            else
              description = "current " + periodParam[2];
          } else
            description = "current " + periodParam[1] + " " + periodParam[2] + "s";
          break;
      }
    }

    return description;
  },
  _getDescription: function (parameters) {
    let description = "";

    // Get a description of the period
    let tmp = this._getValueOfParameter(parameters, "WebAppParamsDate");
    if (tmp == null)
      tmp = this._getValueOfParameter(parameters, "WebAppParamsDateTime");
    if (tmp != null)
      description = this._getPeriodDescription(tmp);

    // Get a description of the other periods
    let ok = true;
    let index = 1;
    do {
      tmp = this._getValueOfParameter(parameters, "WebAppParamsDate" + index);
      if (tmp == null)
        tmp = this._getValueOfParameter(parameters, "WebAppParamsDateTime" + index);
      if (tmp != null)
        description += " VS " + this._getPeriodDescription(tmp);
      else
        ok = false;
      index++;
    } while (ok);

    return description;
  },

  // Public methods
  updateInterface: function () {
    // Remove selection in the list
    uiManager.showHome();
    menuManager.removeListSelection();

    // Clear the favorites and show the section
    $('#favorite-list').empty();
    $('#left-favorites').empty();
    $('#reportfavorites').show();

    // Load favorite reports
    this._loadFavorites();

    // Display them
    if (this._favorites == null || this._favorites.length == 0) {
      // Display "no favorite yet"
      $('#favorite-list').append("No favorites yet");
    } else {
      // Fill with all favorites
      for (var i = 0; i < this._favorites.length; i++) {
        // Append a favorite
        if (this._favorites[i].title != null && this._favorites[i].title != "") {
          $('#favorite-list').append("<div class='favorite'>" +
            "<div class='favorite-description'>" +
            "<div class='favorite-title'>" + this._favorites[i].title + "</div>" +
            "<div class='favorite-reportname'>" + this._favorites[i].reportName +
            (this._favorites[i].description != "" ? ", " : "") + this._favorites[i].description + "</div>" +
            "</div>" +
            "<div class='favorite-actions'>" +
            "<button title='Edit' class='editfavorite ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only' value='" + i + "'></button>" +
            "<button title='View' class='viewfavorite ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only' value='" + i +
            "'" + ((this._favorites[i].reportParameters == "" || this._favorites[i].reportParameters == null) ? " disabled" : "") + "></button>" +
            "<button title='Delete' class='deletefavorite ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only' value='" + i + "'></button>" +
            "</div>" +
            "</div>");

          $('#left-favorites').append("<div class='left-favorite' favoriteNumber='" + i + "'>" +
            this._favorites[i].title + "</div>");
        }
      }
    }
  },
  addFavorite: function (title, reportName, reportPath, reportParameters) {
    // Add a favorite in the list
    if (this._favorites == null)
      this._favorites = new Array();
    this._favorites.push(this._createFavorite(
      title, this._getDescription(reportParameters),
      reportName, reportPath, reportParameters
    ));
    this._saveFavorites();

    // Update the interface
    this.updateInterface();
  },
  deleteFavorite: function (index) {
    this._favorites.splice(index, 1);
    this._saveFavorites();

    // Update the interface
    this.updateInterface();
  },
  openFavorite: function (index) {
    // Enable the favorite to the left
    menuManager.selectFavorite(index);

    // Get the path and parameters
    var favorite = this._favorites[index];
    if (favorite.reportPath == null || favorite.reportPath == "")
      return;

    // Close the left panel on small screens
    if ($(window).width() <= 685) {
      $("#inner").addClass('leftpanel-collapsed');
      $("#menubtn").addClass('disabled');
    }

    // Specify the index of the current favorite in the update button
    $('#updatefavoritebtn').attr("favoriteNumber", index);

    // Open the report
    if (xhr != null) {
      xhr.abort();
      xhr = null;
    }
    xhr = $.ajax({
      type: "GET",
      url: "reportdescription",
      data: 'reportid=' + favorite.reportPath,
      dataType: 'json',
      success: function (report) {
        loadDescription(report);

        // Load parameters
        $('#reportparameters .tile-content')[0].innerHTML = '<img src="images/loader.gif" alt="loading" style="display:block; margin: auto;" />';
        uiManager.showFavorite(true);

        var reportparameterquerystring = '__report=' + report.id + favorite.reportParameters;
        xhr = $.ajax({
          type: "GET",
          url: "getreporttemplate",
          data: utils.insertLoginInQueryString(reportparameterquerystring),
          dataType: 'text',
          success: prepareParameters,
          error: function (jqXHR, textStatus, errorThrown) {
            if (textStatus != "abort") {
              console.error('ajax error : status = ' + textStatus + '  -  error = ' + errorThrown);
              customDialog.openError(textStatus + " - " + errorThrown + " - " + reportparameterquerystring);
            }
          },
          complete: function () {
            uiManager.showReport(false);
          }
        });
      },
      error: function (jqXHR, textStatus, errorThrown) {
        customDialog.openError("Error when trying to open the description of '" + reportPath +
          "'. Error: " + textStatus + ", " + errorThrown);
      }
    });
  },
  viewFavorite: function (index) {
    // Get the path and parameters
    var favorite = this._favorites[index];
    if (favorite.reportPath == null || favorite.reportPath == "")
      return;

    // Open the report in a new window
    launchViewer(favorite.reportPath, favorite.reportParameters);
  },
  updateFavorite: function (index) {
    var aReportTemplate = new ReportTemplate();
    if (aReportTemplate.isValid) {
      var parameters = aReportTemplate.getQueryParametersString();
      var description = this._getDescription(parameters);

      if (index == null || parseInt(index) >= this._favorites.length) {
        // Cannot update favorite
        customDialog.openWarning("Cannot update the favorite: please check the parameters.");
        return;
      }

      // Remove PulseInfoMessage
      var reportParametersWithoutInfoMessage = parameters;
      if (parameters != undefined && parameters != null && parameters != '') {
        var sURLVariables = parameters.split('&');
        reportParametersWithoutInfoMessage = '';

        for (var iVar = 0; iVar < sURLVariables.length; iVar++) {
          if (sURLVariables[iVar] == '')
            continue;
          var sParameterName = sURLVariables[iVar].split('=');
          if (sParameterName[0] == 'PulseInfoMessage')
            continue;
          reportParametersWithoutInfoMessage += '&' + sParameterName[0] + '=' + sParameterName[1];
        }
      }
      // Update favorite
      this._favorites[index].reportParameters = reportParametersWithoutInfoMessage;
      this._favorites[index].description = description;
      this._saveFavorites();

      // Confirmation popup
      customDialog.openInfo("The favorite has been updated successfully!");
    } else {
      // Cannot update favorite
      customDialog.openWarning("Cannot update the favorite: please check the parameters.");
    }

    this.updateInterface();
  }
}

function launchViewer(reportPath, parameters) {
  var path = window.location.pathname.substring(0, window.location.pathname.lastIndexOf("/")) +
    "/viewer?__report=" + reportPath + parameters;

  // Maybe the login is forced in the URL
  let getParam = function (paramName) {
    return (location.search.split(paramName + '=')[1] || '').split('&')[0];
  };
  let urlLogin = getParam('PulseLogin');
  if (urlLogin != '')
    path += "&PulseLogin=" + urlLogin;

  if (storageManager.read("new_tab") == "false") {
    customDialog.openLoader();
    window.location.href = path;
  } else
    window.open(path);
}

function loadDescription(report) {
  $('#report-title').text(report.title);

  // Markdown formatted text - See online doc. No manual change
  var textDescription = report.description;

  if (textDescription == "null")
    textDescription = "No description";

  //$('x-markdowntext')[0].setText(textDescription); // Mardown formatted !
  $('.report-description-details')[0].setText(textDescription); // Mardown formatted !

  $('#report-comment').html((report.comment + '').replace(/\n/g, "<br />")); // Where is it ? 
  $('#thumbnail').attr('src', 'downloadimage?source=' + report.thumbnailPath);
  $('#path').val(report.path);
}

function prepareParameters(result) {
  uiManager.closeAllDialogs();

  // Fill the parameter div with new content
  $('#reportparameters .tile-content')[0].innerHTML = '';
  $('#reportparameters .tile-content').append(result);

  // Transform select with the jquery library "chosen"
  $(".chosen_select").chosen({ disable_search_threshold: 5 });
}

function setDialogBoxes() {
  // Dialog "Add a favorite"
  customDialog.initialize("#addfavoritedialogbox", {
    title: "Add a favorite",
    autoClose: true,
    onOpen: function () {
      // Prefill the name
      $('#favoritename').val($('#report-title').text());

      // Possibility to keep the parameters?
      var aReportTemplate = new ReportTemplate();
      if (aReportTemplate.isValid) {
        $('#rememberparameters').attr("disabled", false);
        $('#rememberparameters').prop('checked', true);
      } else {
        $('#rememberparameters').attr("disabled", true);
        $('#rememberparameters').prop('checked', false);
      }
    },
    onOk: function () {
      // Information about the favorite: title, report path & name, parameters
      var title = $('#favoritename').val();
      var reportName = $('#report-title').text();
      var reportPath = $('#reporttemplateid').attr('value');
      var reportParameters = "";
      if ($('#rememberparameters').is(':checked')) {
        var aReportTemplate = new ReportTemplate();
        if (aReportTemplate.isValid)
          reportParameters = aReportTemplate.getQueryParametersString();
      }
      favoriteManager.addFavorite(title, reportName, reportPath, reportParameters);
      menuManager.showFavorites();
      uiManager.showHome();

      // Close the dialog and display a confirmation
      uiManager.closeAllDialogs();
      customDialog.openInfo("The favorite has been added successfully!");
    }
  });
}

function setFavoriteSystem() {
  // Show / hide favorites
  $('#left-action-list').click(function () { menuManager.showList(); });
  $('#left-action-favorites').click(function () { menuManager.showFavorites(); });

  // Add favorite button
  $('#addfavoritebtn').click(function () { customDialog.open("#addfavoritedialogbox"); });

  // Update favorite button
  $('#updatefavoritebtn').click(function () {
    var favoriteNumber = $(this).attr("favoriteNumber");
    favoriteManager.updateFavorite(favoriteNumber);
    customDialog.openInfo("The favorite has been updated successfully!");
  });

  // Delete favorite button (elements are generated dynamically)
  $('#favorite-list').on('click', '.deletefavorite', function (event) {
    customDialog.openConfirm("Are you sure you want to delete this favorite?", "Confirmation",
      function () {
        favoriteManager.deleteFavorite($(event.target).val());
        customDialog.openInfo("The favorite has been deleted successfully!");
      });
  });

  // View favorite button (elements are generated dynamically)
  $('#favorite-list').on('click', '.viewfavorite', function (event) {
    var favoriteNumber = $(event.target).val();
    favoriteManager.viewFavorite(favoriteNumber);
  });

  // Edit favorite button (elements are generated dynamically)
  $('#favorite-list').on('click', '.editfavorite', function (event) {
    var favoriteNumber = $(event.target).val();
    favoriteManager.openFavorite(favoriteNumber);
  });
  $('#left-favorites').on('click', '.left-favorite', function (event) {
    var favoriteNumber = $(event.target).attr("favoriteNumber");
    favoriteManager.openFavorite(favoriteNumber);
  });

  // Initialize favorites
  favoriteManager.updateInterface();
}

var getUrlParameter = function getUrlParameter(sParam) {
  var tmpParams = window.location.search.substring(1);
  var sPageURL = tmpParams,
    sURLVariables = sPageURL.split('&'),
    sParameterName,
    i;

  for (i = 0; i < sURLVariables.length; i++) {
    sParameterName = sURLVariables[i].split('=');

    if (sParameterName[0] === sParam) {
      return sParameterName[1] === undefined ? true : utils.decodeSingleValue(sParameterName[1]);
    }
  }
};

$(document).ready(function () {
  // Autocollapse the report tree
  $("#tree .folder > span").click(function () {
    var previousState = $(this).parent().find("ul").is(":visible");
    $("#tree > ul > li > ul").hide();
    if (!previousState)
      $(this).parent().find("ul").show();
  });
  $("#tree > ul > li > ul").hide();

  // Click on a report in the report tree
  $("#tree li[reportid]").click(function () {
    var reportid = $(this).attr("reportid");
    if (reportid == null || reportid == -1)
      return;
    uiManager.closeAllDialogs();
    menuManager.removeFavoriteSelection();
    menuManager.removeListSelection();
    $(this).addClass('active');

    // Close the left panel on small screens
    if ($(window).width() <= 685) {
      $("#inner").addClass('leftpanel-collapsed');
      $("#menubtn").addClass('disabled');
    }

    if (xhr != null) {
      xhr.abort();
      xhr = null;
    }

    // Current parameters in the url (everything is possibly specified in it)
    var reportparameterquerystring = '';
    if (window.location.search)
      reportparameterquerystring = window.location.search.substr(1);

    xhr = $.ajax({
      type: "GET",
      url: "reportdescription",
      data: 'reportid=' + reportid,
      dataType: 'json',
      success: function (report) {
        // Initialize the main area
        loadDescription(report);
        $('#reportparameters .tile-content')[0].innerHTML = '<img src="images/loader.gif" alt="loading" style="display:block; margin: auto;" />';
        uiManager.showReport(true);

        // Load parameters
        if (!reportparameterquerystring.includes('__report='))
          reportparameterquerystring += '&__report=' + report.id;
        if (xhr != null) {
          xhr.abort();
          xhr = null;
        }

        xhr = $.ajax({
          type: "GET",
          url: "getreporttemplate",
          data: utils.insertLoginInQueryString(reportparameterquerystring),
          dataType: 'text',
          success: prepareParameters,
          error: function (jqXHR, textStatus, errorThrown) {
            if (textStatus != "abort") {
              console.error('ajax error : status = ' + textStatus + '  -  error = ' + errorThrown);
              customDialog.openError(textStatus + " - " + errorThrown + " - " + reportparameterquerystring);
            }
          },
          complete: function () {
            uiManager.showReport(false);
          }
        });
      }
    });
  });

  // Set all dialog boxes, favorites
  setDialogBoxes();
  setFavoriteSystem();

  // Launch viewer
  $("#viewbtn").click(function () {
    var aReportTemplate = new ReportTemplate();
    console.log(aReportTemplate.toString());
    if (aReportTemplate.isValid) {
      launchViewer(aReportTemplate.name, aReportTemplate.getQueryParametersString());
    } else {
      customDialog.openWarning("Cannot open the view: please check the parameters.");
    }
  });

  // Initialize the interface
  uiManager.showHome();
  menuManager.showList();
  customImagePopup.initialize('#thumbnail');

  // Select a report if specified in the url
  var selectedReport = getUrlParameter("__report");
  if (selectedReport != null) {
    var li = $("#tree li[report='" + selectedReport.replace(/\\/g, "\\\\") + "']");
    li.parent().css("display", "block");
    li.click();

    // Clear the parameters
    window.history.replaceState({}, document.title, window.location.href.split("?")[0]);
  }

  // Check the webservice path
  webServiceManager.initializePath(
    //$('#webservicepath').val(),
    $('#webservicetimeout').val()
  );
});

// Hack to reload the page: loader that would be open are now closed (after a click on 'back')
$(window).bind('unload', function () { });