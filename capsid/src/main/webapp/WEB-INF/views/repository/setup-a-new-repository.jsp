<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />"><c:out value="${organization.displayName}" /></a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}" />"><c:out value="${repository.displayName}" /></a> &rarr; 
      <span class="nav-current">Setup a new repository</span>
      <security:authorize access="hasPermission(#repository, T(com.gitenter.protease.domain.auth.RepositoryUserRole).ORGANIZER)">
        <s:url var="repo_settings_url" value="/organizations/${organization.id}/repositories/${repository.id}/settings" />
        <sf:form method="GET" action="${repo_settings_url}">
          <input type="submit" value="Settings" />
        </sf:form>
      </security:authorize>
    </nav>
    <article>
      <div>
        <h3>This is an empty repository</h3>
      </div>
      <jsp:include page="components/repository-collaboration.jsp"/>
    </article>
