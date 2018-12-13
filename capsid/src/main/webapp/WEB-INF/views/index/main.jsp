<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

    <nav>
      <span class="nav-current">Home</span>
    </nav>
    <article>
      <div class="left-wide">
        <h3>Organized Repositories</h3>
        <c:forEach var="repository" items="${organizedRepositories}">
          <h5><a href="<s:url value="/organizations/${repository.organization.id}/repositories/${repository.id}" />">${repository.displayName}</a></h5>
        </c:forEach>
        <h3>Authored Repositories</h3>
        <c:forEach var="repository" items="${organizedRepositories}">
          <h5><a href="<s:url value="/organizations/${repository.organization.id}/repositories/${repository.id}" />">${repository.displayName}</a></h5>
        </c:forEach>
        <h3>Currently reviewed repository</h3>
      </div>
      <div class="right-narrow">
        <h3>
          Managed organizations
          <s:url var="create_org_url" value="/organizations/create" />
          <sf:form method="GET" action="${create_org_url}">
            <input type="submit" value="+" />
          </sf:form>
        </h3>
        <c:forEach var="organization" items="${managedOrganizations}">
          <h5><a href="<s:url value="/organizations/${organization.id}" />">${organization.displayName}</a></h5>
        </c:forEach>
        <h3>Belonged organizations</h3>
        <c:forEach var="organization" items="${belongedOrganizations}">
          <h5><a href="<s:url value="/organizations/${organization.id}" />">${organization.displayName}</a></h5>
        </c:forEach>
      </div>
      <div style="clear:both"></div>
    </article>
