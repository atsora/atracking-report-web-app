<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="eu.atsora.tracking.reporting.domain.GroupParameter"%>
<%@ page import="eu.atsora.tracking.reporting.domain.ScalarParameter"%>
<%@ page import="eu.atsora.tracking.reporting.treeview.TreeView"%>
<%@ page import="eu.atsora.tracking.reporting.util.Utils"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashSet"%>

<input type='hidden' id='groupname' value='${groupParameter.name}' />
<input type='hidden' id='displayform' value='${groupParameter.displayForm}' />
<input type="hidden" id="helptext" value="${groupParameter.helpText}" />
<c:set var='rangeV2name' value='${groupParameter.name}' scope='page' />

<c:set var='webappParameter' value='${groupParameter.scalarParameterMap["WEBAPP"]}' scope='page' />
<c:set var='minParameter' value='${groupParameter.scalarParameterMap["MINDATE"]}' scope='page' />
<c:set var='maxParameter' value='${groupParameter.scalarParameterMap["MAXDATE"]}' scope='page' />

<c:choose>
  <c:when test='${empty webappParameter.value}'>
    <x-reportdatetime id='reportdatetimegroup_${rangeV2name}' style='margin-bottom: 5px'
      groupDisplayForm='${groupParameter.displayForm}' groupName='${groupParameter.name}'
      dataType='${minParameter.dataType}'
      webapp='${webappParameter.defaultValue[0]}' webappname='${webappParameter.name}'
      mindate='${minParameter.value[0]}' mindatename='${minParameter.name}'
      maxdate='${maxParameter.value[0]}' maxdatename='${maxParameter.name}' >
    </x-reportdatetime>
  </c:when>
  <c:otherwise>
    <x-reportdatetime id='reportdatetimegroup_${rangeV2name}' style='margin-bottom: 5px'
      groupDisplayForm='${groupParameter.displayForm}' groupName='${groupParameter.name}'
      dataType='${minParameter.dataType}'
      webapp='${webappParameter.value[0]}' webappname='${webappParameter.name}'
      mindate='${minParameter.value[0]}' mindatename='${minParameter.name}'
      maxdate='${maxParameter.value[0]}' maxdatename='${maxParameter.name}' >
      </x-reportdatetime>
  </c:otherwise>
</c:choose>
