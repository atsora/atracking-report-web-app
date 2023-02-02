<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="f" uri="http://example.com/functions" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>

<%@ page import="com.lemoinetechnologies.pulse.reporting.birt.Configuration"%>
<%
  pageContext.setAttribute("WEB_SERVICE_PATH", Configuration.WEB_SERVICE_PATH);
  pageContext.setAttribute("WEB_SERVICE_TIMEOUT_MS", Configuration.WEB_SERVICE_TIMEOUT_MS);
%>

<!DOCTYPE html>
<html>

<head>
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta http-Equiv="Cache-Control" Content="no-cache">
  <meta http-Equiv="Pragma" Content="no-cache">
  <meta http-Equiv="Expires" Content="0">
  <meta name="viewport" content="width=device-width, user-scalable=no">
  <title></title>
  <link rel="shortcut icon" href="<%=request.getContextPath()%>/images/favicon.ico" type="image/x-icon">
  <link rel="icon" type="image/gif" href="<%=request.getContextPath()%>/images/favicon.ico">
  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/chosen.css" />
  <link rel="stylesheet" type="text/css"
    href="<%=request.getContextPath()%>/css/customwidgets.css?v=${f:getAutoVersioningSuffix()}" />
  <!-- Done in common.js 
    link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/pulse.exports.light.css?v=${f:getAutoVersioningSuffix()}" /-->
  <link rel="stylesheet" type="text/css"
    href="<%=request.getContextPath()%>/css/style.css?v=${f:getAutoVersioningSuffix()}" />
  <link rel="stylesheet" type="text/css"
    href="<%=request.getContextPath()%>/css/viewer.css?v=${f:getAutoVersioningSuffix()}" />

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
    src="<%=request.getContextPath()%>/js/viewer.js?v=${f:getAutoVersioningSuffix()}"></script>
</head>

<body>
  <x-message></x-message>
  <x-checkserveraccess></x-checkserveraccess>
  <x-checkpath></x-checkpath>
  <!--x-checkcurrenttime></x-checkcurrenttime-->
  <!--x-checkversion></x-checkversion TO ADD SOON-->
  <!--x-checkconfigupdate></x-checkconfigupdate-->
  <x-checklogin></x-checklogin>
  
  <input type="hidden" value="" name="viewingsession" id="viewingsession" />
  <input type="hidden" value="" name="reportid" id="reportid" />
  <input type="hidden" value="" name="querystring" id="querystring" />
  <!--input type="hidden" value="${WEB_SERVICE_PATH}" name="webservicepath" id="webservicepath" /-->
  <input type="hidden" value="${WEB_SERVICE_TIMEOUT_MS}" name="webservicetimeout" id="webservicetimeout" />

  <div id="header">
    <div id="header-right">
      <div class="header-actions">
        <button id="parameterbtn" class="button2" title="Change parameters" role="button" aria-disabled="false">
          <span class="button2-icon"></span>
        </button>
        <button id="exportbtn" class="button2" title="Save the report in a file (pdf, excel, ...)" role="button"
          aria-disabled="false">
          <span class="button2-icon"></span>
        </button>
        <button id="printbtn" class="button2" title="Print the report" role="button" aria-disabled="false">
          <span class="button2-icon"></span>
        </button>
        <button id="slideshowbtn" class="button2" title="Start slideshow" role="button" aria-disabled="false">
          <span class="button2-icon"></span>
        </button>
      </div>
      <div id="header-menu">
        <button id="actionbtn" class="button2 dropdown-button" title="actions" role="button" aria-disabled="false">
          <span class="button2-icon"></span>
        </button>
        <div id="header-menu-dropdown" class="dropdown-content">
          <div class="header-menu-item" id="parameterbtn2" title="Change parameters">
            Change parameters
          </div>
          <div class="header-menu-item" id="exportbtn2" title="Save the report in a file (pdf, excel, ...)">
            Export report
          </div>
          <div class="header-menu-item" id="printbtn2" title="Print the report">
            Print report
          </div>
          <div class="header-menu-separator"></div>
          <div class="header-menu-item" id="slideshowbtn2">
            Start slideshow
          </div>
        </div>
      </div>
    </div>
    <div id="header-left">
      <button id="menubtn" class="button2" title="table of content" role="button" aria-disabled="false">
        <span class="button2-icon"></span>
      </button>
      <button id="homebtn" class="button2" title="home" role="button" aria-disabled="false">
        <span class="button2-icon"></span>
      </button>
    </div>
    <div id="header-center">
      <div id="header-title" class="title">PULSE REPORTS</div>
    </div>
  </div>

  <div id="inner">
    <div id="leftpanel">
      <div id="tableofcontenttitle">
        <spring:message code="tableofcontent" />
      </div>
      <div id="tableofcontenttree">

      </div>
    </div>
    <div id="mainarea">
      <div id="mainarea-inner">
        <div id="report-container" class="tile-container">
          <div id="report-content" class="tile">
          </div>
        </div>
      </div>
      <div id="stop-slideshow">
        <button id="stop-slideshowbtn" class="button2" title="<spring:message code='stopslideshowbtntitle'/>"
          role="button" aria-disabled="false">
          <span class="button2-icon"></span>
        </button>
      </div>
      <div class="header-pagination">
        <div class="header-pagination-inner">
          <button id='firstpagebtn' class="button2" title="<spring:message code='gotofirstpage'/>" role="button"
            aria-disabled="false">
            <span class="button2-icon"></span>
          </button>
          <button id='previouspagebtn' class="button2" title="<spring:message code='gotopreviouspage'/>" role="button"
            aria-disabled="false">
            <span class="button2-icon"></span>
          </button>
          <input type="hidden" value="" name="currentpage" id="currentpage" />
          <input type="text" size="1" value='' style='text-align:right' id='pagenumber' />
          <span>&nbsp;/&nbsp;<span id='totalpage'></span></span>
          <button id='nextpagebtn' class="button2" title="<spring:message code='gotonextpage'/>" role="button"
            aria-disabled="false">
            <span class="button2-icon"></span>
          </button>
          <button id='lastpagebtn' class="button2" title="<spring:message code='gotolastpage'/>" role="button"
            aria-disabled="false">
            <span class="button2-icon"></span>
          </button>
        </div>
      </div>
      <div class="slideshow-warning">
        <div class="slideshow-warning-inner">
          <div class="slideshow-warning-image"></div>
          <div class="slideshow-warning-view-details">View details</div>
        </div>
      </div>
    </div>
  </div>

  <%@ include file="fragment/printdialogbox.jsp"%>
  <%@ include file="fragment/exportdialogbox.jsp"%>
  <%@ include file="fragment/slideshowdialogbox.jsp"%>
  <div id="parameterdialogboxcontainer"></div>
</body>

</html>