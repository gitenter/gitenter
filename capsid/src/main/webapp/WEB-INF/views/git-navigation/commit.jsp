<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />">${organization.displayName}</a> &rarr; 
      <span class="nav-current">${repository.displayName}</span>
    </nav>
    <article>
      <form method="GET" action="<s:url value="/organizations/${organization.id}/repositories/${repository.id}/branches/${branch}/commits/" />" >
        <input type="submit" value="Browse Commits">
      </form>
      <c:set var="folderStructure" value="${folderStructure}" scope="request"/>
      <jsp:include page="commit-folder-structure.jsp"/>
    </article>
