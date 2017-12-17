<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <span class="nav-current">${organization.displayName}</span>
    </nav>
    <article>
      <h2>Repositories:</h2>
      <c:forEach var="repository" items="${organization.repositories}">
        <h3><a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}" />">${repository.displayName}</a></h3>
      </c:forEach>
    </article>