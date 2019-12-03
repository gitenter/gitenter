<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

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
      <security:authorize access="hasPermission(#repository, T(com.gitenter.protease.domain.auth.RepositoryUserRole).PROJECT_ORGANIZER)">
        <s:url var="repo_settings_url" value="/organizations/${organization.id}/repositories/${repository.id}/settings" />
        <sf:form method="GET" action="${repo_settings_url}">
          <input type="submit" value="Settings" />
        </sf:form>
      </security:authorize>
    </nav>