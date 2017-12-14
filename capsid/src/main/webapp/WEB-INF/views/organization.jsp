<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

    <nav>
      <span class="nav-current">Main Page</span>
    </nav>
    <article>
      <h2>Repositories:</h2>
      <c:forEach var="repository" items="${organization.repositories}">
        <h3><a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}/" />">${repository.displayName}</a></h3>
      </c:forEach>
    </article>

    http://localhost:8888/organizations/1/repositories/1
    http://localhost:8888/organizations/1/repositories/1/