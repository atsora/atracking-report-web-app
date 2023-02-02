<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ page import="com.lemoinetechnologies.pulse.reporting.domain.GroupParameter"%>
<%@ page import="com.lemoinetechnologies.pulse.reporting.domain.ScalarParameter"%>
<%@ page import="com.lemoinetechnologies.pulse.reporting.treeview.TreeView"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Set"%>
<%@ page import="com.lemoinetechnologies.pulse.reporting.treeview.TreeViewNode"%>
<%@ page import="com.lemoineautomation.pulse.reporting.treeview.TreeView"%>
<%@ page import="org.apache.logging.log4j.LogManager"%>
<%@ page import="org.apache.logging.log4j.Logger"%>
<%@ page import="java.util.HashSet"%>

<input type="hidden" id="groupname" value="${groupParameter.name}" />
<input type="hidden" id="displayform" value="${groupParameter.displayForm}" />
<input type="hidden" id="helptext" value="${groupParameter.helpText}" />
<% 
  Logger logger = LogManager.getLogger(ScalarParameter.class.getName());
  GroupParameter groupParameter = ((GroupParameter)request.getAttribute("groupParameter"));
  HashSet<String> selectionKeySet = new HashSet<String>(groupParameter.getScalarParameterMap().keySet());
  pageContext.setAttribute("selectionKeySet", selectionKeySet);
  TreeView treeview = null;
  if (groupParameter.getDisplayForm().equals("TREEVIEW")) {
    treeview = groupParameter.getTreeView();
  } else if (groupParameter.getDisplayForm().equals("TREEVIEW_BY_LEAF")) {
    treeview = groupParameter.getTreeViewAsCompanyDepartmentMachine();
  } else {
    throw new Exception("Unsupported displayForm: " + groupParameter.getDisplayForm());
  }
  
  int treeviewSize = treeview.getSize();
  
  request.setAttribute("treeview",treeview);
  request.setAttribute("treeviewSize",treeviewSize);
  ArrayList<String[]> currentvalue = new ArrayList<String[]>(treeviewSize);
  for (int i = 1; i <= treeviewSize; i++) {
    currentvalue.add(groupParameter.getScalarParameterMap().get(Integer.toString(i)).getValue());
  }
  request.setAttribute("currentvalue", currentvalue);
  request.setAttribute("allvalue", "-1");
  request.setAttribute("single", treeview.isSingleSelection());
%>
<c:forEach var="scalarParameterKey" items="${selectionKeySet}" >
  <c:set var="scalarParameter" value="${groupParameter.scalarParameterMap[scalarParameterKey]}"></c:set>
  <div class="parameter" style="display:none">
    <input type="hidden" id="parameterkey" value="${scalarParameterKey}" />
    <input type="hidden" id="name" value="${scalarParameter.name}" />
    <c:choose>
      <c:when test="${empty scalarParameter.defaultValue}">
        <input type="hidden" id="defaultvalue" value="" />
      </c:when>
      <c:otherwise>
        <input type="hidden" id="defaultvalue" value="${scalarParameter.defaultValue[0]}" />
      </c:otherwise>
    </c:choose>
    <input type="hidden" id="datatype" value="${scalarParameter.dataType}" />
    <input type="hidden" id="parametertype" value="${scalarParameter.parameterType}" />
    <input type="hidden" id="required" value="${scalarParameter.required}" />
    <input type="hidden" id="helptext" value="${scalarParameter.helpText}" />
    <input type="hidden" id="hidden" value="${scalarParameter.hidden}" />
  </div>
</c:forEach>
<div class="parameter">
  <c:choose>
    <c:when test="${single == true}">
      <div class="treeviewparametersingle">
    </c:when>
    <c:otherwise>
      <div class="treeviewparameter">
    </c:otherwise>
  </c:choose>
    <ul>
      <li class="folder expanded">
        <span class="folder">All</span>
        <ul>
          <c:forEach var="rootnode"  items="${treeview.roots}">
            <c:if test="${(rootnode.level) == treeviewSize}"> <!-- current node is a leaf -->
              <c:set var="contain" value="false" />
              <c:forEach var="item" items="${currentvalue[rootnode.level-1]}">
                <c:if test="${rootnode.key == item || (allvalue == item && single == false)}">
                  <c:set var="contain" value="true" />
                </c:if>
              </c:forEach>
              <c:choose>
                <c:when test="${contain}">
                  <li class="selected" id="${rootnode.key}">
                    <span>${rootnode.display}</span>
                  </li>
                </c:when>
                <c:otherwise>
                  <li id="${rootnode.key}">
                    <span>${rootnode.display}</span>
                  </li>
                </c:otherwise>
              </c:choose>
            </c:if>
            <c:if test="${(rootnode.level) != treeviewSize}"> <!-- current node is not a leaf -->
              <c:set var="contain" value="false" />
              <c:forEach var="item" items="${currentvalue[rootnode.level-1]}">
                <c:if test="${rootnode.key == item || (allvalue == item && single == false)}">
                  <c:set var="contain" value="true" />
                </c:if>
              </c:forEach>
              <c:choose>
                <c:when test="${contain}">
                  <li class="selected folder" id="${rootnode.key}">
                    <span>${rootnode.display}</span>
                    <c:set var="treeviewNode" value="${rootnode}" scope="request" />
                    <jsp:include page="parameterUI/treenode.jsp" />
                  </li>
                </c:when>
                <c:otherwise>
                  <li class="folder" id="${rootnode.key}">
                    <span>${rootnode.display}</span>
                    <c:set var="treeviewNode" value="${rootnode}" scope="request" />
                    <jsp:include page="parameterUI/treenode.jsp" />
                  </li>
                </c:otherwise>
              </c:choose>	                
            </c:if>
          </c:forEach>
        </ul>
      </li>
    </ul>
  </div>
</div>

<script type="text/javascript">
$(function() {
  /*$(".treeviewparameter").dynatree({
    checkbox: true,
    selectMode: 3,
    onActivate: function(node) {},
    onDeactivate: function(node) {}
  });
  $(".treeviewparametersingle").dynatree({
    checkbox: true,
    selectMode: 3,
    onActivate: function(node) {},
    onDeactivate: function(node) {},
    onCreate: function(node, nodeSpan) {
      if (node.hasChildren() == true) {
        node.data.unselectable = true;
      }
    },
    onSelect: function(flag, node) {
      var selectedNodes = node.tree.getSelectedNodes();
      if (flag) {
        for (var i = 0; i < selectedNodes.length; i++) {
          if (node != selectedNodes[i]) {
            selectedNodes[i].select(false);
          }
        }
      } else if (selectedNodes.length == 0) {
        node.select(true);
      }
    }
  });*/
});
</script>