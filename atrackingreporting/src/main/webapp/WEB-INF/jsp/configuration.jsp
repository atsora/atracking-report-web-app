<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>

<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Configuration</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/style.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/jqueryui/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/configuration.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/dynatree/ui.dynatree.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery/jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqueryui/jquery-ui.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqueryui/jquery.dynatree.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqueryui/timepickeraddon/jquery-ui-sliderAccess.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jqueryui/timepickeraddon/jquery-ui-timepicker-addon.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/common.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/configuration.js"></script>

</head>
<body>
	<!--input type="hidden" value="${configuration.WEB_SERVICE_PATH}" name="webservicepath" id="webservicepath"/-->
	<div id="header">
		<div id="headermenu">
			<button id="listreportbtn" title="<spring:message code='viewlistofreporttemplates'/>">View report template list</button>
		</div>
	</div>
	<div id="inner">
		<div>
			<table>
				<tr>
					<td>
						<fieldset id="applicationparameters">
							<legend>
								<spring:message code="applicationparameters" />
							</legend>
							<table>
								<tr>
									<td class="rightalign"><spring:message code="birt_locale" /></td>
									<td><input type="hidden" name="birtlocale" value="${configuration.BIRT_LOCALE}" /> <select id="birtlocale">
											<c:if test="${configuration.BIRT_LOCALE == 'en'}">
												<option value="en" selected="selected">
													<spring:message code="english" />
												</option>
											</c:if>
											<c:if test="${!(configuration.BIRT_LOCALE == 'en')}">
												<option value="en">
													<spring:message code="english" />
												</option>
											</c:if>
											<c:if test="${configuration.BIRT_LOCALE == 'fr'}">
												<option value="fr" selected="selected">
													<spring:message code="french" />
												</option>
											</c:if>
											<c:if test="${!(configuration.BIRT_LOCALE == 'fr')}">
												<option value="fr">
													<spring:message code="french" />
												</option>
											</c:if>
											<c:if test="${configuration.BIRT_LOCALE == 'de'}">
												<option value="de" selected="selected">
													<spring:message code="german" />
												</option>
											</c:if>
											<c:if test="${!(configuration.BIRT_LOCALE == 'de')}">
												<option value="de">
													<spring:message code="german" />
												</option>
											</c:if>
									</select></td>
								</tr>
								<tr>
									<td class="rightalign"><spring:message code="birt_log_level" /></td>
									<td><input type="hidden" name="birtloglevel" value="${configuration.BIRT_LOG_LEVEL}" /> <select id="birtloglevel">
											<c:if test="${configuration.BIRT_LOG_LEVEL == 'OFF'}">
												<option value="OFF" selected>OFF</option>
											</c:if>
											<c:if test="${!(configuration.BIRT_LOG_LEVEL == 'OFF')}">
												<option value="OFF">OFF</option>
											</c:if>
											<c:if test="${configuration.BIRT_LOG_LEVEL == 'SEVERE'}">
												<option value="SEVERE" selected>SEVERE</option>
											</c:if>
											<c:if test="${!(configuration.BIRT_LOG_LEVEL == 'SEVERE')}">
												<option value="SEVERE">SEVERE</option>
											</c:if>
											<c:if test="${configuration.BIRT_LOG_LEVEL == 'WARNING'}">
												<option value="WARNING" selected>WARNING</option>
											</c:if>
											<c:if test="${!(configuration.BIRT_LOG_LEVEL == 'WARNING')}">
												<option value="WARNING">WARNING</option>
											</c:if>
											<c:if test="${configuration.BIRT_LOG_LEVEL == 'INFO'}">
												<option value="INFO" selected>INFO</option>
											</c:if>
											<c:if test="${!(configuration.BIRT_LOG_LEVEL == 'INFO')}">
												<option value="INFO">INFO</option>
											</c:if>
											<c:if test="${configuration.BIRT_LOG_LEVEL == 'CONFIG'}">
												<option value="CONFIG" selected>CONFIG</option>
											</c:if>
											<c:if test="${!(configuration.BIRT_LOG_LEVEL == 'CONFIG')}">
												<option value="CONFIG">CONFIG</option>
											</c:if>
											<c:if test="${configuration.BIRT_LOG_LEVEL == 'FINE'}">
												<option value="FINE" selected>FINE</option>
											</c:if>
											<c:if test="${!(configuration.BIRT_LOG_LEVEL == 'FINE')}">
												<option value="FINE">FINE</option>
											</c:if>
											<c:if test="${configuration.BIRT_LOG_LEVEL == 'FINER'}">
												<option value="FINER" selected>FINER</option>
											</c:if>
											<c:if test="${!(configuration.BIRT_LOG_LEVEL == 'FINER')}">
												<option value="FINER">FINER</option>
											</c:if>
											<c:if test="${configuration.BIRT_LOG_LEVEL == 'FINEST'}">
												<option value="FINEST" selected>FINEST</option>
											</c:if>
											<c:if test="${!(configuration.BIRT_LOG_LEVEL == 'FINEST')}">
												<option value="FINEST">FINEST</option>
											</c:if>
											<c:if test="${configuration.BIRT_LOG_LEVEL == 'ALL'}">
												<option value="ALL" selected>ALL</option>
											</c:if>
											<c:if test="${!(configuration.BIRT_LOG_LEVEL == 'ALL')}">
												<option value="ALL">ALL</option>
											</c:if>
									</select></td>
								</tr>
								<tr>
									<td class="rightalign"><spring:message code="birt_log_rolling_size" /></td>
									<td><input type="text" name="birtlogrollingsize" id="birtlogrollingsize" value="${configuration.BIRT_LOG_ROLLING_SIZE/(1024*1024)}" />&nbsp;<spring:message
											code="megabyte"
										/></td>
								</tr>
								<tr>
									<td class="rightalign"><spring:message code="birt_log_max_backup_index" /></td>
									<td><input type="text" name="birtlogmaxbackupindex" id="birtlogmaxbackupindex" value="${configuration.BIRT_LOG_MAX_BACKUP_INDEX}" /></td>
								</tr>
								<tr>
									<td class="rightalign"><spring:message code="viewing_session_timeout" /></td>
									<td><input type="text" name="viewingsessiontimeout" id="viewingsessiontimeout" value="${configuration.SESSION_TIMEOUT/60000}" />&nbsp;<spring:message code="minute_s" /></td>
								</tr>
								<tr>
									<td class="rightalign"><spring:message code="reporttemplate_directory" /></td>
									<td><input type="text" name="reporttemplatedirectory" id="reporttemplatedirectory" value="${configuration.DESIGN_FOLDER}" readonly="readonly" />
									&nbsp;&nbsp;&nbsp;<button class="buttonpointer" id="reporttemplatedirectorybtn" title="<spring:message code='selectafolder'/>">...</button></td>
								</tr>
								<tr>
									<td class="rightalign"><spring:message code="birt_resource_directory" /></td>
									<td><input type="text" name="birtresourcedirectory" id="birtresourcedirectory" value="${configuration.BIRT_RESOURCE_PATH}" readonly="readonly" />
										&nbsp;&nbsp;&nbsp;<button class="buttonpointer" id="birtresourcedirectorybtn" title="<spring:message code='selectafolder'/>">...</button></td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="rightalign">
						<button id="resetbtn" title="<spring:message code='reset'/>">
							<spring:message code="reset" />
						</button>
						<button id="validatebtn" title="<spring:message code='validate'/>">
							<spring:message code="validate" />
						</button>
					</td>
				</tr>
			</table>
		</div>
	</div>
</body>
</html>