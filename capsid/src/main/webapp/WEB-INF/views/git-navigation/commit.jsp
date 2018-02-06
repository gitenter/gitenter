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
      <jsp:include page="components/commit-menu.jsp" />
      <div class="folder-structure">
        <h3>Browse files and folders</h3>
        <ul>
          <c:forEach var="i" items="${folderStructure.childrenList()}">
            <c:set var="folderOrFile" value="${i}" scope="request"/>
            <jsp:include page="components/commit-folder-structure.jsp"/>
          </c:forEach>
        </ul>
      </div>
      <jsp:include page="components/repository-collaboration.jsp"/>
    </article>
