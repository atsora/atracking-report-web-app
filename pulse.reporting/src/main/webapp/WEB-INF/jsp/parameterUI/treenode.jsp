<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ page import="com.lemoinetechnologies.pulse.reporting.treeview.TreeViewNode"%>
<%@ page import="com.lemoinetechnologies.pulse.reporting.treeview.TreeView"%>

<%
	TreeViewNode node = ((TreeViewNode) request.getAttribute("treeviewNode"));
	boolean hasChildren = (node.getChildren().size() > 0) ? true : false;
	pageContext.setAttribute("hasChildren", hasChildren);
%>
<c:if test="${hasChildren}">
	<ul>
		<c:forEach var="entry" items="${treeviewNode.children}">
			<c:choose>
				<c:when test="${(entry.level) == treeviewSize}">
					<c:set var="contain" value="false" />
					<c:forEach var="item" items="${currentvalue[entry.level-1]}">
						<c:if test="${entry.key == item || (allvalue == item && single == false)}">
							<c:set var="contain" value="true" />
						</c:if>
					</c:forEach>
					<c:choose>
						<c:when test="${contain}">
							<li class="selected" id="${entry.key}"><span>${entry.display}</span></li>
						</c:when>
						<c:otherwise>
							<li id="${entry.key}"><span>${entry.display}</span></li>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<li class="folder" id="${entry.key}">
					  <span>${entry.display}</span>
						<c:set var="treeviewNode" value="${entry}" scope="request" />
						<jsp:include page="treenode.jsp" />
				 </li>
				</c:otherwise>
			</c:choose>			
		</c:forEach>
	</ul>
</c:if>
