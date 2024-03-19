<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<input id="fmt_addfavorite" type="hidden" value="<spring:message code='view.addfavorite' />"/>
<input id="fmt_validate" type="hidden" value="<spring:message code='validate' />"/>
<div id="addfavoritedialogbox">
  <span><spring:message code='name'/></span><input type="text" name="favoritename" id="favoritename" value=""/>
  </br></br>
  <input type="checkbox" name="rememberparameters" checked="checked" id="rememberparameters" value="rememberparameters"/><label for="rememberparameters"><spring:message code='rememberparameters'/></label>
</div>