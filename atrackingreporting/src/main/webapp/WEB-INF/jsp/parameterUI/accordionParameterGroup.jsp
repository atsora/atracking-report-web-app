<%@ include file="defaultParameterGroup.jsp"%>

<script type="text/javascript">
$(function() {
  // Hide lists
  var groupClass = "${groupParameter.name}" + "_row";
  $("." + groupClass + " .parameterGroupContent").hide();
  
  // Create a machine selection dialog
  var dataAdaptor = new AccordionDataAdaptor(groupClass);
  var uniqueSelection = ($("." + groupClass + " .parameter:nth-of-type(2) select[multiple]").length == 0);
  accordionSelection.initialize("." + groupClass, dataAdaptor, uniqueSelection);
});
</script>