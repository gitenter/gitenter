<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <span class="nav-current">${organization.displayName}</span>
    </nav>
    <article>
      <h2>Repositories (<a href="<s:url value="/organizations/${organization.id}/repositories/create" />">+</a>):</h2>
      <c:forEach var="repository" items="${organization.repositories}">
        <h3><a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}" />">${repository.displayName}</a></h3>
      </c:forEach>
      
      <h2>Managers:</h2>
      <c:forEach var="member" items="${organization.managers}">
        <h3>${member.displayName}</h3>
      </c:forEach>
    </article>