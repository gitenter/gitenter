<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />">${organization.displayName}</a> &rarr;
      <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}" />">${repository.displayName}</a> &rarr; 
      <c:if test="${branch != null}">
        <span class="nav-current">Branch: ${branch}</span>
      </c:if>
      <c:if test="${shaChecksumHash != null}">
        <span class="nav-current">Commit: ${fn:substring(shaChecksumHash, 0, 6)}</span>
      </c:if>
    </nav>
    <article>
      <jsp:include page="components/commit-menu.jsp"/>
      <div>
        <c:set var="folderStructure" value="${folderStructure}" scope="request"/>
        <jsp:include page="components/commit-folder-structure.jsp"/>
      </div>
      <div>
        <p>Clone with SSH: <code>git clone git@${rootUrl}:${organization.name}/${repository.name}</code></p>
      </div>
    </article>
