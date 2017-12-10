<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />">${organization.displayName}</a> &rarr; 
      <span class="nav-current">${repository.displayName}</span>
    </nav>
    <article>

      <c:forEach var="folderOrFile" items="${ls}">
        <!-- TODO: print the node here -->
        ${folderOrFile}
      </c:forEach>
    </article>