<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />"><c:out value="${organization.displayName}" /></a> &rarr;
      <a href="<s:url value="/organizations/${organization.id}/settings" />">Settings</a> &rarr; 
      <span class="nav-current">Manage managers</span>
    </nav>
    <article>
      <div class="left-narrow">
        <h5>Managers</h5>
        <ul class="user-list">
        <c:forEach var="map" items="${managerMaps}">
          <li>
            <c:if test="${map.isAlterable(operatorUsername)}">
              <span class="user-deletable"><c:out value="${map.user.displayName}" /></span>
              <s:url var="remove_manager_url" value="/organizations/${organization.id}/settings/managers/remove" />
              <sf:form method="POST" action="${remove_manager_url}">
                <input type="hidden" name="to_be_downgrade_username" value="${map.user.username}" />
                <input type="hidden" name="organization_user_map_id" value="${map.id}" />
                <input class="delete" type="submit" value="&darr;" />
              </sf:form>
            </c:if>
            <c:if test="${!map.isAlterable(operatorUsername)}">
              <span class="user"><c:out value="${map.user.displayName}" /></span>
            </c:if>
          </li>
        </c:forEach>
        </ul>
        <h5>Ordinary Members</h5>
        <ul class="user-list">
        <c:forEach var="map" items="${memberMaps}">
          <li>
            <span class="user-deletable"><c:out value="${map.user.displayName}" /></span>
            <s:url var="remove_manager_url" value="/organizations/${organization.id}/settings/managers/add" />
            <sf:form method="POST" action="${remove_manager_url}">
              <input type="hidden" name="to_be_upgrade_username" value="${map.user.username}" /> 
              <input type="hidden" name="organization_user_map_id" value="${map.id}" />
              <input class="delete" type="submit" value="&uarr;" />
            </sf:form>
          </li>
        </c:forEach>
        </ul>
      </div>
      <div class="right-wide">
      </div>
      <div style="clear:both"></div>
    </article>
