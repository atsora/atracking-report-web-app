<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<input type="hidden" id="widget" value="RADIOBTN" />
<input type="hidden" id="name" value="${scalarParameter.name}" />
<c:choose>
  <c:when test="${empty scalarParameter.defaultValue}">
    <input type="hidden" id="defaultvalue" value="" />
  </c:when>
  <c:otherwise>
    <input type="hidden" id="defaultvalue" value="${scalarParameter.defaultValue[0]}" />
  </c:otherwise>
</c:choose>
<input type="hidden" id="datatype" value="${scalarParameter.dataType}" />
<input type="hidden" id="parametertype" value="${scalarParameter.parameterType}" />
<input type="hidden" id="required" value="${scalarParameter.required}" />
<input type="hidden" id="helptext" value="${scalarParameter.helpText}" />
<input type="hidden" id="hidden" value="${scalarParameter.hidden}" />

<c:choose>
  <c:when test="${empty scalarParameter.value}">
    <input type="radio" name="${scalarParameter.name}_value" value="true" /><spring:message code='yes' />
    &nbsp;&nbsp;&nbsp;
    <input type="radio" name="${scalarParameter.name}_value" value="false" /><spring:message code="no" />
  </c:when>
  <c:when test="${scalarParameter.value[0]}">
    <input type="radio" name="${scalarParameter.name}_value" checked="checked" value="true" /><spring:message code="yes" />
    &nbsp;&nbsp;&nbsp;
    <input type="radio" name="${scalarParameter.name}_value" value="false" /><spring:message code="no" />
  </c:when>
  <c:otherwise>
    <input type="radio" name="${scalarParameter.name}_value" value="true" /><spring:message code="yes" />
    &nbsp;&nbsp;&nbsp;
    <input type="radio" name="${scalarParameter.name}_value" checked="checked" value="false" /><spring:message code="no" />
  </c:otherwise>
</c:choose>