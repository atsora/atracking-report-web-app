// Copyright (C) 2009-2023 Lemoine Automation Technologies
//
// SPDX-License-Identifier: EPL-2.0

function AccordionDataAdaptor(groupClass) {
  this._firstLevelSelect = $("." + groupClass + " .parameter:nth-of-type(1) select");
  this._secondLevelSelect = $("." + groupClass + " .parameter:nth-of-type(2) select");

  _string2int = function (str) {
    return (str == "null" || str == null || str == "") ? -1 : parseInt(str);
  };

  this._getOptions = function (elt) {
    var options = [];
    if (elt != null) {
      elt.find('option').each(function () {
        if (this.value != "-1") {
          options[options.length] = {
            id: _string2int(this.value),
            display: this.text,
            selected: this.selected
          };
        }
      });
    }
    return options;
  };

  this._selectOptions = function (elt, options) {
    if (elt == null)
      return;

    if (elt.attr("multiple")) {
      var found = false;
      if (options != null && options.length > 0) {
        elt.find('option').each(function () {
          if (options.indexOf(_string2int(this.value)) != -1) {
            found = true;
            this.selected = true;
          } else
            this.selected = false;
        });
      }
      if (!found)
        elt.val("-1");
    } else {
      if (options != null && options.length > 0)
        elt.val(options[0]);
      else
        elt.val("-1");
    }
  };

  // List of second level elements with a reference of the first level element
  this.getSecondLevel = function () {
    var secondLevelElements = [];
    if (this._secondLevelSelect == null)
      return secondLevelElements;

    this._secondLevelSelect.find('option').each(function () {
      if (this.value != "-1") {
        var elts = this.text.split(',');
        if (elts.length >= 2) {
          var display = elts[1];
          for (var i = 2; i < elts.length; i++)
            display += "," + elts[i];

          secondLevelElements[secondLevelElements.length] = {
            id: _string2int(this.value),
            display: display,
            firstLevelId: _string2int(elts[0]),
            selected: this.selected
          };
        }
      }
    });

    return secondLevelElements;
  };

  this.selectSecondLevel = function (elementIds) {
    this._selectOptions(this._secondLevelSelect, elementIds);
  };

  this.getFirstLevel = function () {
    return this._getOptions(this._firstLevelSelect);
  };

  this.selectFirstLevel = function (elementIds) {
    this._selectOptions(this._firstLevelSelect, elementIds);
  };
}

