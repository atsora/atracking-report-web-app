<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="com.lemoinetechnologies.pulse.reporting.domain.ScalarParameter"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.HashSet"%>
<%@ page import="java.util.ArrayList"%>

<%
  ScalarParameter parameter = ((ScalarParameter) pageContext.getAttribute("scalarParameter"));
  ArrayList<String> selectionKeySet = new ArrayList<String>(parameter.getSelectionList().keySet());
  pageContext.setAttribute("selectionKeySet", selectionKeySet);

  int size;
  if (selectionKeySet.size() <= 5) {
    size = selectionKeySet.size();
  } else if (selectionKeySet.size() > 5 && selectionKeySet.size() <= 8) {
    size = 5;
  } else {
    size = 8;
  }
  
  pageContext.setAttribute("size", size);
  pageContext.setAttribute("isrequired", parameter.isRequired());
  pageContext.setAttribute("defaultvalue", parameter.getDefaultValue());
  pageContext.setAttribute("currentvalue", parameter.getValue());
  pageContext.setAttribute("allowMultipleValues", parameter.isMultiSelect());
  pageContext.setAttribute("hasdefaultvalue", parameter.hasdefaultvalue());
  pageContext.setAttribute("defaultvalueStr", java.util.Arrays.toString(parameter.getDefaultValue()));
  pageContext.setAttribute("currentvalueStr", java.util.Arrays.toString(parameter.getValue()));
%>

<input type="hidden" id="widget" value="LISTBOX" />
<input type="hidden" id="name" value="${scalarParameter.name}" />
<input type="hidden" class="${scalarParameter.name}_default" id="defaultvalue" value="${defaultvalueStr}" />
<input type="hidden" id="pvalue" value="${currentvalueStr}" />
<input type="hidden" id="datatype" value="${scalarParameter.dataType}" />
<input type="hidden" id="parametertype" value="${scalarParameter.parameterType}" />
<input type="hidden" id="required" value="${scalarParameter.required}" />
<input type="hidden" id="helptext" value="${scalarParameter.helpText}" />
<input type="hidden" id="hidden" value="${scalarParameter.hidden}" />

<!-- Kind of select: can be required or not, possibly allow a multiple selection -->
<c:if test="${isrequired}">
  <c:if test="${allowMultipleValues}">
    <select size='${size}' multiple="multiple" class="${scalarParameter.name}_input chosen_select input_required">
  </c:if>
  <c:if test="${!allowMultipleValues}">
    <select size='${size}' class="${scalarParameter.name}_input chosen_select input_required">
  </c:if>
</c:if>
<c:if test="${!isrequired}">
  <c:if test="${allowMultipleValues}">
    <select size='${size}' multiple="multiple" class="${scalarParameter.name}_input chosen_select">
  </c:if>
  <c:if test="${!allowMultipleValues}">
    <select size='${size}' class="${scalarParameter.name}_input chosen_select">
    <!--- Use default / No value - __NULL --->
    <c:if test="${!hasdefaultvalue}">     
      <option selected="selected" value="__NULL"><spring:message code="novalue" htmlEscape="true"/></option>
    </c:if>
    <c:if test="${hasdefaultvalue}">
      <option selected="selected" value="__NULL"><spring:message code="usedefault" htmlEscape="true"/></option>
    </c:if>
  </c:if>
</c:if>

<!-- Add the options -->
<c:forEach var="item" items="${selectionKeySet}">
  <c:set var="isSelected" value="false" />
  <c:forEach var="val" items="${currentvalue}">
    <c:if test="${item == val}">
      <c:set var="isSelected" value="true" />
    </c:if>
  </c:forEach>
  <c:set var="isDefault" value="false" />
  <c:forEach var="val" items="${defaultvalue}">
    <c:if test="${item == val}">
      <c:set var="isDefault" value="true" />
    </c:if>
  </c:forEach>
  
  <c:if test="${isSelected}">
    <c:if test="${isDefault}">
      <option selected="selected" isdefault="true" value="${item}">${scalarParameter.selectionList[item]}</option>
    </c:if>
    <c:if test="${!isDefault}">
      <option selected="selected" value="${item}">${scalarParameter.selectionList[item]}</option>
    </c:if>
  </c:if>
  <c:if test="${!isSelected}">
    <c:if test="${isDefault}">
      <option isdefault="true" value="${item}">${scalarParameter.selectionList[item]}</option>
    </c:if>
    <c:if test="${!isDefault}">
      <option value="${item}">${scalarParameter.selectionList[item]}</option>
    </c:if>
  </c:if>
</c:forEach>

</select>

<c:if test="${allowMultipleValues}">
<script type="text/javascript">
$(function()
{
  // Initialize the order of the selected options
  let orderOptions = function(orderedOptionValues)
  {
    // Add the value "-1" at the beginning if it's not in the values
    if (!orderedOptionValues.includes('-1'))
      orderedOptionValues.unshift('-1');
    
    // Take one by one the selected <option> in the reverse order and place them on the top of the <select>
    for (let i = orderedOptionValues.length - 1; i >= 0; i--)
    {
      let select = document.querySelector('.${scalarParameter.name}_input');
      let option = select.querySelector('option[value="' + orderedOptionValues[i].trim() + '"]');
      if (option != null) {
        select.removeChild(option);
        select.insertBefore(option, select.firstChild);
      }
    }
  };
  orderOptions($('.${scalarParameter.name}_default').val().replace('[', '').replace(']', '').split(','));
  
  // Return the current selected values
  let getOptionValues = function(optionKind)
  {
    let values = [];
    let selectedOptions = $('.${scalarParameter.name}_input').find(optionKind);
    selectedOptions.each(function(){values.push($(this).val());});
    return values;
  }

  // Initialization: check if "all" is selected
  let previousStateWithAll = getOptionValues('option:selected').includes('-1');
  
  // Default values
  let defaultValues = getOptionValues('option[isdefault="true"]');
  
  $('.${scalarParameter.name}_input').chosen().change(function()
  {
    // New state of the selection
    let currentValues = getOptionValues('option:selected');
    let withAllSelected = currentValues.includes('-1');
    let withSomethingElseThanAllSelected = withAllSelected ? (currentValues.length > 1) : (currentValues.length > 0);
    if (!withAllSelected && !withSomethingElseThanAllSelected)
    {
      // Back to default
      withAllSelected = false;
      defaultValues.forEach(function(item) {
        if (item == "-1")
          withAllSelected = true;
        $('.${scalarParameter.name}_input option[value="' + item + '"]')[0].selected = true;
      });
    }
    else if (withAllSelected && withSomethingElseThanAllSelected)
    {
      if (previousStateWithAll)
      {
        // Remove "All"
        $('.${scalarParameter.name}_input option[value="-1"]')[0].selected = false;
        withAllSelected = false;
      }
      else
      {
        // Remove everyting but "All"
        currentValues.forEach(function(item) {
          if (item != '-1')
            $('.${scalarParameter.name}_input option[value="' + item + '"]')[0].selected = false;
        });
      }
    }
    
    // New state of the "All" value
    previousStateWithAll = withAllSelected;
    
    // Reorder the options and update the widget "chosen"
    orderOptions($('.${scalarParameter.name}_input').val());
    $('.${scalarParameter.name}_input').trigger('chosen:updated');
  });
});
</script>
</c:if>