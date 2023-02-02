<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

  <input type="hidden" id="widget" value="DATETIMEBOX" />
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
  <c:choose>
    <c:when test="${empty scalarParameter.value}">
      <input type="hidden" id="${scalarParameter.name}_currentvalue" value="" />
    </c:when>
    <c:otherwise>
      <input type="hidden" id="${scalarParameter.name}_currentvalue" value="${scalarParameter.value[0]}" />
    </c:otherwise>
  </c:choose>
  <x-datetimepicker id="${scalarParameter.name}_value" showseconds="true"></x-datetimepicker>

  <script type="text/javascript">
    $(function () {
      //var dateTimeMoment = moment($('#${scalarParameter.name}_currentvalue').val(),
      //["YYYY-MM-DD", "YYYY-MM-DD HH:mm", "YYYY-MM-DD HH:mm:ss", "YYYY-MM-DD HH:mm:ss.SSS"]);

      var dateTimeMoment = moment($('#${scalarParameter.name}_currentvalue').val(),
      ["YYYY-MM-DD", "YYYY-MM-DD HH:mm", "YYYY-MM-DD HH:mm:ss", "YYYY-MM-DD HH:mm:ss.SSS"]);
      $('#${scalarParameter.name}_value').attr('defaultdatetime',
        (dateTimeMoment == null || !dateTimeMoment.isValid() ? moment() : dateMoment)
          .format('YYYY-MM-DDTHH:mm:ss')); // iso
    });
  </script>