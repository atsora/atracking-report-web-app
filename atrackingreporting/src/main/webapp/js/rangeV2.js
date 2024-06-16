// Called when a daterange needs to be filled
function RangeV2(rangeName, groupName, isDate) {
  var _updating = false;

  // Change the computed parameters
  let updateParameter2 = function (minDate, maxDate) {
    if (minDate == null && maxDate == null)
      return;

    _updating = true;
    try {
      // Update min date
      customDateTimePicker.set('#mindate' + groupName, minDate == null || !minDate.isValid() ? moment() : minDate);

      // Update max date
      let noend = $("#" + rangeName + "_type").val() == "since";
      if (!noend)
        customDateTimePicker.set('#maxdate' + groupName, maxDate == null || !maxDate.isValid() ? moment() : maxDate);
    } catch (e) {
      $.jqlog.error("Error when updating min / max: " + e.message);
    }
    _updating = false;
  };

  // Initialization
  var updateParameter = function () {
    var parameter = $("#" + rangeName + "_type").val();
    switch (parameter) {
      case "since":
        var minDate = customDateTimePicker.get("#mindate" + groupName);
        var format = isDate ? 'YYYY-MM-DD' : 'YYYY-MM-DD HH:mm:ss'; // Remove .SSS'; Not supported by BIRT
        if (minDate != null && minDate.isValid())
          parameter += "_" + minDate.format(format) + "_";
        break;
      case "explicit":
        var minDate = customDateTimePicker.get("#mindate" + groupName);
        var maxDate = customDateTimePicker.get("#maxdate" + groupName);
        var format = isDate ? 'YYYY-MM-DD' : 'YYYY-MM-DD HH:mm:ss'; // Remove .SSS'; Not supported by BIRT
        if (minDate != null && minDate.isValid()) {
          parameter += "_" + minDate.format(format) + "_";
          if (maxDate != null && maxDate.isValid())
            parameter += maxDate.format(format);
        }
        break;
      case "past":
        parameter += "_" + $("#" + rangeName + "_number").val() + "_" + $("#" + rangeName + "_unit").val();
        break;
      case "current":
        parameter += "_" + $("#" + rangeName + "_duration").val();
        break;
    }

    // Update min and max date time
    $("#" + rangeName + "_value").val(parameter);
    let rangeComputer = new RangeComputer(parameter, isDate);
    rangeComputer.compute(updateParameter2);
  };

  var initializeFromV2 = function () {
    var parameters = $("#" + rangeName + "_value").val().split('_');

    if (parameters.length == 3) {
      let p1 = parameters[1];
      let p2 = parameters[2];
      switch (parameters[0]) {
        case "explicit":
          var minDate = moment(p1, ['YYYY-MM-DD HH:mm:ss.SSS', 'YYYY-MM-DD HH:mm:ss',
            'YYYY-MM-DD HH:mm', 'YYYY-MM-DD',
            //'MM/DD/YYYY HH:mm:ss.SSS', 'MM/DD/YYYY HH:mm:ss', 'MM/DD/YYYY HH:mm', 'MM/DD/YYYY'
          ]);
          var maxDate = moment(p2, ['YYYY-MM-DD HH:mm:ss.SSS', 'YYYY-MM-DD HH:mm:ss',
            'YYYY-MM-DD HH:mm', 'YYYY-MM-DD',
            //'MM/DD/YYYY HH:mm:ss.SSS', 'MM/DD/YYYY HH:mm:ss', 'MM/DD/YYYY HH:mm', 'MM/DD/YYYY'
          ]);
          $("#" + rangeName + "_type").val("explicit");
          customDateTimePicker.set("#mindate" + groupName, minDate);
          customDateTimePicker.set("#maxdate" + groupName, maxDate);
          break;
        case "since":
          var minDate = moment(p1, ['YYYY-MM-DD HH:mm:ss.SSS', 'YYYY-MM-DD HH:mm:ss',
            'YYYY-MM-DD HH:mm', 'YYYY-MM-DD',
            //'MM/DD/YYYY HH:mm:ss.SSS', 'MM/DD/YYYY HH:mm:ss', 'MM/DD/YYYY HH:mm', 'MM/DD/YYYY'
          ]);
          $("#" + rangeName + "_type").val("since");
          customDateTimePicker.set("#mindate" + groupName, minDate);
          break;
        case "past":
          $("#" + rangeName + "_type").val("past");
          $("#" + rangeName + "_number").val(parameters[1]);
          $("#" + rangeName + "_unit").val(parameters[2]);
          break;
        case "current":
          $("#" + rangeName + "_type").val("current");
          $("#" + rangeName + "_duration").val(parameters[1] + "_" + parameters[2]);
          break;
        default:
          return false;
      }
      updateParameter();
      return true;
    }
    return false;
  };
  initializeFromV2();

  // Enable or not the end date time
  var enableEndDateTime = function () {
    $("#mindate" + groupName).prop('disabled', $("#" + rangeName + "_type").val() != "explicit" && $("#" + rangeName + "_type").val() != "since");
    $("#maxdate" + groupName).prop('disabled', $("#" + rangeName + "_type").val() != "explicit");
  };
  enableEndDateTime();

  // Show one row
  var showRow = function () {
    switch ($("#" + rangeName + "_type").val()) {
      case "explicit":
        $("#" + rangeName + "_row1_type").css('width', "100%");
        $("#" + rangeName + "_row2_past").hide();
        $("#" + rangeName + "_row2_current").hide();
        $("#mindate" + groupName).parent().css('width', "50%");
        $("#maxdate" + groupName).parent().show();
        break;
      case "since":
        $("#" + rangeName + "_row1_type").css('width', "100%");
        $("#" + rangeName + "_row2_past").hide();
        $("#" + rangeName + "_row2_current").hide();
        $("#mindate" + groupName).parent().css('width', "100%");
        $("#maxdate" + groupName).parent().hide();
        break;
      case "past":
        $("#" + rangeName + "_row1_type").css('width', "30%");
        $("#" + rangeName + "_row2_past").show();
        $("#" + rangeName + "_row2_current").hide();
        $("#mindate" + groupName).parent().css('width', "50%");
        $("#maxdate" + groupName).parent().show();
        break;
      case "current":
        $("#" + rangeName + "_row1_type").css('width', "30%");
        $("#" + rangeName + "_row2_past").hide();
        $("#" + rangeName + "_row2_current").show();
        $("#mindate" + groupName).parent().css('width', "50%");
        $("#maxdate" + groupName).parent().show();
        break;
    }
  };
  showRow();

  updateParameter();

  // Reactions
  $("#" + rangeName + "_type").change(function () { showRow(); enableEndDateTime(); updateParameter(); });
  $("#" + rangeName + "_number").change(function () { updateParameter(); });
  $("#" + rangeName + "_unit").change(function () { updateParameter(); });
  $("#" + rangeName + "_duration").change(function () { updateParameter(); });
  $("#mindate" + groupName).on("valuechanged", function () {
    if (!_updating)
      updateParameter();
  });
  $("#maxdate" + groupName).on("valuechanged", function () {
    if (!_updating)
      updateParameter();
  });
}