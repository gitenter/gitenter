<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />">${organization.displayName}</a> &rarr;
      <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}" />">${repository.displayName}</a> &rarr; 
      <c:if test="${branchName != null}">
        <span class="nav-current">Branch: ${branchName.name}</span>
      </c:if>
      <c:if test="${shaChecksumHash != null}">
        <span class="nav-current">Commit: ${shaChecksumHash}</span>
      </c:if>
    </nav>
    <article>
      <c:if test="${branchName != null}">
        <form method="GET" action="<s:url value="/organizations/${organization.id}/repositories/${repository.id}/branches/${branchName.name}/commits/" />" >
          <input class="menu" type="submit" value="Browse Historical Commits">
        </form>
      </c:if>
      <c:set var="folderStructure" value="${folderStructure}" scope="request"/>
      <jsp:include page="commit-folder-structure.jsp"/>
    </article>
