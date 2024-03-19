<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<input id="basedirectory" type="hidden" value="${reportTree.baseDir}" />
<div id="reportdescription" class="tile-container">
  <div class="tile">
    <div class="tile-title title" id="report-title"></div>
    <div class="tile-content">
      <div class="report-description">
        <x-markdowntext class="report-description-details"></x-markdowntext>
      </div>
      <img id="thumbnail" alt="" src="" />
      <input type="hidden" id="path" name="path" value=""/>
    </div>
  </div>
</div>
<!--spring:message code="comment" /-->
<!--spring:message code='reporttemplatelist'/-->