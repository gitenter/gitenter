<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

    <nav>
      <span class="nav-current">Home</span>
    </nav>
    <article>
      <h2>Organizations: (<a href="<s:url value="/organizations/create" />">+</a>)</h2>
      <c:forEach var="organization" items="${organizations}">
        <h3><a href="<s:url value="/organizations/${organization.id}" />">${organization.displayName}</a></h3>
      </c:forEach>
    </article>
