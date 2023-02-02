<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<input id="fmt_slideshow" type="hidden" value="<spring:message code='slideshow.report' />"/>
<div id="slideshowdialogbox">
  <div>
	  <spring:message code="delayoftransition"/>&nbsp;:&nbsp;&nbsp;
    <p>
      <input type="radio" name="delayoftransition" value="slow">Slow<br>
      <input type="radio" name="delayoftransition" value="normal" checked>Normal<br>
      <input type="radio" name="delayoftransition" value="fast">Fast
    </p>
  </div>
  <div>
	  <spring:message code="delayofrefresh"/>&nbsp;:&nbsp;&nbsp;
	  <input type="text" size="4" value="5" style='text-align:right' id="delayofrefresh"/>
  </div>
</div>