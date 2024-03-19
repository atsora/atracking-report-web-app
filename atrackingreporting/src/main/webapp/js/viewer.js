// Copyright (C) 2009-2023 Lemoine Automation Technologies
// Copyright (C) 2023 Atsora Solutions
//
// SPDX-License-Identifier: EPL-2.0

var xhr = null;
var computedParameterQueryString = '';
var slideshowtimer = null;

/**
 * return true if first parameter represents a valid 
 * page range such as second parameters is max page number
 * @param range a string representing a page range
 * @param total max page number
 * @returns {Boolean}
 */
function isvalidpagerange(range, total) {
  var p = new RegExp("^((\\d+(\\-\\d+)?, ?)*(\\d+(\\-\\d+)?))+$", "g");
  if (!p.test(range)) {
    return false;
  }
  var array = range.split(",");
  for (var i = 0; i < array.length; i++) {
    var token = array[i].replace(/^\s+/g, '').replace(/\s+$/g, ''); // Trim
    var j = token.indexOf("-");
    if (j != -1) {
      var min = parseInt(token.substring(0, j));
      var max = parseInt(token.substring(j + 1));
      if (min == 0 || min > max || max > total) {
        return false;
      }
    } else {
      var page = parseInt(token);
      if (page == 0 || page > total) {
        return false;
      }
    }
  }
  return true;
}

// Add default parameters
function getReportParameterEncodedQueryString() {
  if (computedParameterQueryString == '') {
    // Load the query and adapt the report path
    let query = '__report=' + $('#reportid').val() + '&'
      + utils.encodeFullQueryString($('#querystring').val());

    // Split the arguments
    let split = query.split('&');

    // Analyze all elements and build a new query
    /* compatibility is broken - remove
    let processOldDateParam = function (key, value) {
      let newParamValue = '';
      switch (value) {
        case "1": // Explicit
          newParamValue = 'explicit';
          break;
        case "2": // Since
          newParamValue = 'since';
          break;
        case "3": // Today
          newParamValue = "current_1_day";
          break;
        case "4": // Yesterday
          newParamValue = "past_1_day";
          break;
        case "5": // Week to date
          newParamValue = "current_1_week";
          break;
        case "6": // Last 7 days
          newParamValue = "past_7_day";
          break;
        case "7": // Last completed week
          newParamValue = "past_1_week";
          break;
        case "8": // 2 weeks to date
          newParamValue = "current_2_week";
          break;
        case "9": // Last 15 days
          newParamValue = "past_14_day";
          break;
        case "10": // Last completed 2 weeks
          newParamValue = "past_2_week";
          break;
        case "11": // Month to date
          newParamValue = "current_1_month";
          break;
        case "12": // Last 30 days
          newParamValue = "past_30_day";
          break;
        case "13": // Last completed month
          newParamValue = "past_1_month";
          break;
        case "14": // Quarter to date
          newParamValue = "current_1_quarter";
          break;
        case "15": // Last 90 days
          newParamValue = "past_90_day";
          break;
        case "16": // Last completed quarter
          newParamValue = "past_1_quarter";
          break;
        case "17": // Semester to date - removed
          newParamValue = "current_1_semester";
          break;
        case "18": // Last 180 days
          newParamValue = "past_180_day";
          break;
        case "19": // Last completed semester - removed
          newParamValue = "past_1_semester";
          break;
        case "20": // Year to date
          newParamValue = "current_1_year";
          break;
        case "21": // Last 365 days
          newParamValue = "past_365_day";
          break;
        case "22": // Last completed year
          newParamValue = "past_1_year";
          break;
        case "23": // WeekDays - Last started range
          // Not possible anymore
          break;
        case "24": // WeekDays - Last completed range
          // Not possible anymore
          break;
        case "25": // Last completed 4 weeks
          newParamValue = "past_4_week";
          break;
        case "26": // Last completed 8 weeks
          newParamValue = "past_8_week";
          break;
        case "27": // Last completed 12 weeks
          newParamValue = "past_12_week";
          break;
        default:
          // Nothing
          break;
      }
      return newParamValue != '' ? ('&' + key + '=' + newParamValue) : '';
    }; */

    for (let i = 0; i < split.length; i++) {
      let element = split[i];
      if (element == '')
        continue;

      // Element in the form key / value
      let keyVal = element.split('=');
      if (keyVal.length != 2)
        continue;
      let key = keyVal[0];
      let value = keyVal[1];

      // Check old parameters is done earlier

      /* Should never happen again - compatibility is broken
      switch (key) {
        case 'PulseDateTimeRange':
          computedParameterQueryString += processOldDateParam('WebAppParamsDateTime', val);
          break;
        case 'PulseDateTimeRange1':
          computedParameterQueryString += processOldDateParam('WebAppParamsDateTime1', val);
          break;
        case 'PulseDateRange':
          computedParameterQueryString += processOldDateParam('WebAppParamsDate', val);
          break;
        case 'PulseDateRange1':
          computedParameterQueryString += processOldDateParam('WebAppParamsDate1', val);
          break;
        default:*/
      computedParameterQueryString += '&' + key + '=' + value; // Utils.encodeValue IS already done !%20 - DO NOT do it twice !
      /*break;
  }*/
    }
  }

  return computedParameterQueryString.substring(1);
}

