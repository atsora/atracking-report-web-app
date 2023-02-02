<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ page import="com.lemoinetechnologies.pulse.reporting.domain.ScalarParameter"%>
<%@ page import="com.lemoinetechnologies.pulse.reporting.domain.GroupParameter"%>


<input type="hidden" id="reporttemplateid" value="${reportTemplate.id}" />

<!-- Build grouped parameters -->
<c:set var = "withParam" scope = "session" value = "0"/>
<c:forEach var="entry" items="${reportTemplate.groupParameters}">
  <c:set var = "withParam" scope = "session" value = "1"/>
  <c:set var="groupParameter" value="${entry}" scope="request" />
  <%
    GroupParameter group = (GroupParameter)request.getAttribute("groupParameter");
    if (group.isHidden()) {
      // The group can be explicitely flagged as hidden
      pageContext.setAttribute("isGroupHidden", true);
    } else {
      // Or maybe all the children are hidden? In that case the group is hidden too
      int counter = group.getScalarParameterMap().size();
      for (ScalarParameter scalar: group.getScalarParameterMap().values()) {
        if (scalar.isHidden()) {
          counter--;
        }
      }
      if (counter == 0) {
        pageContext.setAttribute("isGroupHidden", true);
      } else {
        pageContext.setAttribute("isGroupHidden", false);
      }
    }
  %>
  <c:if test="${isGroupHidden}">
    <div class="parameterGroup ${groupParameter.name}_row" style="display:none">
  </c:if>
  <c:if test="${!isGroupHidden}">
    <div class="parameterGroup ${groupParameter.name}_row">
  </c:if>
      <div class="mainTitleDiv">
        <div class="mainTitle">${groupParameter.promptText}</div>
        <c:if test="${groupParameter.helpText!= null && groupParameter.helpText!= ''}">
          <button title="${groupParameter.helpText}" class='helpParameter-button button2' role='button' aria-disabled='false'></button>
        </c:if>
      </div>
      <div class="parameterGroupContent">
        <c:choose>
          <c:when test="${groupParameter.displayForm == 'TREEVIEW'}">
            <%@ include file="treeviewParameterGroup.jsp" %>
          </c:when>
          <c:when test="${groupParameter.displayForm == 'TREEVIEW_BY_LEAF'}">
            <%@ include file="treeviewParameterGroup.jsp" %>
          </c:when>
          <c:when test="${groupParameter.displayForm == 'DATERANGE'}">
            <%@ include file="daterangeParameterGroup.jsp" %>
          </c:when>
          <c:when test="${groupParameter.displayForm == 'MACHINES'}">
            <%@ include file="machinesParameterGroup.jsp" %>
          </c:when>
          <c:when test="${groupParameter.displayForm == 'ACCORDION'}">
            <%@ include file="accordionParameterGroup.jsp" %>
          </c:when>
          <c:when test="${groupParameter.displayForm == 'DEFAULT'}">
            <%@ include file="defaultParameterGroup.jsp" %>
          </c:when>
        </c:choose>
      </div>
      <div class="parametergrouperrormsg"></div>
    </div>
</c:forEach>

<!-- Build single parameters -->
<c:forEach var="entry" items="${reportTemplate.scalarParameters}">
  <c:set var="scalarParameter" value="${entry}" scope="page" />
  <c:if test="${entry.hidden}">
    <div class="parameter hiddenParameter" style="display:none">
      <div class="mainTitleDiv">
        <div class="mainTitle">${scalarParameter.promptText}</div>
        <c:if test="${scalarParameter.helpText!=null && scalarParameter.helpText!=''}">
          <button title="${scalarParameter.helpText}" class='helpParameter-button button2' role='button' aria-disabled='false'></button>
        </c:if>
      </div>
      <div class="parameterContent">
        <%@ include file="hiddenParameter.jsp"%>
      </div>
    </div>
  </c:if>
  
  <c:if test="${!entry.hidden}">
    <c:set var = "withParam" scope = "session" value = "1"/>
    <%-- scriplet used to find which widget to build in order to represents current scalar parameter --%>
    <%
      String widget = ((ScalarParameter) pageContext.getAttribute("scalarParameter")).determineWidget();
      pageContext.setAttribute("widget", widget);
    %>
    <div class="parameter">
      <div class="mainTitleDiv">
        <div class="mainTitle">${scalarParameter.promptText}</div>
        <c:if test="${scalarParameter.helpText!=null && scalarParameter.helpText!=''}">
          <button title="${scalarParameter.helpText}" class='helpParameter-button button2' role='button' aria-disabled='false'></button>
        </c:if>
      </div>
      <div class="parameterContent">
        <c:choose>
          <c:when test="${widget == 'textbox'}">
            <%@ include file="textboxParameter.jsp"%>
          </c:when>
          <c:when test="${widget == 'listbox'}">
            <%@ include file="listboxParameter.jsp"%>
          </c:when>
          <c:when test="${widget == 'radiobtn'}">
            <%@ include file="radiobtnParameter.jsp"%>
          </c:when>
          <c:when test="${widget == 'datetimebox'}">
            <%@ include file="datetimeboxParameter.jsp"%>
          </c:when>
          <c:when test="${widget == 'datebox'}">
            <%@ include file="dateboxParameter.jsp"%>
          </c:when>
          <c:when test="${widget == 'timebox'}">
            <%@ include file="timeboxParameter.jsp"%>
          </c:when>
        </c:choose>
      </div>
      <div class="parametererrormsg"></div>
    </div>
  </c:if>
</c:forEach>

<c:if test="${withParam == '0'}">
  This report has no configuration.
</c:if>