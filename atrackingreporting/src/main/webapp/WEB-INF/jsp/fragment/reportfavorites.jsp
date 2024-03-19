<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<input id="basedirectory" type="hidden" value="${reportTree.baseDir}" />
<div id="reportfavorites" class="tile-container">
  <div class="tile">
    <div class="tile-title title">Favorites</div>
    <div class="tile-content">
      <div id="favorite-list"></div>
    </div>
  </div>
</div>