function activateSlideShow(refreshtime, delayoftransition) {
  // Save parameters
  storageManager.write('delayoftransition', delayoftransition);
  storageManager.write('refreshtime', refreshtime);
  storageManager.writeLocal('slideshow', true);

  // Add a css for a full screen report
  $('head').append('<link rel="stylesheet" type="text/css" href="css/slideshow.css" id="slideshow_stylesheet">');

  var delaytime = 8;
  switch (delayoftransition) {
    case 'slow': delaytime = 12; break;
    case 'fast': delaytime = 4; break;
  }

  // Timer for changing the page
  var refreshDate = new Date();
  if (slideshowtimer != null)
    slideshowtimer.stop();
  slideshowtimer = $.timer(
    function () {
      var currentPos = $('#mainarea-inner').scrollTop();
      var visibleHeight = $('#mainarea').height();
      var totalHeight = $('#report-container').height();
      if (currentPos + visibleHeight < totalHeight) {
        // Scroll down
        var newPos = 0.85 * visibleHeight + currentPos;
        if (newPos > totalHeight - visibleHeight)
          newPos = totalHeight - visibleHeight;
        $('#mainarea-inner').animate({ scrollTop: newPos });
      } else {
        // New page
        var pagenumber = parseInt($('#currentpage').val());
        var totalpage = parseInt($('#totalpage').html());

        // New page to display
        var newPage = (pagenumber % totalpage) + 1;

        if (newPage == 1) {
          // Refresh the report?
          var minutes = (new Date() - refreshDate) / 60000;
          if (minutes > refreshtime) {
            refreshDate = new Date();
            gotopage(1, null, true);
          } else if (currentPos != 0 || newPage != pagenumber) {
            gotopage(1);
          }
        } else {
          gotopage(newPage);
        }
      }
    },
    delaytime * 1000, // In ms
    false
  );

  slideshowtimer.play(true);
}

function deactivateSlideshow() {
  slideshowtimer.stop();
  slideshowtimer = null;
  storageManager.writeLocal('slideshow', false);
  $('#slideshow_stylesheet').remove();
}

function saveLeftPanelState() {
  if ($("#inner").hasClass('leftpanel-collapsed'))
    storageManager.writeLocal("left_panel_state", "collapsed");
  else
    storageManager.writeLocal("left_panel_state", "expanded");
}

