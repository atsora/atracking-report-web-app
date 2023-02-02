<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<input id="fmt_print" type="hidden" value="<spring:message code='print.report' />"/>
<div id="printdialogbox">
  <div>
	  <spring:message code="format" />&nbsp;:&nbsp;&nbsp;
	  <input type="radio" name="printformat" value="PDF" checked="checked" id="printformatPdf"/><label for="printformatPdf">PDF</label>&nbsp;&nbsp;&nbsp;
	  <input type="radio" name="printformat" value="HTML" id="printformatHtml"/><label for="printformatHtml">HTML</label>
  </div>
  <div>
	  <input type="radio" name="printpage" value="ALL" checked="checked" id="printpageAll"/><label for="printpageAll"><spring:message code='allpages'/></label> &nbsp;&nbsp;&nbsp;
	  <input type="radio" name="printpage" value="CURRENT" id="printpageCurrent"/><label for="printpageCurrent"><spring:message code='currentpage'/></label>&nbsp;&nbsp;&nbsp;
	  <input type="radio" name="printpage" value="RANGE" id="printpageRange"/><label for="printpageRange"><spring:message code='pages'/></label> 
	  &nbsp;:&nbsp;<input type="text" name="printpagerange" id="printpagerange" value="" size="6" />
  </div>
  <div>
    <spring:message code="pagerange.msg" />
  </div>
</div>