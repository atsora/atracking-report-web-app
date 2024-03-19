<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<input id="fmt_export" type="hidden" value="<spring:message code='export.report' />"/>
<input id="fmt_pagerange.msg" type="hidden" value="<spring:message code='pagerange.msg' />"/>
<input id="fmt_validate" type="hidden" value="<spring:message code='validate' />"/>
<div id="exportdialogbox">
  <div>
    <spring:message code="format" />&nbsp;:&nbsp;&nbsp;
    <select id="exportformat" style="vertical-align:middle;">
      <c:forEach var="entry" items="${availableFormat}">
        <option value="${entry.key}" <c:if test="${entry.key == 'pdf'}">selected='selected'</c:if> >${entry.value}</option>
      </c:forEach>
    </select>    
  </div>
  <div>
    <input type="radio" name="exportpage" value="ALL" checked="checked" id="exportpageAll"/><label for="exportpageAll"><spring:message code='allpages'/></label>&nbsp;&nbsp;&nbsp;
    <input type="radio" name="exportpage" value="CURRENT" id="exportpageCurrent"/> <label for="exportpageCurrent"><spring:message code='currentpage'/></label>&nbsp;&nbsp;&nbsp;
    <input type="radio" name="exportpage" value="RANGE" id="exportpageRange"/> <label for="exportpageRange"><spring:message code='pages'/></label> 
    &nbsp;:&nbsp;<input type="text" name="exportpagerange" id="exportpagerange" value="" size="6" />
  </div>
  <div>
    <spring:message code="pagerange.msg" />
  </div>
  <div id="excelexportimagewarning" style="font-size:0.8em; color:red">
    <spring:message code="excelexportimagewarning"/>
  </div>
</div>