function settingDialogbox() {
  /* PRINT DIALOG BOX */
  $("input:radio[name='printpage']").change(function () {
    var option = $("input:radio[name='printpage']:checked").val();
    if (option == 'ALL') {
      $("#printpagerange").prop("disabled", true);
    } else if (option == 'CURRENT') {
      $("#printpagerange").prop("disabled", true);
    } else if (option == 'RANGE') {
      $("#printpagerange").prop("disabled", false);
      $("#printpagerange").removeAttr("disabled");
    }
  });
  $("input:radio[name='printpage']")[0].checked = true;
  $("#printpagerange").prop("disabled", true);

  customDialog.initialize("#printdialogbox", {
    title: "Print report",
    autoClose: false,
    onOk: function () {
      var format = $("input[name='printformat']:radio:checked").val();
      var url = window.location.pathname.substring(0, window.location.pathname.lastIndexOf('/') + 1) + 'print';
      url = url + "?" + getReportParameterEncodedQueryString() + '&__format=' + format;
      var page = $("input[name='printpage']:radio:checked").val();
      var pagerange;
      var totalpage = $('#totalpage').html();
      if (page == 'RANGE') {
        pagerange = $("#printpagerange").val();
        if (!isvalidpagerange(pagerange, totalpage)) {
          customDialog.openWarning('Page range is not valid!');
          return;
        }
        url = url + '&__pagerange=' + pagerange;
      } else if (page == 'CURRENT') {
        pagerange = $("#pagenumber").val();
        url = url + '&__pagerange=' + pagerange;
      }
      url = url + '&__session=' + $('#viewingsession').val();
      customDialog.close("#printdialogbox");
      var printwnd = window.open(url, '_blank', '');
      printwnd.onload = function () { printwnd.print(); };
    },
    onCancel: function () { customDialog.close("#printdialogbox"); }
  });

  /* EXPORT DIALOG BOX */
  $("input:radio[name='exportpage']").change(function () {
    var option = $("input:radio[name='exportpage']:checked").val();
    if (option == 'ALL') {
      $("#exportpagerange").prop("disabled", true);
    } else if (option == 'CURRENT') {
      $("#exportpagerange").prop("disabled", true);
    } else if (option == 'RANGE') {
      $("#exportpagerange").prop("disabled", false);
      $("#exportpagerange").removeAttr("disabled");
    }
  });
  $("input:radio[name='exportpage']")[0].checked = true;
  $("#exportpagerange").prop("disabled", true);
  $("#excelexportimagewarning").css('display', 'none');
  $("#exportformat").change(function () {
    var value = $("#exportformat").val().toLowerCase();
    if (value.substr(0, 3) == 'xls') {
      $("#excelexportimagewarning").css('display', 'inline');
    } else {
      $("#excelexportimagewarning").css('display', 'none');
    }
  });
  $("#exportselect").change();

  customDialog.initialize("#exportdialogbox", {
    title: "Export report",
    autoClose: false,
    onOpen: function () { $("#exportformat").val('pdf'); },
    onOk: function () {
      var format = $('select#exportformat').val();
      var url = window.location.pathname.substring(0, window.location.pathname.lastIndexOf('/') + 1) + 'export';
      url = url + "?" + getReportParameterEncodedQueryString() + '&__format=' + format;
      var page = $("input[name='exportpage']:radio:checked").val();
      var pagerange;
      var totalpage = $('#totalpage').html();
      if (page == 'RANGE') {
        pagerange = $("#exportpagerange").val();
        if (!isvalidpagerange(pagerange, totalpage)) {
          customDialog.openWarning('Page range is not valid!');
          return;
        }
        url = url + '&__pagerange=' + pagerange;
      } else if (page == 'CURRENT') {
        pagerange = $("#pagenumber").val();
        url = url + '&__pagerange=' + pagerange;
      }
      url = url + '&__session=' + $('#viewingsession').val();
      window.open(url, '_blank');
      customDialog.close("#exportdialogbox");
    },
    onCancel: function () { customDialog.close("#exportdialogbox"); }
  });

  /* SLIDESHOW DIALOG BOX */
  customDialog.initialize("#slideshowdialogbox", {
    title: "Start slideshow",
    autoClose: false,
    onOk: function () {
      var refreshtime = parseInt($('#delayofrefresh').val());
      var delayoftransition = $('input[name=delayoftransition]:checked').val();
      if (refreshtime == null || refreshtime < 1 || refreshtime > 1000 || isNaN(refreshtime) ||
        (delayoftransition != 'slow' && delayoftransition != 'normal' && delayoftransition != 'fast')) {
        customDialog.openWarning("Cannot start the slideshow: please check the parameters.");
      } else {
        // Close the dialog and activate the slideshow 
        customDialog.close("#slideshowdialogbox")
        activateSlideShow(refreshtime, delayoftransition);
      }
    },
    onCancel: function () { customDialog.close("#slideshowdialogbox"); }
  });

  /* PARAMETER DIALOG BOX */
  customDialog.initialize("#parameterdialogboxcontainer", {
    title: "Update parameters",
    autoClose: false,
    onOk: function () {
      var aReportTemplate = new ReportTemplate();
      if (aReportTemplate.isValid) {
        customDialog.openLoader();
        var queryreportparameter = aReportTemplate.getQueryParametersString();
        var pathname = window.location.pathname.substring(0, window.location.pathname.lastIndexOf("/")) +
          "/viewer?__report=" + $('#reporttemplateid').val() + queryreportparameter;
        saveLeftPanelState();
        window.document.location = pathname;
        customDialog.close('#parameterdialogboxcontainer');
        customDialog.closeLoader();
      } else {
        console.error('Some parameters are not correct');
      }
    },
    onCancel: function () { customDialog.close("#parameterdialogboxcontainer"); }
  });
}

