<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="f" uri="http://example.com/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>

<ul>
  <c:forEach var="node" items="${nodelist}">
    <c:set var="nodelist" value="${node.children}" scope="request" />
    <c:set var="childcount" value="${fn:length(node.children)}" scope="page" />
    <c:choose>
      <c:when test="${childcount == 0}">
        <li reportid="${node.id}">
          <span>
            <c:out value="${node.name}" escapeXml="false" />
          </span>
        </li>
      </c:when>
      <c:when test="${childcount > 0}">
        <li class="folder">
          <span>
            <c:out value="${node.name}" escapeXml="false" />
          </span> 
          <jsp:include page="reporttree.jsp" />
        </li>
      </c:when>
    </c:choose>
  </c:forEach>
</ul>