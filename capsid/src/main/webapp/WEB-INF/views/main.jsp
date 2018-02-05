<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

    <nav>
      <span class="nav-current">Home</span>
    </nav>
    <article>
      <div>
        <h2>
          Managed organizations
          <s:url var="create_org_url" value="/organizations/create" />
          <sf:form method="GET" action="${create_org_url}">
            <input class="delete" type="submit" value="+" />
          </sf:form>
        </h2>
        <c:forEach var="organization" items="${organizations}">
          <h3><a href="<s:url value="/organizations/${organization.id}" />">${organization.displayName}</a></h3>
        </c:forEach>
      </div>
      <div>
        <h2>Involved repositories</h2>
        <c:forEach var="map" items="${repositoryMemberMaps}">
          <c:set var="repository" value="${map.repository}" />
          <h3><a href="<s:url value="/organizations/${repository.organization.id}/repositories/${repository.id}" />">${repository.displayName}</a> <span class="explanation">: ${map.role.displayName}</span></h3>
        </c:forEach>
      </div>
    </article>
