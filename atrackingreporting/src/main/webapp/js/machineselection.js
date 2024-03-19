// Copyright (C) 2009-2023 Lemoine Automation Technologies
// Copyright (C) 2023 Atsora Solutions
//
// SPDX-License-Identifier: EPL-2.0

function MachineDataAdaptor(groupClass)
{
  this._companySelect = $("." + groupClass + " .PulseCompanies_input");
  this._departmentSelect = $("." + groupClass + " .PulseDepartments_input");
  this._categorySelect = $("." + groupClass + " .PulseCategories_input");
  this._subcategorySelect = $("." + groupClass + " .PulseSubcategories_input");
  this._cellSelect = $("." + groupClass + " .PulseCells_input");
  this._machineSelect = $("." + groupClass + " .PulseMachines_input");
  
  _string2int = function(str) {
    return (str == "null" || str == null || str == "") ? -1 : parseInt(str);
  };
  
  this._getOptions = function(elt, possibleIndexes) {
    var options = [];
    if (elt != null) {
      elt.find('option').each(function() {
        if (this.value != "-1") {
          let valueInt = _string2int(this.value);
          if (possibleIndexes == null || possibleIndexes.includes(valueInt))
            options[options.length] = {
              id: valueInt,
              display: this.text,
              selected: this.selected
            };
        }
      });
    }
    return options;
  };
  
  this._selectOptions = function(elt, options) {
    if (elt == null)
      return;
    
    if (elt.attr("multiple")) {
      var found = false;
      if (options != null && options.length > 0) {
        elt.find('option').each(function() {
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
  
  // List of machines with their associated attributes
  this.getMachines = function() {
    var machines = [];
    if (this._machineSelect == null)
      return machines;
    
    this._machineSelect.find('option').each(function() {
      if (this.value != "-1") {
        var elts = this.text.split(',');
        if (elts.length >= 6) {
          var display = elts[5];
          for (var i = 6; i < elts.length; i++)
            display += "," + elts[i];
          
          machines[machines.length] = {
            id: _string2int(this.value),
            display: display,
            companyId: _string2int(elts[0]),
            departmentId: _string2int(elts[1]),
            categoryId: _string2int(elts[2]),
            subcategoryId: _string2int(elts[3]),
            cellId: _string2int(elts[4]),
            selected: this.selected
          };
        }
      }
    });

    return machines;
  };

  this._getPossibleIndexes = function(machines, attribute) {
    let result = [];
    if (machines != null)
      for (let i = 0; i < machines.length; i++)
        if (machines[i][attribute] != -1 && !result.includes(machines[i][attribute]))
          result.push(machines[i][attribute]);
    return result;
  };
  
  this.getCategories = function() {
    let machines = this.getMachines();
    return {
      companies: this._getOptions(this._companySelect, this._getPossibleIndexes(machines, 'companyId')),
      departments: this._getOptions(this._departmentSelect, this._getPossibleIndexes(machines, 'departmentId')),
      categories: this._getOptions(this._categorySelect, this._getPossibleIndexes(machines, 'categoryId')),
      subcategories: this._getOptions(this._subcategorySelect, this._getPossibleIndexes(machines, 'subcategoryId')),
      cells: this._getOptions(this._cellSelect, this._getPossibleIndexes(machines, 'cellId'))
    };
  };
  
  this.selectMachines = function(machineIds) {
    this._selectOptions(this._machineSelect, machineIds);
  };
  
  this.selectCategories = function(categories) {
    this._selectOptions(this._companySelect, categories.companies);
    this._selectOptions(this._departmentSelect, categories.departments);
    this._selectOptions(this._categorySelect, categories.categories);
    this._selectOptions(this._subcategorySelect, categories.subcategories);
    this._selectOptions(this._cellSelect, categories.cells);
  };
}

var machineSelection = {
  // Private functions and attributes
  _dataManager: customWidgetsUtils.createDataManager('customMachineSelectionId'),
  _formatDisplayableElements: function(displayableElements, singularTitle, pluralTitle) {
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
  _getCategoryList: function(id) {
    var getCategoryList2 = function(idType, catName, categories) {
      var list = "";
      if (categories != null && categories.length > 0) {
        list += "<li><span>" + catName + "</span><ul style='display:none;'>";
        for (var i = 0; i < categories.length; i++)
          list +=
            "<li>" +
              "<label><input type='checkbox' " + idType + "=" + categories[i].id + "><span>" +
              categories[i].display + "</span></label>" +
            "</li>";
        list += "</ul></li>";
      }
      return list;
    };
    
    var data = this._dataManager.get(id);
    var categories = data['machineDataAdaptor'].getCategories();
    
    var list = "<ul><li><ul><li><label><input type='checkbox' class='customMachineSelectionNoFilter'><span>No filter</span></label></li></ul></li>";
    list += getCategoryList2("companyId", "Companies", categories.companies);
    list += getCategoryList2("departmentId", "Departments", categories.departments);
    list += getCategoryList2("categoryId", "Categories", categories.categories);
    list += getCategoryList2("subcategoryId", "Subcategories", categories.subcategories);
    list += getCategoryList2("cellId", "Cells", categories.cells);
    
    return list + "</ul>";
  },
  _getMachineList: function(id) {
    var data = this._dataManager.get(id);
    var machines = data['machineDataAdaptor'].getMachines();
    
    var list = "";
    if (machines != null && machines.length > 0) {
      list = "<ul>";
      for (var i = 0; i < machines.length; i++) {
        list +=
          "<li machineId='" + machines[i].id + "' companyId='" + machines[i].companyId + "' departmentId='" + machines[i].departmentId + "' categoryId='" + machines[i].categoryId + "' subcategoryId='" + machines[i].subcategoryId + "' cellId='" + machines[i].cellId + "'>" +
            "<label><input type='checkbox'><span>" + machines[i].display + "</span></label>" +
          "</li>";
      }
      list += "</ul>";
    } else {
      list = "No machines";
    }
    return list;
  },
  
  _selectFromBase: function(id) {
    var selectCategories = function(categories, idType) {
      var selected = false;
      if (categories != null && categories.length > 0) {
        for (var i = 0; i < categories.length; i++) {
          if (categories[i].selected) {
            $("#machineSelectionDialogPart1-" + id + " .machineSelectionDialogCategoryList input[type=checkbox][" + idType + "=" + categories[i].id + "]").prop('checked', true);
            selected = true;
          }
        }
      }
      return selected;
    };
    
    // Categories
    $("#machineSelectionDialogPart1-" + id + " .machineSelectionDialogCategoryList input[type=checkbox]").prop('checked', false);
    var data = this._dataManager.get(id);
    var categories = data['machineDataAdaptor'].getCategories();
    var withFilter = false;
    withFilter |= selectCategories(categories.companies, "companyId");
    withFilter |= selectCategories(categories.departments, "departmentId");
    withFilter |= selectCategories(categories.categories, "categoryId");
    withFilter |= selectCategories(categories.subcategories, "subcategoryId");
    withFilter |= selectCategories(categories.cells, "cellId");
    $("#machineSelectionDialogPart1-" + id + " .machineSelectionDialogCategoryList input[type=checkbox].customMachineSelectionNoFilter").prop('checked', !withFilter);
    
    // Machines
    $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogMachineList input[type=checkbox]").prop('checked', false);
    var machines = data['machineDataAdaptor'].getMachines();
    withFilter = false;
    for (var i = 0; i < machines.length; i++) {
      if (machines[i].selected) {
        $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogMachineList li[machineId=" + machines[i].id + "] input[type=checkbox]").prop('checked', true);
        $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogMachineList li[machineId=" + machines[i].id + "]").attr('selection', true);
        withFilter = true;
      }
    }
    if (!withFilter && !data['uniqueMachine']) {
      $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogMachineList li input[type=checkbox]").prop('checked', true);
      $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogMachineList li").attr('selection', true);
    }
  },
  
  _updateBaseSelection: function(id) {
    var getIds = function(typeId) {
      var list = [];
      $("#machineSelectionDialogPart1-" + id + " .machineSelectionDialogCategoryList input:checkbox:checked[" + typeId + "]").each(function() {
        list[list.length] = parseInt($(this).attr(typeId));
      });
      return list;
    };
    
    var data = this._dataManager.get(id);
    var dataAdaptor = data['machineDataAdaptor'];
    
    var filter = $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogSearchInput").val().toLowerCase();
    
    var allMachinesSelected = true;
    $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogMachineList li:visible input:checkbox").each(function() {
      if (!$(this).prop("checked"))
        allMachinesSelected = false;
    });
    
    if (!data['uniqueMachine'] && allMachinesSelected && !filter) {
      // Select only categories
      dataAdaptor.selectCategories({
        companies: getIds("companyId"),
        departments: getIds("departmentId"),
        categories: getIds("categoryId"),
        subcategories: getIds("subcategoryId"),
        cells: getIds("cellId")
      });
      dataAdaptor.selectMachines(null);
    } else {
      // Select only machines
      dataAdaptor.selectCategories({
        companies: [],
        departments: [],
        categories: [],
        subcategories: [],
        cells: []
      });
      var selectedMachines = [];
      $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogMachineList li[selectable=true][selection=true]:visible").each(function() {
        selectedMachines[selectedMachines.length] = parseInt($(this).attr("machineId"));
      });
      dataAdaptor.selectMachines(selectedMachines);
    }
  },
  
  _updateView: function(id, updateBaseSelection) {
    // All machines visible at first, all markers "selection" removed, markers "selectable" added
    $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogMachineList li").show();
    $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogMachineList li").removeAttr("selection");
    $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogMachineList li").attr("selectable", true);
    
    // Category filters or not?
    if ($("#machineSelectionDialogPart1-" + id + " .machineSelectionDialogCategoryList input:checkbox:checked").not(".customMachineSelectionNoFilter").length == 0) {
      $("#machineSelectionDialogPart1-" + id + " .machineSelectionDialogCategoryList input[type=checkbox].customMachineSelectionNoFilter").prop('checked', true);
    } else {
      $("#machineSelectionDialogPart1-" + id + " .machineSelectionDialogCategoryList input[type=checkbox].customMachineSelectionNoFilter").prop('checked', false);
      
      // Visibility of machines
      var filterCategory = function(typeId) {
        var selectedCategories = [];
        $("#machineSelectionDialogPart1-" + id + " .machineSelectionDialogCategoryList input:checkbox:checked[" + typeId + "]").each(function() {
          selectedCategories[selectedCategories.length] = $(this).attr(typeId);
        });
        if (selectedCategories.length > 0) {
          $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogMachineList li").each(function() {
            if (selectedCategories.indexOf($(this).attr(typeId)) == -1) {
              $(this).hide();
              $(this).removeAttr("selectable");
            }
          });
        }
      };
      filterCategory("companyId");
      filterCategory("departmentId");
      filterCategory("categoryId");
      filterCategory("subcategoryId");
      filterCategory("cellId");
    }
    
    // Add markers "selection"
    $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogMachineList li").each(function() {
      if ($(this).find("input:checkbox").prop("checked"))
        $(this).attr("selection", true);
    });
    
    // Text filter
    var strToFind = $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogSearchInput").val().toLowerCase();
    $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogMachineList li:visible").each(function() {
      if ($(this).find("span").text().toLowerCase().indexOf(strToFind) == -1)
        $(this).hide();
    });
    
    var data = this._dataManager.get(id);
    if (!data['uniqueMachine']) {
      // State of "select all"
      var withSelected = false;
      var withUnselected = false;
      $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogMachineList li").each(function() {
        if ($(this).css('display') != 'none') {
          if ($(this).find("input:checkbox").prop("checked"))
            withSelected = true;
          else
            withUnselected = true;
        }
      });
      
      if (withSelected && withUnselected) {
        $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogCheckAll").prop("checked", false);
        $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogCheckAll").prop("indeterminate", true);
      } else if (withSelected) {
        $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogCheckAll").prop("checked", true);
        $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogCheckAll").prop("indeterminate", false);
      } else if (withUnselected) {
        $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogCheckAll").prop("checked", false);
        $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogCheckAll").prop("indeterminate", false);
      }
    }
  },
  
  // Get a summary of the selected categories or machines
  _getSummary: function(id) {
    var data = this._dataManager.get(id);
    var machineDataAdaptor = data['machineDataAdaptor'];
    
    var txt = "";
    var machines = machineDataAdaptor.getMachines();
    var categories = machineDataAdaptor.getCategories();
    
    // Machines
    if (machines != null && machines.length > 0)
      txt = this._formatDisplayableElements(machines, null, null);
    
    // Categories
    if (txt == "") {
      if (categories != null) {
        // Companies
        txt = this._formatDisplayableElements(categories.companies, "Company", "Companies");
        
        // Departments
        var tmp = this._formatDisplayableElements(categories.departments, "Department", "Departments");
        if (tmp != "")
          txt += (txt != "" ? "<br />" + tmp : tmp);
        
        // Categories
        var tmp = this._formatDisplayableElements(categories.categories, "Category", "Categories");
        if (tmp != "")
          txt += (txt != "" ? "<br />" + tmp : tmp);
        
        // Subcategories
        var tmp = this._formatDisplayableElements(categories.subcategories, "Subcategory", "Subcategories");
        if (tmp != "")
          txt += (txt != "" ? "<br />" + tmp : tmp);
        
        // Cells
        var tmp = this._formatDisplayableElements(categories.cells, "Cell", "Cells");
        if (tmp != "")
          txt += (txt != "" ? "<br />" + tmp : tmp);
      }
    }
    
    if (txt == "")
      txt = data['uniqueMachine'] ? "No machines selected" : "All machines selected";
    
    return txt;
  },
  
  // Initialize a machine selection dialog
  initialize: function(selector, machineDataAdaptor, uniqueMachine) {
    var id = this._dataManager.createNewId();
    this._dataManager.initializeIdAttribute(selector, id);
    this._dataManager.set(id, "machineDataAdaptor", machineDataAdaptor);
    uniqueMachine = (uniqueMachine == null ? false : uniqueMachine);
    this._dataManager.set(id, "uniqueMachine", uniqueMachine);
  
    // Summary
    $(selector).append(
      "<button title='Change machines' class='machineSelectionDialogButton button2' id='machineSelectionDialogButton-" + id + "' role='button' aria-disabled='false'>" +
        "<span class='button2-icon'></span>" +
      "</button>" +
      "<div class='machineSelectionDialogSummary' id='machineSelectionDialogSummary-" + id + "'></div>" +
      "<div class='machineSelectionDialogPart1' id='machineSelectionDialogPart1-" + id + "'>" +
        "<div class='machineSelectionDialogCategoryList'>" +
          this._getCategoryList(id) +
        "</div>" +
      "</div>" +
      "<div class='machineSelectionDialogPart2' id='machineSelectionDialogPart2-" + id + "'>" +
        (uniqueMachine ? "" :
        "<label><input type='checkbox' class='machineSelectionDialogCheckAll'><span>Select all</span></label>") +
        "<div class='machineSelectionDialogMachineList'" + (uniqueMachine ? "style='top: 4px;'" : "") + ">" +
          this._getMachineList(id) +
        "</div>" +
        "<div class='machineSelectionDialogSearch'>" +
          "<button title='Clear search' class='machineSelectionDialogClearSearch button2' id='machineSelectionDialogClearSearch-" + id + "' role='button' aria-disabled='false'>" +
            "<span class='button2-icon'></span>" +
          "</button>" +
          "<span><input type='text' class='machineSelectionDialogSearchInput' placeholder='Search...'></input></span>" +
        "</div>" +
      "</div>"
    );
    
    // Create a dialog
    customDialog.initialize("#machineSelectionDialogPart1-" + id, {
      title: uniqueMachine ? "Select a machine" : "Select machines",
      autoClose: false,
      onOpen: function() {
        // Reinitialize selection and view
        $("#machineSelectionDialogClearSearch-" + id).parent().find(".machineSelectionDialogSearchInput").val("");
        machineSelection._selectFromBase(id);
        machineSelection._updateView(id);
      },
      onOk: function() {
        if ($("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogMachineList li[selectable=true][selection=true]:visible").length == 0) {
          if (uniqueMachine) {
            customDialog.openError("Please select one machine.");
            return;
          } else {
            // It's ok if "Select all" is checked
            if (!$("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogCheckAll").prop("checked")) {
              customDialog.openError("Please select at least one machine.");
              return;
            }
          }
        }
        // Store the new parameters
        machineSelection._updateBaseSelection(id);
      
        // Update summary
        $("#machineSelectionDialogSummary-" + id).html(machineSelection._getSummary(id));
        
        customDialog.close("#machineSelectionDialogPart1-" + id);
      },
      onCancel: function() {
        customDialog.close("#machineSelectionDialogPart1-" + id);
      },
      fullScreenOnSmartphone: true,
      fixedHeight: true
    });
    customDialog.addPage("#machineSelectionDialogPart1-" + id, "#machineSelectionDialogPart2-" + id);
    
    // Initialize selection, view, summary
    this._selectFromBase(id);
    this._updateView(id);
    $("#machineSelectionDialogSummary-" + id).html(machineSelection._getSummary(id));
    
    ///  Events ///
    
    // Change a category filter
    $("#machineSelectionDialogPart1-" + id + " .machineSelectionDialogCategoryList input:checkbox").change(function() {
      if ($(this).hasClass("customMachineSelectionNoFilter"))
        $("#machineSelectionDialogPart1-" + id + " input:checkbox").prop("checked", false);
      machineSelection._updateView(id);
    });
    
    // Check / uncheck a machine
    $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogMachineList input:checkbox").change(function() {
      if (uniqueMachine) {
        // Uncheck all except maybe what has just been checked
        $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogMachineList li input:checkbox")
          .prop("checked", false);
        $(this).prop("checked", true);
      }
      machineSelection._updateView(id);
    });
    
    // Filter with a machine name
    $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogSearchInput").keyup(function() {
      machineSelection._updateView(id);
    });
    
    // Clear filter
    $("#machineSelectionDialogClearSearch-" + id).click(function() {
      $(this).parent().find(".machineSelectionDialogSearchInput").val("");
      machineSelection._updateView(id);
    });
    
    // Check all / none
    if (!uniqueMachine) {
      $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogCheckAll").change(function() {
        if (!$(this).prop("undefined")) {
          $("#machineSelectionDialogPart2-" + id + " .machineSelectionDialogMachineList li:visible input:checkbox")
            .prop("checked", $(this).prop("checked"));
          machineSelection._updateView(id);
        }
      });
    }
    
    // Summary
    $("#machineSelectionDialogSummary-" + id).html(this._getSummary(id));
    
    // Collapse / Expand machine categories
    $(".machineSelectionDialogCategoryList > ul > li > span").click(function() {
      $(this).parent().find("ul").toggle();
    });
    
    // Open selection dialog
    $("#machineSelectionDialogButton-" + id).click(function() {
      customDialog.open("#machineSelectionDialogPart1-" + id);
    });
  }
}