var accordionSelection = {
  // Private functions and attributes
  _uniqueElement: false,
  _dataManager: customWidgetsUtils.createDataManager('customAccordionSelectionId'),
  _formatDisplayableElements: function (displayableElements, singularTitle, pluralTitle) {
    var txt = "";
    var count = 0;

    if (displayableElements != null && displayableElements.length > 0) {
      for (var i = 0; i < displayableElements.length; i++) {
        if (displayableElements[i].selected) {
          if (count > 0)
            txt += ", ";
          txt += displayableElements[i].display;
          count++;
        }
      }
    }

    if (count == 1 && singularTitle != null && singularTitle != "")
      return singularTitle + ": " + txt;
    if (count > 1 && pluralTitle != null && pluralTitle != "")
      return pluralTitle + ": " + txt;
    return txt;
  },
  _getElementList: function (id, uniqueElement) {
    var data = this._dataManager.get(id);
    var firstLevels = data['accordionDataAdaptor'].getFirstLevel();
    var secondLevels = data['accordionDataAdaptor'].getSecondLevel();

    // Build the html
    var html = "";
    for (var i = 0; i < firstLevels.length; i++) {
      // New first level
      var firstLevel = firstLevels[i];
      var htmlTmp = "<div class='customAccordionFirstLevel' firstLevelId='" + firstLevel.id + "'>" +
        "<div>" + (uniqueElement ? "" : "<input type='checkbox' firstLevelId='" + firstLevel.id + "'>") + "<span>" + firstLevel.display + "</span></div><ul style='display:none'>";

      // Display all second levels within the first level
      var withSecondLevel = false;
      for (var j = 0; j < secondLevels.length; j++) {
        var secondLevel = secondLevels[j];
        if (secondLevel.firstLevelId == firstLevel.id) {
          withSecondLevel = true;
          htmlTmp +=
            "<li firstLevelId='" + firstLevel.id + "' secondLevelId='" + secondLevel.id + "'>" +
            "<label><input type='checkbox' secondLevelId='" + secondLevel.id + "'><span>" + secondLevel.display + "</span></label>" +
            "</li>";
        }
      }

      // End of the first level
      htmlTmp += "</ul></div>";

      if (withSecondLevel)
        html += htmlTmp;
    }

    // Get the orphans
    var orphans = new Array();
    for (var j = 0; j < secondLevels.length; j++) {
      var secondLevel = secondLevels[j];
      if (secondLevel.firstLevelId == -1)
        orphans[orphans.length] = secondLevel;
    }

    return html;
  },

  _selectFromBase: function (id) {
    // Clear selection
    $("#accordionSelectionDialogPart1-" + id + " .customAccordionFirstLevel input[type=checkbox]").prop('checked', false);

    // First level selection
    var withFilter = false;
    var data = this._dataManager.get(id);
    var firstLevels = data['accordionDataAdaptor'].getFirstLevel();
    if (firstLevels != null && firstLevels.length > 0) {
      for (var i = 0; i < firstLevels.length; i++) {
        if (firstLevels[i].selected) {
          $("#accordionSelectionDialogPart1-" + id + " .customAccordionFirstLevel[firstLevelId=" + firstLevels[i].id + "] ul input[type=checkbox]").prop('checked', true);
          withFilter = true;
        }
      }
    }

    // Second level selection
    var secondLevels = data['accordionDataAdaptor'].getSecondLevel();
    for (var i = 0; i < secondLevels.length; i++) {
      if (secondLevels[i].selected) {
        $("#accordionSelectionDialogPart1-" + id + " .customAccordionFirstLevel li[secondLevelId=" + secondLevels[i].id + "] input[type=checkbox]").prop('checked', true);
        $("#accordionSelectionDialogPart1-" + id + " .customAccordionFirstLevel li[secondLevelId=" + secondLevels[i].id + "]").attr('selection', true);
        withFilter = true;
      }
    }

    // Everything selected?
    if (!withFilter && !this._uniqueElement) {
      $("#accordionSelectionDialogPart1-" + id + " .customAccordionFirstLevel li input[type=checkbox]").prop('checked', true);
      $("#accordionSelectionDialogPart1-" + id + " .customAccordionFirstLevel li").attr('selection', true);
    }
  },

  _updateBaseSelection: function (id) {
    var data = this._dataManager.get(id);
    var dataAdaptor = data['accordionDataAdaptor'];

    if (!this._uniqueElement && $("#accordionSelectionDialogPart1-" + id + " .accordionSelectionDialogElementList li:not([selection])").length == 0) {
      // Select everything
      dataAdaptor.selectFirstLevel(null);
      dataAdaptor.selectSecondLevel(null);
    } else {
      // Check if first levels or second levels will be selected
      var firstLevels = [];
      var secondLevels = [];
      var firstLevelSelection = true;
      $("#accordionSelectionDialogPart1-" + id + " .customAccordionFirstLevel").each(function () {
        var firstLevelId = parseInt($(this).attr("firstLevelId"));
        var withChecked = false;
        var withUnchecked = false;
        $(this).find('li input').each(function () {
          if ($(this).prop('checked')) {
            withChecked = true;
            secondLevels[secondLevels.length] = parseInt($(this).attr("secondLevelId"));
          } else {
            withUnchecked = true;
          }
        });
        if (withChecked) {
          if (withUnchecked)
            firstLevelSelection = false;
          else
            firstLevels[firstLevels.length] = firstLevelId;
        }
      });

      if (firstLevelSelection && !this._uniqueElement) {
        dataAdaptor.selectFirstLevel(firstLevels);
        dataAdaptor.selectSecondLevel(null);
      } else {
        dataAdaptor.selectFirstLevel(null);
        dataAdaptor.selectSecondLevel(secondLevels);
      }
    }
  },

  _updateView: function (id, updateBaseSelection) {
    // All markers "selection" removed
    $("#accordionSelectionDialogPart1-" + id + " .customAccordionFirstLevel li").removeAttr("selection");

    // Add markers "selection" and "fullFirstLevel"
    var allElements = true;
    $("#accordionSelectionDialogPart1-" + id + " .customAccordionFirstLevel li").each(function () {
      if ($(this).find("input:checkbox").prop("checked"))
        $(this).attr("selection", true);
      else
        allElements = false;
    });
    $("#accordionSelectionDialogPart1-" + id + " .accordionSelectionDialogCheckAll").attr("allElements", allElements);

    // Text filter
    var strToFind = $("#accordionSelectionDialogPart1-" + id + " .accordionSelectionDialogSearchInput").val().toLowerCase();
    $("#accordionSelectionDialogPart1-" + id + " .customAccordionFirstLevel").show();
    $("#accordionSelectionDialogPart1-" + id + " .customAccordionFirstLevel div").show();
    $("#accordionSelectionDialogPart1-" + id + " .customAccordionFirstLevel span").show();
    $("#accordionSelectionDialogPart1-" + id + " .customAccordionFirstLevel li").show();
    $("#accordionSelectionDialogPart1-" + id + " .customAccordionFirstLevel").each(function () {
      if ($(this).find("div span").text().toLowerCase().indexOf(strToFind) == -1) {
        // Search the keyword in the children
        var found = false;
        $(this).find("li").each(function () {
          if ($(this).find("span").text().toLowerCase().indexOf(strToFind) == -1)
            $(this).hide();
          else
            found = true;
        });
        if (!found)
          $(this).hide();
      }
    });

    if (!this._uniqueElement) {
      // Scan the selection
      var withSelectedGlobal = false;
      var withUnselectedGlobal = false;
      $("#accordionSelectionDialogPart1-" + id + " .customAccordionFirstLevel").each(function () {
        if ($(this).css('display') != 'none') {
          var withSelectedLocal = false;
          var withUnselectedLocal = false;
          $(this).find('li').each(function () {
            if ($(this).css('display') != 'none') {
              if ($(this).find('input:checkbox').prop("checked")) {
                withSelectedGlobal = true;
                withSelectedLocal = true;
              } else {
                withUnselectedGlobal = true;
                withUnselectedLocal = true;
              }
            }
          });
        }

        // State of a local "select all"
        if (withSelectedLocal && withUnselectedLocal) {
          $(this).find('div input:checkbox').prop("checked", false);
          $(this).find('div input:checkbox').prop("indeterminate", true);
        } else if (withSelectedLocal) {
          $(this).find('div input:checkbox').prop("checked", true);
          $(this).find('div input:checkbox').prop("indeterminate", false);
        } else if (withUnselectedLocal) {
          $(this).find('div input:checkbox').prop("checked", false);
          $(this).find('div input:checkbox').prop("indeterminate", false);
        }
      });

      // State of the global "select all"
      if (withSelectedGlobal && withUnselectedGlobal) {
        $("#accordionSelectionDialogPart1-" + id + " .accordionSelectionDialogCheckAll").prop("checked", false);
        $("#accordionSelectionDialogPart1-" + id + " .accordionSelectionDialogCheckAll").prop("indeterminate", true);
      } else if (withSelectedGlobal) {
        $("#accordionSelectionDialogPart1-" + id + " .accordionSelectionDialogCheckAll").prop("checked", true);
        $("#accordionSelectionDialogPart1-" + id + " .accordionSelectionDialogCheckAll").prop("indeterminate", false);
      } else if (withUnselectedGlobal) {
        $("#accordionSelectionDialogPart1-" + id + " .accordionSelectionDialogCheckAll").prop("checked", false);
        $("#accordionSelectionDialogPart1-" + id + " .accordionSelectionDialogCheckAll").prop("indeterminate", false);
      }
    }
  },

  // Get a summary of the selected groups or items
  _getSummary: function (id) {
    var data = this._dataManager.get(id);
    var accordionDataAdaptor = data['accordionDataAdaptor'];

    var txt = "";
    var items = accordionDataAdaptor.getSecondLevel();
    var categories = accordionDataAdaptor.getFirstLevel();

    // Items
    if (items != null && items.length > 0)
      txt = this._formatDisplayableElements(items, null, null);

    // Categories
    if (txt == "") {
      if (categories != null) {
        // Cells
        var tmp = this._formatDisplayableElements(categories, "Group", "Groups");
        if (tmp != "")
          txt += (txt != "" ? "<br />" + tmp : tmp);
      }
    }

    if (txt == "")
      txt = this._uniqueElement ? "No items selected" : "All items selected";

    return txt;
  },

  // Initialize a accordion selection dialog
  initialize: function (selector, accordionDataAdaptor, uniqueElement) {
    if (uniqueElement == null)
      uniqueElement = false;
    var id = this._dataManager.createNewId();
    this._dataManager.initializeIdAttribute(selector, id);
    this._dataManager.set(id, "accordionDataAdaptor", accordionDataAdaptor);
    this._uniqueElement = uniqueElement;

    // Summary
    $(selector).append(
      "<button title='Change selection' class='accordionSelectionDialogButton button2' id='accordionSelectionDialogButton-" + id + "' role='button' aria-disabled='false'>" +
      "<span class='button2-icon'></span>" +
      "</button>" +
      "<div class='accordionSelectionDialogSummary' id='accordionSelectionDialogSummary-" + id + "'></div>" +
      "<div class='accordionSelectionDialogPart1' id='accordionSelectionDialogPart1-" + id + "'>" +
      (uniqueElement ? "" :
        "<label><input type='checkbox' class='accordionSelectionDialogCheckAll'><span>Select all</span></label>") +
      "<div class='accordionSelectionDialogElementList'" + (uniqueElement ? "style='top: 4px;'" : "") + ">" +
      this._getElementList(id, uniqueElement) +
      "</div>" +
      "<div class='accordionSelectionDialogSearch'>" +
      "<button title='Clear search' class='accordionSelectionDialogClearSearch button2' id='accordionSelectionDialogClearSearch-" + id + "' role='button' aria-disabled='false'>" +
      "<span class='button2-icon'></span>" +
      "</button>" +
      "<span><input type='text' class='accordionSelectionDialogSearchInput' placeholder='Search...'></input></span>" +
      "</div>" +
      "</div>"
    );

    // Create a dialog
    customDialog.initialize("#accordionSelectionDialogPart1-" + id, {
      title: uniqueElement ? "Select an item" : "Select items",
      autoClose: false,
      onOpen: function () {
        // Reinitialize selection and view
        $("#accordionSelectionDialogClearSearch-" + id).parent().find(".accordionSelectionDialogSearchInput").val("");
        accordionSelection._selectFromBase(id);
        accordionSelection._updateView(id);
      },
      onOk: function () {
        if ($("#accordionSelectionDialogPart1-" + id + " .accordionSelectionDialogElementList li[selection=true]").length == 0) {
          customDialog.openError(uniqueElement ? "Please select one item." : "Please select at least one item.");
          return;
        }
        // Store the new parameters
        accordionSelection._updateBaseSelection(id);

        // Update summary
        $("#accordionSelectionDialogSummary-" + id).html(accordionSelection._getSummary(id));

        customDialog.close("#accordionSelectionDialogPart1-" + id);
      },
      onCancel: function () {
        customDialog.close("#accordionSelectionDialogPart1-" + id);
      },
      fullScreenOnSmartphone: true,
      fixedHeight: true
    });

    // Initialize selection, view, summary
    this._selectFromBase(id);
    this._updateView(id);
    $("#accordionSelectionDialogSummary-" + id).html(accordionSelection._getSummary(id));

    ///  Events ///

    // Check / uncheck an item
    $("#accordionSelectionDialogPart1-" + id + " .accordionSelectionDialogElementList li input:checkbox").change(function () {
      if (uniqueElement) {
        // Uncheck all except maybe what has just been checked
        $("#accordionSelectionDialogPart1-" + id + " .accordionSelectionDialogElementList li input:checkbox")
          .prop("checked", false);
        $(this).prop("checked", true);
      }
      accordionSelection._updateView(id);
    });

    // Filter with a item name
    $("#accordionSelectionDialogPart1-" + id + " .accordionSelectionDialogSearchInput").keyup(function () {
      accordionSelection._updateView(id);
    });

    // Clear filter
    $("#accordionSelectionDialogClearSearch-" + id).click(function () {
      $(this).parent().find(".accordionSelectionDialogSearchInput").val("");
      accordionSelection._updateView(id);
    });

    // Check all / none
    if (!uniqueElement) {
      $("#accordionSelectionDialogPart1-" + id + " .accordionSelectionDialogCheckAll").change(function () {
        if (!$(this).prop("undefined")) {
          var isChecked = $(this).prop("checked");
          $("#accordionSelectionDialogPart1-" + id + " .accordionSelectionDialogElementList li").each(function () {
            if ($(this).css('display') != 'none')
              $(this).find('input:checkbox').prop("checked", isChecked);
          });
          accordionSelection._updateView(id);
        }
      });

      $("#accordionSelectionDialogPart1-" + id + " .customAccordionFirstLevel div input:checkbox").change(function () {
        if (!$(this).prop("undefined")) {
          var isChecked = $(this).prop("checked");
          $(this).parent().parent().find("li").each(function () {
            if ($(this).css('display') != 'none')
              $(this).find("input:checkbox").prop("checked", isChecked);
          });
          accordionSelection._updateView(id);
        }
      });
      $("#accordionSelectionDialogPart1-" + id + " .customAccordionFirstLevel div input:checkbox").click(function (event) {
        event.stopPropagation();
      });
    }

    // Collapse or expand groups
    $(".customAccordionFirstLevel > div").click(function () {
      var initialVisible = $(this).parent().find("ul").is(':visible');
      $(".customAccordionFirstLevel > ul").hide();
      if (initialVisible)
        $(this).parent().find("ul").hide();
      else
        $(this).parent().find("ul").show();
    });

    // Open selection dialog
    $("#accordionSelectionDialogButton-" + id).click(function () {
      customDialog.open("#accordionSelectionDialogPart1-" + id);
    });
  }
}