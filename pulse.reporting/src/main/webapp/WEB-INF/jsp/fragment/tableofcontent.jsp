<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>

<c:choose>
  <c:when test="${emptytoc}">
    <div>Table of content not provided.</div>
  </c:when>
  <c:when test="${!emptytoc}">
    <ul>
      <c:forEach var="toc" items="${toclist}">
        <c:set var="toclist" value="${toc.children}" scope="request" />
        <c:set var="childcount" value="${fn:length(toc.children)}" scope="page" />
        <c:choose>
          <c:when test="${childcount == 0}">
            <li id="${toc.pageNumber}" data="bookmark:'${toc.bookmark}'">
              <span class='toctext'>
                <span class='page_number'>${toc.pageNumber}</span>
                <c:choose>
                  <c:when test="${empty toc.displayText}">
                    <i>empty</i>
                  </c:when>
                  <c:otherwise>
                    ${toc.displayText}
                  </c:otherwise>
                </c:choose>
              </span>
            </li>
          </c:when>
          <c:when test="${childcount > 0}">
            <li class="folder" id="${toc.pageNumber}" data="bookmark:'${toc.bookmark}'">
              <span class='tocexpandable tocexpanded'></span>
              <span class='toctext'>
                <span class='page_number'>${toc.pageNumber}</span>
                <c:choose>
                  <c:when test="${empty toc.displayText}">
                    <i>empty</i>
                  </c:when>
                  <c:otherwise>
                    ${toc.displayText}
                  </c:otherwise>
                </c:choose>
              </span> 
              <jsp:include page="tableofcontent.jsp" />
            </li>
          </c:when>
        </c:choose>
      </c:forEach>
    </ul>
  </c:when>
</c:choose>