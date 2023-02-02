// Copyright (C) 2009-2023 Lemoine Automation Technologies
//
// SPDX-License-Identifier: EPL-2.0

///////////
// Utils //
///////////

var customWidgetsUtils = {
  _inputTimeEnabled: null,
  _inputDateEnabled: null,
  _inputDateTimeEnabled: null,
  _inputEnabled2: function (type) {
    // Device detection (http://stackoverflow.com/questions/3514784/what-is-the-best-way-to-detect-a-mobile-device-in-jquery)
    return /(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|ipad|iris|kindle|Android|Silk|lge |maemo|midp|mmp|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino/i.test(navigator.userAgent) || /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(navigator.userAgent.substr(0, 4));

    // http://stackoverflow.com/questions/10193294/how-can-i-tell-if-a-browser-supports-input-type-date
    /*try {
      var input = document.createElement('input');
      input.setAttribute('type', type);
      input.setAttribute('value', 'not-a-good-value');
      return input.value !== 'not-a-good-value';
    } catch(e) {
      return false;
    }*/
  },

  // Return true if a native input is available for the current device
  isInputEnabled: function (type) {
    var result = false;
    switch (type) {
      case 't':
        if (this._inputTimeEnabled == null)
          this._inputTimeEnabled = this._inputEnabled2('time');
        result = this._inputTimeEnabled;
        break;
      case 'd':
        if (this._inputDateEnabled == null)
          this._inputDateEnabled = this._inputEnabled2('date');
        result = this._inputDateEnabled;
        break;
      case 'dt':
        if (this._inputDateTimeEnabled == null)
          this._inputDateTimeEnabled = this._inputEnabled2('datetime-local');
        result = this._inputDateTimeEnabled;
        break;
    }
    return result;
  },

  createDataManager: function (idName) {
    return {
      _idName: idName,
      _id: 0,
      _data: [],
      createNewId: function () {
        this._data[this._id] = {};
        return this._id++;
      },
      initializeIdAttribute: function (selector, id) { $(selector).attr(this._idName, id); },
      getId: function (selector) {
        if ($(selector).length) {
          var attribute = $(selector).attr(this._idName);
          if (typeof attribute === typeof undefined && attribute === false)
            throw "Selector '" + selector + "' has no attribute '" + this._idName + "'";
          var id = parseInt(attribute);
          if (id < 0 || id >= this._id)
            throw "Bad " + this._idName + " '" + id + "' for selector '" + selector + "'";
          return id;
        } else
          throw "Selector '" + selector + "'doesn't exist";
      },
      get: function (id) { return this._data[id]; },
      set: function (id, field, data) { this._data[id][field] = data; }
    };
  }
}


////////////
// Dialog //
////////////

var customDialog = {
  // Private functions and attributes
  _dataManager: customWidgetsUtils.createDataManager('customDialogId'),
  _createDialog: function (attributes) {
    // Create an id
    var id = this._dataManager.createNewId();
    var dialogId = "customDialog" + id;

    // Options
    var closeButton = (attributes["closeButton"] != "hidden");
    var fullScreenOnSmartphone = (attributes["fullScreenOnSmartphone"] == true);

    // Create a dialog
    $("body").append(
      "<div id='" + dialogId + "' class='customDialog'>" +
      "<div class='customDialogShadow'></div>" +
      "<div class='customDialogWindow" + (fullScreenOnSmartphone ? " customDialogWindowFullScreenOnSmartphone" : "") + "'>" +
      "<div class='customDialogHeader'>" +
      (closeButton ? "<div class='customDialogCloseBox'></div>" : "") +
      "<div class='customDialogTitle'>" + attributes['title'] + "</div>" +
      "</div>" +
      "<div class='customDialogContent'></div>" +
      "<div class='customDialogButtons'>" +
      "<button class='customDialogCancel button2' title='Cancel' role='button' aria-disabled='false'><span class='button2-icon'></span></button>" +
      "<button class='customDialogPrevious button2' title='Previous' role='button' aria-disabled='false'><span class='button2-icon'></span></button>" +
      "<button class='customDialogNext button2' title='Next' role='button' aria-disabled='false'><span class='button2-icon'></span></button>" +
      "<button class='customDialogOk button2' title='Ok' role='button' aria-disabled='false'><span class='button2-icon'></span></button>" +
      "</div>" +
      "</div>" +
      "</div>");
    this._dataManager.initializeIdAttribute("#" + dialogId, id);

    // Number of pages
    this._dataManager.set(id, "attributes", attributes);
    this._dataManager.set(id, "currentPage", 0);
    this._dataManager.set(id, "pageCount", 0);

    // Add callbacks
    $("#" + dialogId + " .customDialogCloseBox").click(function () { customDialog.cancel("#" + dialogId); });
    $("#" + dialogId + " .customDialogCancel").click(function () { customDialog.cancel("#" + dialogId); });
    $("#" + dialogId + " .customDialogPrevious").click(function () { customDialog.previous("#" + dialogId); });
    $("#" + dialogId + " .customDialogNext").click(function () { customDialog.next("#" + dialogId); });
    $("#" + dialogId + " .customDialogOk").click(function () { customDialog.ok("#" + dialogId); });

    // Max height or height of the dialog
    if (attributes['fixedHeight'] == true) {
      $(window).on('resize', function () {
        $('#' + dialogId + ' .customDialogContent > div').css('height', (($(this).height() - 70) * 0.6) + 'px');
      });
    } else {
      $(window).on('resize', function () {
        $('#' + dialogId + ' .customDialogContent > div').css('max-height', (($(this).height() - 70) * 0.6) + 'px');
      });
    }

    return id;
  },
  _displayNavigation: function (selector) {
    var id = this._dataManager.getId(selector);
    var dialogId = "customDialog" + id;
    var data = this._dataManager.get(id);

    // Extract parameters defining the element visibility
    var currentPage = data["currentPage"];
    var pageCount = data["pageCount"];
    var cancelButton = data["attributes"]["cancelButton"]; // can be null, "hidden"
    var previousButton = data["attributes"]["previousButton"]; // can be null, "hidden"
    var nextButton = data["attributes"]["nextButton"]; // can be null, "hidden"
    var okButton = data["attributes"]["okButton"]; // can be null, "hidden"
    var multiPage = data["attributes"]["multiPage"]; // can be "auto" / null, "on", "off"

    // First and/or last page?
    $("#" + dialogId).toggleClass("customDialogFirstPage", currentPage == 0);
    $("#" + dialogId).toggleClass("customDialogLastPage", currentPage == pageCount - 1);

    // Hidden buttons?
    $("#" + dialogId).toggleClass("customDialogNoCancel", cancelButton == "hidden");
    $("#" + dialogId).toggleClass("customDialogNoPrevious", previousButton == "hidden");
    $("#" + dialogId).toggleClass("customDialogNoNext", nextButton == "hidden");
    $("#" + dialogId).toggleClass("customDialogNoOk", okButton == "hidden");

    // Multipage state
    $("#" + dialogId).toggleClass("customDialogMultiPageOn", multiPage == "on");
    $("#" + dialogId).toggleClass("customDialogMultiPageOff", multiPage == "off");

    // Set current page
    for (var i = 0; i < pageCount; i++)
      $("#" + dialogId + " .customDialogPage" + i).toggleClass("customDialogCurrentPage", i == currentPage);
  },

  /// Create a dialog with initialize and addpage
  /// Attributes:
  /// - title
  /// - cancelButton / previousButton / nextButton / okButton: can be set to "hidden"
  /// - multipage: can be set to "auto" (default), "on", "off"
  /// - onOpen, onOk, onCancel: functions that can be triggered
  /// - autoClose: true / false (default), close automatically the dialog
  /// - autoDelete: true / false (default), remove automatically the html associated to the dialog
  /// - cancelEnabled: true (default) / false, cancelation of the dialog can be disabled (esc key will not close the dialog)
  initialize: function (selector, attributes) {
    if (attributes == null)
      attributes = {};

    // Create a new dialog
    var id = this._createDialog(attributes);

    // Add a page
    this.addPage("#customDialog" + id, selector);
  },
  addPage: function (selector, pageSelector) {
    // Id of the dialog
    var id = this._dataManager.getId(selector);

    // Number of pages
    var pageCount = this._dataManager.get(id)["pageCount"];
    var blockToInsert = $(pageSelector).detach().addClass('customDialogPage' + pageCount);
    blockToInsert.appendTo("#customDialog" + id + " .customDialogContent");
    this._dataManager.initializeIdAttribute(pageSelector, id);

    // Update data
    this._dataManager.set(id, "pageCount", pageCount + 1);

    // Dimensions of a page
    $("#customDialog" + id + " .customDialogContent > div").css('width', (100 / (pageCount + 1)) + '%');
    var attributes = this._dataManager.get(id)["attributes"];
    if (attributes['fixedHeight'] == true)
      $('#customDialog' + id + ' .customDialogContent > div').css('height', (($(window).height() - 70) * 0.6) + 'px');
    else
      $('#customDialog' + id + ' .customDialogContent > div').css('max-height', (($(window).height() - 70) * 0.6) + 'px');
  },
  setAttribute: function (selector, key, value) {
    // Id of the dialog
    var id = this._dataManager.getId(selector);

    // Update data
    var attributes = this._dataManager.get(id)["attributes"];
    attributes[key] = value;
    this._dataManager.set(id, "attributes", attributes);
  },

  // Open common dialogs (info, warning, error, question)
  _addCommonPage: function (id, message, icon) {
    var dialogId = "customDialog" + id;
    var pageId = dialogId + "content";
    $("body").append(
      "<div id='" + pageId + "'>" +
      "<div class='customDialogIcon customDialogIcon" + icon + "'></div>" +
      "<div class='customDialogMessage'>" + message + "</div>" +
      "</div>");
    this.addPage("#" + dialogId, "#" + pageId);
  },
  openInfo: function (message, title, onOk) {
    var id = this._createDialog({
      title: (title == null ? "Information" : title),
      cancelButton: "hidden",
      previousButton: "hidden",
      autoClose: true,
      autoDelete: true,
      onOk: onOk,
      onCancel: onOk
    });
    this._addCommonPage(id, message, "Information");
    this.open("#customDialog" + id);
  },
  openWarning: function (message, title, onOk) {
    var id = this._createDialog({
      title: (title == null ? "Warning" : title),
      cancelButton: "hidden",
      previousButton: "hidden",
      autoClose: true,
      autoDelete: true,
      onOk: onOk,
      onCancel: onOk
    });
    this._addCommonPage(id, message, "Warning");
    this.open("#customDialog" + id);
  },
  openError: function (message, title, onOk) {
    var id = this._createDialog({
      title: (title == null ? "Error" : title),
      cancelButton: "hidden",
      previousButton: "hidden",
      autoClose: true,
      autoDelete: true,
      onOk: onOk,
      onCancel: onOk
    });
    this._addCommonPage(id, message, "Error");
    this.open("#customDialog" + id);
  },
  openConfirm: function (message, title, onOk, onCancel) {
    var id = this._createDialog({
      title: (title == null ? "Confirmation" : title),
      autoClose: true,
      autoDelete: true,
      onOk: onOk,
      onCancel: onCancel
    });
    this._addCommonPage(id, message, "Question");
    this.open("#customDialog" + id);
  },

  /// Open / close a loading dialog
  openLoader: function (abortFunction, message) {
    // Loader already open?
    try {
      var id = this._dataManager.getId("#customDialogLoader");
      var dialogId = "customDialog" + id;

      // Already open, just change the abort function
      var attributes = this._dataManager.get(id)["attributes"];
      attributes["onCancel"] = abortFunction;
      attributes["cancelButton"] = (abortFunction == null ? "hidden" : "");
      this._dataManager.set(id, "attributes", attributes);
      this._displayNavigation("#" + dialogId);
      return;
    } catch (e) { }

    // Create a new dialog with possibly a cancel button
    var id = (abortFunction == null) ?
      this._createDialog({
        title: "Please wait...",
        cancelButton: "hidden",
        previousButton: "hidden",
        okButton: "hidden",
        nextButton: "hidden",
        closeButton: "hidden",
        autoClose: true,
        autoDelete: true,
        cancelEnabled: false
      }) : this._createDialog({
        title: "Please wait...",
        previousButton: "hidden",
        okButton: "hidden",
        nextButton: "hidden",
        closeButton: "hidden",
        onCancel: abortFunction,
        autoClose: true,
        autoDelete: true
      });

    // Add a special class to this dialog
    $("#customDialog" + id).addClass('customDialogButtonRight');

    // Add content
    $("body").append(
      "<div id='customDialogLoader'>" +
      (message != null ? "<div style='margin: 20px 10px'>" + message + "</div>" : "") +
      "<div class='customProgress' style='margin: 20px 10px'>" +
      "<div data-effect='slide-left' class='customProgressBar' role='progressbar' aria-valuenow='100' aria-valuemin='0' aria-valuemax='100' style='width: 100%; transition: all 0.7s ease-in-out 0s;'></div>" +
      "</div>" +
      "</div>");
    this.addPage("#customDialog" + id, "#customDialogLoader");

    // Open it
    this.open("#customDialog" + id);
  },
  closeLoader: function () {
    if ($("#customDialogLoader").length)
      this.close("#customDialogLoader");
  },

  /// Trigger event to a dialog
  _openIds: [],
  open: function (selector) {
    this._displayNavigation(selector);
    var id = this._dataManager.getId(selector);
    var attributes = this._dataManager.get(id)["attributes"];

    // Back to first page
    if (this._dataManager.get(id)["currentPage"] > 0)
      this._dataManager.set(id, "currentPage", 0);
    this._displayNavigation(selector);

    if (attributes["onOpen"] != null)
      attributes["onOpen"]();
    $("#customDialog" + id).addClass('customDialogEnabled');
    this._openIds[this._openIds.length] = id;
  },
  close: function (selector) {
    var id = this._dataManager.getId(selector);
    var attributes = this._dataManager.get(id)["attributes"];

    var dialogId = "customDialog" + id;
    $("#" + dialogId).removeClass('customDialogEnabled');

    // autoDelete?
    if (attributes["autoDelete"] != null && attributes["autoDelete"] == true)
      $("#" + dialogId).remove();

    var index = this._openIds.indexOf(id);
    if (index > -1) {
      this._openIds.splice(index, 1);
    }
  },
  closeAll: function () {
    $(".customDialog").each(function () { customDialog.close("#" + this.id); });
  },
  closeLast: function () {
    if (this._openIds.length > 0)
      this.cancel("#customDialog" + this._openIds[this._openIds.length - 1]);
  },
  cancel: function (selector) {
    var id = this._dataManager.getId(selector);
    var attributes = this._dataManager.get(id)["attributes"];

    // Cancel allowed?
    if (attributes["cancelEnabled"] != null && attributes["cancelEnabled"] == false)
      return;

    // onCancel?
    if (attributes["onCancel"] != null)
      attributes["onCancel"]();

    // autoClose?
    if (attributes["autoClose"] != null && attributes["autoClose"] == true)
      this.close(selector);
  },
  ok: function (selector) {
    var id = this._dataManager.getId(selector);
    var attributes = this._dataManager.get(id)["attributes"];

    // onOk?
    if (attributes["onOk"] != null)
      attributes["onOk"]();

    // autoClose?
    if (attributes["autoClose"] != null && attributes["autoClose"] == true)
      this.close(selector);
  },
  previous: function (selector) {
    var id = this._dataManager.getId(selector);
    var currentPage = this._dataManager.get(id)["currentPage"];
    if (currentPage > 0)
      this._dataManager.set(id, "currentPage", currentPage - 1);
    this._displayNavigation(selector);
  },
  next: function (selector) {
    var id = this._dataManager.getId(selector);
    var pageCount = this._dataManager.get(id)["pageCount"];
    var currentPage = this._dataManager.get(id)["currentPage"];
    if (currentPage < pageCount - 1)
      this._dataManager.set(id, "currentPage", currentPage + 1);
    this._displayNavigation(selector);
  }
}
$(document).keyup(function (e) {
  if (e.keyCode == 27)
    customDialog.closeLast();
});


//////////////////////
// Date time picker //
//////////////////////


/////////////////
// Image popup //
/////////////////

var customImagePopup = {
  // Private functions and attributes
  _dataManager: customWidgetsUtils.createDataManager('customImagePopupId'),

  // Initialize a custom image popup, the selector must an img
  initialize: function (selector) {
    // Keep data about this component
    var id = this._dataManager.createNewId();
    this._dataManager.initializeIdAttribute(selector, id);
    this._dataManager.set(id, "open", false);

    // Create a container for the image
    $("body").append(
      "<div id='customImagePopup" + id + "' class='customImagePopup'>" +
      "<div class='customImagePopupShadow'></div>" +
      "<div class='customImagePopupWindow'>" +
      "</div>" +
      "</div>");

    // The image is clickable
    $(selector).click(function () { customImagePopup._onClick(id, this); });
    $(selector).hover(function () { $(this).css('cursor', 'pointer'); });
    $('#customImagePopup' + id).click(function () { customImagePopup._onClose(id); });
  },

  _onClick: function (id, elt) {
    // Already open?
    if (this._dataManager.get(id)["open"])
      return;

    // Get the source of the image
    var src = $(elt).attr('src');
    if (src == null || src == "")
      throw 'Cannot get the source of the image';

    // Display the image
    $('#customImagePopup' + id + ' .customImagePopupWindow').html("<img src='" + src + "' />");
    $('#customImagePopup' + id).addClass('customImagePopupEnabled');

    // Now it is open
    this._dataManager.set(id, "open", true);
  },

  _onClose: function (id) {
    // Now it is closed
    this._dataManager.set(id, "open", false);
    $('#customImagePopup' + id).removeClass('customImagePopupEnabled');
  }
}