function settingPageNavigationBtn() {
  // Go to first page and go to previous page buttons
  var pagenumber = parseInt($('#currentpage').val());
  if (pagenumber <= 1) {
    $('#firstpagebtn').addClass("button_disabled");
    $('#previouspagebtn').addClass("button_disabled");
  } else {
    $('#firstpagebtn').removeClass("button_disabled");
    $('#previouspagebtn').removeClass("button_disabled");
  }
  $('#firstpagebtn').click(function () {
    gotopage(1);
  });
  $('#previouspagebtn').click(function () {
    var pagenumber = parseInt($('#currentpage').val());
    gotopage(pagenumber - 1);
  });

  // Go to next page and go to last page buttons
  var totalpage = parseInt($('#totalpage').html());
  if (totalpage <= 1 || pagenumber == totalpage) {
    $('#nextpagebtn').addClass("button_disabled");
    $('#lastpagebtn').addClass("button_disabled");
  } else {
    $('#nextpagebtn').removeClass("button_disabled");
    $('#lastpagebtn').removeClass("button_disabled");
  }
  $('#nextpagebtn').click(function () {
    var pagenumber = parseInt($('#currentpage').val());
    gotopage(pagenumber + 1);
  });
  $('#lastpagebtn').click(function () {
    var totalpage = parseInt($('#totalpage').html());
    gotopage(totalpage);
  });

  // Page number input
  $('#pagenumber').prop("disabled", totalpage <= 1);
  $("#pagenumber").keyup(function (event) {
    if (event.keyCode == 13) { // If "enter" key is pressed
      var pagenumber = parseInt($('#pagenumber').val());
      gotopage(pagenumber);
    }
  });
}

