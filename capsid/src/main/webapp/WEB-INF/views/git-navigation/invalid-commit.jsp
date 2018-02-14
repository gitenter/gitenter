<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />"><c:out value="${organization.displayName}" /></a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}" />"><c:out value="${repository.displayName}" /></a> &rarr; 
      <c:if test="${branch != null}">
        <span class="nav-current">Branch: <c:out value="${branch}" /></span>
      </c:if>
      <c:if test="${shaChecksumHash != null}">
        <span class="nav-current">Commit: ${fn:substring(shaChecksumHash, 0, 6)}</span>
      </c:if>
    </nav>
    <article>
      <div>
        <jsp:include page="components/commit-menu.jsp"/>
      </div>
      <div class="error">
        <h3>Trace Analyzer error</h3>
        <p><c:out value="${errorMessage}" /></p>
      </div>
      <jsp:include page="components/repository-collaboration.jsp"/>
    </article>
