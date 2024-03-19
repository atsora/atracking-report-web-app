<%@ include file="defaultParameterGroup.jsp"%>

<script type="text/javascript">
$(function() {
  // Hide lists
  var groupClass = "${groupParameter.name}" + "_row";
  $("." + groupClass + " .parameterGroupContent").hide();
  
  // Create a machine selection dialog
  var dataAdaptor = new MachineDataAdaptor(groupClass);
  var uniqueSelection = ($("." + groupClass + " .PulseMachines_input[multiple]").length == 0);
  machineSelection.initialize("." + groupClass, dataAdaptor, uniqueSelection);
});
</script>