function actionBtn() {
  // Parameters buttons
  $('#parameterbtn, #parameterbtn2').click(function () {
    var reportparameterquerystring = getReportParameterEncodedQueryString() + '&__session=' + $('#viewingsession').val();
    customDialog.openLoader(function () {
      if (xhr != null) {
        xhr.abort();
        xhr = null;
      }
    });
    xhr = $.ajax({
      type: "GET",
      url: "getreporttemplate",
      data: utils.insertLoginInQueryString(reportparameterquerystring),
      dataType: 'text',
      success: function (result) {
        // Button for validation?
        $('#parameterdialogboxcontainer').html(result);
        if (result.indexOf('This report has no configuration') >= 0)
          customDialog.setAttribute("#parameterdialogboxcontainer", "okButton", "hidden");
        else
          customDialog.setAttribute("#parameterdialogboxcontainer", "okButton", null);

        // Transform select with the jquery library "chosen"
        $(".chosen_select").chosen({ disable_search_threshold: 5 });

        customDialog.closeLoader();
        customDialog.open("#parameterdialogboxcontainer");
        var maxheight = $(window).height() - 60;
        var dialogheight = $('#parametersdialogbox').closest('.ui-dialog').height();
        var titlebarHeight = $('#parametersdialogbox').closest('.ui-dialog').find('.ui-dialog-titlebar').outerHeight();
        var buttonPaneHeight = $('#parametersdialogbox').closest('.ui-dialog').find('.ui-dialog-buttonpane').outerHeight();
        $('#parametersdialogbox').css('max-height', (maxheight - titlebarHeight - buttonPaneHeight) + 'px');
      },
      error: function (jqXHR, textStatus, errorThrown) {
        console.error('ajax error : status = ' + textStatus + '  -  error = ' + errorThrown);
      },
      complete: function () {
        customDialog.closeLoader();
      }
    });
  });

  // Export buttons
  $('#exportbtn, #exportbtn2').click(function () {
    customDialog.open('#exportdialogbox');
  });

  // Print buttons
  $('#printbtn, #printbtn2').click(function () {
    customDialog.open('#printdialogbox');
  });

  // Slideshow buttons
  $('#slideshowbtn, #slideshowbtn2').click(function () {
    $('.dropdown-content-show').removeClass('dropdown-content-show');
    customDialog.open('#slideshowdialogbox');
  });
  $('#stop-slideshowbtn').click(function () {
    deactivateSlideshow();
  });

  // Initialize the slideshow parameters
  $("input[name=delayoftransition][value='" + storageManager.read('delayoftransition') + "']").prop("checked", true);
  if (storageManager.read('refreshtime') != null && !isNaN(storageManager.read('refreshtime')))
    $("#delayofrefresh").val(storageManager.read('refreshtime'));

  // Home button
  if (storageManager.read("new_tab") == "false" || true) { // Always shown
    $('#homebtn').click(function () {
      window.location.href = window.location.pathname.substring(0, window.location.pathname.lastIndexOf("/"))
        + "/";
    });
  } else {
    $('#homebtn').hide();
  }
}

// Function called after a page is selected or after a scroll
function updateTocSelection() {
  // Do nothing if it's been forbidden
  if ($('#tableofcontenttree').hasClass('noscrollselection'))
    return;

  // Current page
  let currentPage = parseInt($('#currentpage').val(), 10);

  // Browse the bookmarks
  let lastHidden = '';
  let lastHiddenDistance = -1;
  let firstVisible = '';
  let firstVisibleDistance = -1;
  let lastPage = '';
  let lastPageDistance = -1;

  $('li[data^="bookmark:"]').each(function () {
    // Only process visible bookmarks (the toc tree may be partially or totally collapsed)
    if (!$(this).is(":visible"))
      return;

    // Extract the bookmark and check it is valid
    let bookmarkId = $(this).attr('data');
    bookmarkId = bookmarkId.substr(bookmarkId.indexOf(':') + 2, bookmarkId.length - bookmarkId.indexOf(':') - 3);
    if (bookmarkId === "")
      return;

    // Bookmark page
    let bookmarkPage = parseInt($(this).attr('id'), 10);

    if (bookmarkPage == currentPage) {
      // The bookmark is supposed to be found on the displayed page
      let bookmark = $("[id='" + bookmarkId + "']");
      if (bookmark.length == 0)
        return;

      // Keep the closest bookmark on the top
      let distance = bookmark.position().top - 50 - $('#mainarea-inner').scrollTop();
      if (distance < 0) {
        // The id is hidden above or is on the top of the page, suitable to be selected
        if (lastHiddenDistance == -1 || -distance < lastHiddenDistance) {
          lastHiddenDistance = -distance;
          lastHidden = bookmarkId;
        }
      } else {
        // The id is after, might be suitable in case there is no lastHidden
        if (firstVisibleDistance == -1 || distance < firstVisibleDistance) {
          firstVisibleDistance = distance;
          firstVisible = bookmarkId;
        }
      }
    } else if (bookmarkPage < currentPage) {
      // The bookmark is on another page. Bookmarks in the previous pages are interesting
      if (lastPageDistance == -1 || lastPageDistance >= currentPage - bookmarkPage) {
        console.log("lastPageDistance is '" + lastPageDistance + "', currentPage is '" + currentPage + "', bookmarkPage is '" + bookmarkPage + "'");
        lastPageDistance = currentPage - bookmarkPage;
        lastPage = bookmarkId;
      }
    }
  });
  console.log("last hidden is '" + lastHidden + "', last page is '" + lastPage + "'");

  // Remove previous selection in the toc
  $('#tableofcontenttree .active').removeClass('active');

  // Select the new one
  let bookmarkToSelect = (lastHidden != '') ? lastHidden : ((firstVisible != '') ? firstVisible : lastPage);
  if (bookmarkToSelect != '') {
    bookmarkToSelect = "'" + bookmarkToSelect + "'";
    $('li[data="bookmark:' + bookmarkToSelect + '"]').addClass('active');
  } else {
    // By default, select the first bookmark
    let bookmarks = $('#tableofcontenttree > ul > li[data^="bookmark:"]:first-child');
    if (bookmarks.length)
      bookmarks.addClass('active');
  }
}

