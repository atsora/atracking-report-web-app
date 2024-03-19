<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:if test="${! empty error}">
  <div id="errordialogbox">
    <span style="float: left; margin-right: .3em;"> <img
      src="<%=request.getContextPath()%>/images/exception.png"
      alt="exception image" />
    </span>
    <pre>${error}</pre>
  </div>
  <script type="text/javascript">
    // Error dialog box
    $('#errordialogbox').dialog({
      closeOnEscape : false,
      autoOpen: false,
      modal: true,
      title: "<spring:message code='anexceptionoccurred'/>",
      width: 900,
      height: 400,
      buttons: {
        "<spring:message code='returntothelistofreporttemplates'/>": function() {
          var locale = getUrlParameterValue('locale');
          if(null == locale){
            document.location.pathname = '${pageContext.request.contextPath}/listreport';
          }
          else {
            document.location.pathname = '${pageContext.request.contextPath}/listreport?locale='+locale;
          }
        }
      }
    });
    $('#errordialogbox').dialog('open');
  </script>
</c:if>
<c:if test="${empty error}">
  <div id="reportparameters" class="tile-container">
    <div class="tile">
      <div class="tile-title title"><spring:message code='settingreportparameters' /></div>
      <div class="tile-content">
        <!--%@ include file="../parameterUI/parametersPanel.jsp"%-->
      </div>
    </div>
  </div>
  <div id="reportactions" class="tile-container">
    <div class="tile">
      <div class="tile-title title"><spring:message code='actions' /></div>
      <div class="tile-content">
        <button id="addfavoritebtn" class="button1" title="<spring:message code='addfavoritebtntitle' />" role="button" aria-disabled="false">
          <span class="button1-text">
            <spring:message code="view.addfavorite" />
            <span class="button1-icon"></span>
          </span>
        </button>
        <button id="updatefavoritebtn" class="button1" title="<spring:message code='updatefavoritebtntitle' />" role="button" aria-disabled="false">
          <span class="button1-text">
            <spring:message code="view.updatefavorite" />
            <span class="button1-icon"></span>
          </span>
        </button>
        <button id="viewbtn" class="button1" title="<spring:message code='viewbtntitle' />" role="button" aria-disabled="false">
          <span class="button1-text">
            <spring:message code="view.report" />
            <span class="button1-icon"></span>
          </span>
        </button>
      </div>
    </div>
  </div>
</c:if>   
<input id="fmt_validate" type="hidden" value="<spring:message code="validate" />" />