<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="f" uri="http://example.com/functions" %>
<%@ page import="org.springframework.web.servlet.support.RequestContextUtils"%>
<%@ page import="org.springframework.web.servlet.LocaleResolver"%>
<%@ page import="java.util.Locale"%>
<%@ page import="java.util.Enumeration"%>
<%@ page isELIgnored="false"%>

<%@ page import="eu.atsora.tracking.reporting.birt.Configuration"%>
<%
  pageContext.setAttribute("WEB_SERVICE_PATH", Configuration.WEB_SERVICE_PATH);
  pageContext.setAttribute("WEB_SERVICE_TIMEOUT_MS", Configuration.WEB_SERVICE_TIMEOUT_MS);
%>

<!DOCTYPE>
<html>

<head>
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta name="viewport" content="width=device-width, user-scalable=no">
  <title>Reports - Atsora Tracking</title>

  <link rel="shortcut icon" href="<%=request.getContextPath()%>/images/favicon.ico" type="image/x-icon">
  <link rel="icon" type="image/gif" href="<%=request.getContextPath()%>/images/favicon.ico">
  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/chosen.css" />
  <link rel="stylesheet" type="text/css"
    href="<%=request.getContextPath()%>/css/customwidgets.css?v=${f:getAutoVersioningSuffix()}" />
  <link rel="stylesheet" type="text/css"
    href="<%=request.getContextPath()%>/css/style.css?v=${f:getAutoVersioningSuffix()}" />
  <link rel="stylesheet" type="text/css"
    href="<%=request.getContextPath()%>/css/listreport.css?v=${f:getAutoVersioningSuffix()}" />
  <link rel="stylesheet" type="text/css"
    href="<%=request.getContextPath()%>/css/style_light/pulse.exports.light.css?v=${f:getAutoVersioningSuffix()}">

  <script type="text/javascript" src="<%=request.getContextPath()%>/js/moment.js"></script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery/jquery.js"></script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery/jquery.timer.js"></script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery/chosen.jquery.js"></script>
  
  <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/config_pulsecomponent_default.js?v=${f:getAutoVersioningSuffix()}"></script>
  <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/config_reportwebapp.js?v=${f:getAutoVersioningSuffix()}"></script>
  <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/config_install.js?v=${f:getAutoVersioningSuffix()}"></script>
  <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/config_custom.js?v=${f:getAutoVersioningSuffix()}"></script>
  <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/translation_pulsecomponent_default.js?v=${f:getAutoVersioningSuffix()}"></script>
  <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/pulse.exports.light.js?v=${f:getAutoVersioningSuffix()}"></script>
  <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/reporttemplate.js?v=${f:getAutoVersioningSuffix()}"></script>
 <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/common.js?v=${f:getAutoVersioningSuffix()}"></script>
  <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/customwidgets.js?v=${f:getAutoVersioningSuffix()}"></script>
  <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/machineselection.js?v=${f:getAutoVersioningSuffix()}"></script>
  <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/accordionselection.js?v=${f:getAutoVersioningSuffix()}"></script>
  <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/listreport.js?v=${f:getAutoVersioningSuffix()}"></script>
</head>

<body>
  <x-message></x-message>
  <x-checkserveraccess></x-checkserveraccess>
  <x-checkpath></x-checkpath>
  <!--x-checkcurrenttime></x-checkcurrenttime-->
  <!--x-checkversion></x-checkversion TO ADD SOON-->
  <!--x-checkconfigupdate></x-checkconfigupdate-->
  <x-checklogin></x-checklogin>

  <!--input type="hidden" value="${WEB_SERVICE_PATH}" name="webservicepath" id="webservicepath" /-->
  <input type="hidden" value="${WEB_SERVICE_TIMEOUT_MS}" name="webservicetimeout" id="webservicetimeout" />
  <div id="header">
    <div id="header-right">
      <div id="header-menu">
        <button id="actionbtn" class="button2 dropdown-button" title="actions" role="button" aria-disabled="false">
          <span class="button2-icon"></span>
        </button>
        <div id="header-menu-dropdown" class="dropdown-content">
          <div id="configurationbtn" class="header-menu-item">Configuration</div>
        </div>
      </div>
    </div>
    <div class="header-close-to-right">
      <div id="help-div">
        <button id="help-icon" class="button2" title="help" role="button" aria-disabled="false">
        </button>
      </div>
    </div>
    <div class="header-close-to-right-login">
      <x-logindisplay donotuseinline='true'></x-logindisplay>
    </div>
    <div id="header-left">
      <button id="menubtn" class="button2" title="menu" role="button" aria-disabled="false">
        <span class="button2-icon"></span>
      </button>
    </div>
    <div id="header-center">
      <a href="listreport">
        <div id="header-title" class="title">
          <div id="header-logo"></div>Atsora Tracking Reports
        </div>
      </a>
    </div>
  </div>

  <div id="inner">
    <div id="leftpanel">
      <div id="left-actions">
        <div id="left-action-list"></div>
        <div id="left-action-favorites"></div>
      </div>
      <div id="tree">
        <c:set var="context" value="${pageContext.request.contextPath}" scope="request" />
        <c:set var="rootchildren" value="${fn:length(reportTree.root.children)}" scope="page" />
        <c:if test="${rootchildren == 0}">
          <div>
            <spring:message code='directorycontainsnoreporttemplate' />
          </div>
        </c:if>
        <c:if test="${rootchildren != 0}">
          <c:set var="nodelist" value="${reportTree.root.children}" scope="request" />
          <jsp:include page="fragment/reporttree.jsp" />
        </c:if>
      </div>
      <div id="left-favorites"></div>
    </div>
    <div id="mainarea">
      <div id="mainarea-inner">
        <%@ include file="fragment/reportfavorites.jsp"%>
        <%@ include file="fragment/reportdescription.jsp"%>
        <%@ include file="fragment/reportparameters.jsp"%>
      </div>
    </div>
  </div>
  <%@ include file="fragment/addfavoritedialogbox.jsp"%>

  <div id="configurationpanel">
    <div class="header-menu-switch">
      <label class="switch">
        <input type="checkbox" id="darkthemebtn">
        <div class="slider round"></div>
      </label>
      <span>Dark theme</span>
    </div>
    <div class="header-menu-switch">
      <label class="switch">
        <input type="checkbox" id="newtabbtn">
        <div class="slider round"></div>
      </label>
      <span>Open report in a new tab</span>
    </div>
  </div>
</body>

</html>