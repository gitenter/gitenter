<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />"><c:out value="${organization.displayName}" /></a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}" />"><c:out value="${repository.displayName}" /></a> &rarr; 
      <c:if test="${branchName != null}">
        <span class="nav-current">Branch: <c:out value="${branchName}" /></span>
      </c:if>
      <c:if test="${commitSha != null}">
        <span class="nav-current">Commit: ${fn:substring(commitSha, 0, 6)}</span>
      </c:if>
    </nav>