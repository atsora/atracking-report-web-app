<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.lemoinetechnologies.pulse.reporting.domain.GroupParameter"%>
<%@ page import="com.lemoinetechnologies.pulse.reporting.domain.ScalarParameter"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.apache.logging.log4j.LogManager"%>
<%@ page import="org.apache.logging.log4j.Logger"%>


<input type="hidden" id="groupname" value="${groupParameter.name}" />
<input type="hidden" id="displayform" value="${groupParameter.displayForm}" />
<input type="hidden" id="helptext" value="${groupParameter.helpText}" />
<% 
  Logger logger = LogManager.getLogger(GroupParameter.class);
  GroupParameter groupParameter = ((GroupParameter)request.getAttribute("groupParameter"));
  ArrayList<ScalarParameter> scalarParameters = new ArrayList<ScalarParameter>(groupParameter.getScalarParameterMap().values());
  pageContext.setAttribute("scalarParameters", scalarParameters);
%>

<c:forEach var="entry" items="${scalarParameters}">
  <c:set var="scalarParameter" value="${entry}" scope="page" />
  <c:if test="${entry.hidden}">
    <div class="parameter" style="display:none">
      <%@ include file="hiddenParameter.jsp"%>
    </div>
  </c:if>
  <c:if test="${!entry.hidden}">
    <%-- scriplet used to find which widget to build in order to represents current scalar parameter  --%>
    <%
      String widget = ((ScalarParameter) pageContext.getAttribute("entry")).determineWidget();
      pageContext.setAttribute("widget", widget);
    %>
    <div class="parameter">
      <div class="subTitle">${scalarParameter.promptText}</div>
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