function gotopage(targetedpage, bookmark, refresh) {
  // Just a scroll?
  if (bookmark != null && bookmark != "" && parseInt($('#currentpage').val()) == targetedpage) {
    // Select the bookmark
    $('#tableofcontenttree .active').removeClass('active');
    $('li[data="bookmark:\'' + bookmark + '\'"]').addClass('active');

    // Prevent the automatic bookmark selection
    $('#tableofcontenttree').addClass('noscrollselection');
    setTimeout(function () { $('#tableofcontenttree').removeClass('noscrollselection'); }, 700);

    // Scroll to the bookmark
    let scrollTo = $("[id='" + bookmark + "']");
    if (scrollTo != null) {
      $('#mainarea-inner').animate({ scrollTop: scrollTo.position().top + 15 }, 600);
    }

    return;
  }

  if (refresh == null)
    refresh = false;

  var totalpage = parseInt($('#totalpage').html());

  if (!utils.isValidValue(targetedpage, 'INTEGER') || targetedpage < 1 || targetedpage > totalpage) {
    customDialog.openWarning('Requested page number ' + targetedpage + ' is not correct.\nPage number must be between 1 and ' + totalpage);
    $('#pagenumber').val($('#currentpage').val());
    return;
  }

  if (targetedpage <= 1) {
    $('#firstpagebtn').addClass("button_disabled");
    $('#previouspagebtn').addClass("button_disabled");
  } else {
    $('#firstpagebtn').removeClass("button_disabled");
    $('#previouspagebtn').removeClass("button_disabled");
  }

  if (totalpage <= 1 || targetedpage == totalpage) {
    $('#nextpagebtn').addClass("button_disabled");
    $('#lastpagebtn').addClass("button_disabled");
  } else {
    $('#nextpagebtn').removeClass("button_disabled");
    $('#lastpagebtn').removeClass("button_disabled");
  }

  var querystring = getReportParameterEncodedQueryString() +
    (refresh ? "" : '&__session=' + $('#viewingsession').val()) +
    '&__page=' + targetedpage;

  xhr = $.ajax({
    type: "GET",
    url: "getviewingpage",
    data: utils.insertLoginInQueryString(querystring),
    dataType: 'json',
    success: function (response) {
      if (typeof response.outputfilepath != 'undefined') {
        $('.slideshow-warning').hide();

        // Update page number
        $('#currentpage').val(targetedpage);
        $('#pagenumber').val(targetedpage);

        // Load content
        var subUrl = window.location.pathname.substring(0, window.location.pathname.lastIndexOf('/') + 1) +
          'download?source=' + escape(response.outputfilepath);
        $('#report-content').empty();
        $.get(subUrl, function (pageContent) {
          // Process the content: image links must be relative and not absolute
          pageContent = pageContent.replace(/http[^'"]+\/downloadimage\?/g, 'downloadimage?');

          // Add the content
          $('#report-content').html(pageContent);

          if (bookmark) {
            // Select the bookmark
            $('#tableofcontenttree .active').removeClass('active');
            $('li[data="bookmark:\'' + bookmark + '\'"]').addClass('active');

            // Prevent the automatic bookmark selection
            $('#tableofcontenttree').addClass('noscrollselection');
            setTimeout(function () { $('#tableofcontenttree').removeClass('noscrollselection'); }, 700);

            // Scroll to the bookmark
            let scrollTo = $("[id='" + bookmark + "']");
            if (scrollTo != null) {
              $('#mainarea-inner').animate({ scrollTop: scrollTo.position().top + 15 }, 600);
            }
          } else {
            // Select the bookmark normally
            updateTocSelection();
          }
        });

        // Update the session id and the maximum number of pages
        if (typeof response.sessionid != 'undefined')
          $('#viewingsession').val(response.sessionid);
        if (typeof response.totalpage != 'undefined') {
          $('#totalpage').html(response.totalpage);
        }
      }
      if (typeof response.error != 'undefined') {
        if (storageManager.readLocal('slideshow') == true) {
          // Discrete clickable notification in slideshow mode
          $('.slideshow-warning-view-details').unbind();
          $('.slideshow-warning-view-details').click(function () {
            customDialog.openError(response.error);
          });
          $('.slideshow-warning').show();
        } else {
          // Normal error dialog
          customDialog.openError(response.error);
        }
      }
    },
    error: function (jqXHR, textStatus, errorThrown) {
      var error = 'Error when trying to load a page';
      if (errorThrown != "")
        error += ": " + errorThrown;
      if (textStatus != "")
        error += " (status " + textStatus + ")";
      error += ".";
      console.error(error);
      if (storageManager.readLocal('slideshow') == true) {
        // Discrete clickable notification in slideshow mode
        $('.slideshow-warning-view-details').unbind();
        $('.slideshow-warning-view-details').click(function () {
          customDialog.openError(error);
        });
        $('.slideshow-warning').show();
      } else {
        // Normal error dialog
        customDialog.openError(error);
      }
    }
  });
}

function onReportReady() {
  // Fill the toc and display a page
  xhr = $.ajax({
    type: "GET",
    url: "gettoc",
    data: "__session=" + $('#viewingsession').val(),
    dataType: 'text',
    success: function (result) {
      // Page not loading state anymore (some elements are not hidden anylonger)
      $(document.body).removeClass('loading');

      // Fill the table of content
      $('#tableofcontenttree').html(result);

      // Initialization of the left panel (part 1)
      if ($(window).width() <= 685) {
        $('head').append('<style id="leftpanel-init">#leftpanel {display: none !important;}</style>');
        $("#inner").addClass('leftpanel-collapsed');
        $("#menubtn").addClass('disabled');
      }

      // Initialize the navigation buttons
      settingPageNavigationBtn();

      // Table of content
      $('#tableofcontenttree li > span.toctext').click(function () {
        gotopage(parseInt($(this).parent().attr("id")), $(this).parent().attr("data").split("'")[1]);

        // Close the left panel on small screens
        if ($(window).width() <= 685) {
          $("#inner").addClass('leftpanel-collapsed');
          $("#menubtn").addClass('disabled');
        }
      });
      $('#tableofcontenttree .tocexpandable').click(function () {
        $(this).toggleClass('tocexpanded');
        $(this).parent().children("ul").css('display', $(this).hasClass('tocexpanded') ? 'block' : 'none');
        updateTocSelection();
      });

      // Intercept clicks on a link
      $('#report-content').on('click', 'a', function (e) {
        var bookmark = $(this).attr('href');
        if (bookmark == null || bookmark == "")
          return;

        // Internal move?
        if (bookmark[0] == '#') {
          // String to find
          bookmark = "bookmark:'" + decodeURI(bookmark.substr(1)) + "'";

          // Find the page
          var elt = $("#tableofcontenttree li[data='" + bookmark + "']");
          if (elt != null) {
            gotopage(parseInt(elt.attr('id')), bookmark);
            e.preventDefault();
          }
        }
      });

      // Open a page
      $('.slideshow-warning').hide();
      let currentPage = getParam(location.search, '__page');
      if (currentPage == '')
        currentPage = 1; // Default is first page
      else
        currentPage = parseInt(currentPage);
      gotopage(currentPage);

      // Possibly activate the slideshow
      let txt = location.search;
      if (storageManager.readLocal('slideshow') == true) {
        activateSlideShow(storageManager.read('refreshtime'), storageManager.read('delayoftransition'));
      }
    },
    error: function (jqXHR, textStatus, errorThrown) {
      customDialog.openError('Cannot load the table of content: ' + textStatus + ', ' + errorThrown);
      console.error('ajax error : status = ' + textStatus + '  -  error = ' + errorThrown);
    }
  });
}

$(document).ready(function () {
  // Check old parameters // oldparam // PulseDate  
  let href = window.location.href;
  let idx = href.indexOf('PulseDateRange');
  if (idx != -1) {
    customDialog.openError('Cannot run the report, old parameter is used: PulseDateRange. '
      + 'Please. Change your bookmark');
    console.error('old parameter used: PulseDateRange - in URL = ' + href);

    $('.slideshow-warning').hide();
    return;
  }
  idx = href.indexOf('PulseDateTimeRange');
  if (idx != -1) {
    customDialog.openError('Cannot run the report, old parameter is used: PulseDateTimeRange. '
      + 'Please. Change your bookmark');
    console.error('old parameter used: PulseDateTimeRange - in URL = ' + href);

    $('.slideshow-warning').hide();
    return;
  }

  // Page in loading state (some elements are hidden)
  $(document.body).addClass('loading');
  customDialog.openLoader(null, 'Your report is being generated (the operation can take a few minutes).');

  // Configure header and dialogs
  settingDialogbox();
  actionBtn();

  // Reaction to a scroll
  $('#mainarea-inner').scroll(updateTocSelection);

  let runReportFunction = function () {
    // Run the report asynchroneously with all parameters of the url + login
    xhr = $.ajax({
      type: "GET",
      url: "runreport",
      data: utils.insertLoginInQueryString(window.location.search.substring(1)),
      dataType: 'json',
      success: function (result) {
        if (result.error != "") {
          customDialog.openError('Cannot run the report: ' + result.error);
        } else {
          // Fill different parameters in the page
          $('#viewingsession').val(result.sessionid);
          $('#reportid').val(result.reportid);
          $('#querystring').val(result.querystring);
          $('#totalpage').html(result.totalpage);
          $('#header-title').html(result.reporttitle);
          document.title = result.reporttitle;

          // The report has been generated, fill the page
          onReportReady();
        }
      },
      error: function (jqXHR, textStatus, errorThrown) {
        customDialog.openError('Cannot run the report: ' + textStatus + ', ' + errorThrown);
        console.error('ajax error : status = ' + textStatus + '  -  error = ' + errorThrown);
      },
      complete: function (resultat, statut) {
        customDialog.closeLoader();
      }
    });
  };

  // Check the webservice path
  webServiceManager.initializePath(
    //$('#webservicepath').val(),
    $('#webservicetimeout').val());

  runReportFunction();
});

window.onload = function () {
  // Initialization of the left panel (part 2)
  $('#leftpanel-init').remove();
}

function getParam(txt, paramName) {
  return (txt.split(paramName + '=')[1] || '').split('&')[0];
}
