<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<input id="updateparameters" type="hidden" value="<spring:message code='updateparameters' />"/>
<div id="parametersdialogbox">
	<div id="parameterslist">
    <table cellspacing="0" cellpadding="0" border="0">
      <%@ include file="parameterUI/parametersPanel.jsp"%>
    </table> 
	</div>
</div>