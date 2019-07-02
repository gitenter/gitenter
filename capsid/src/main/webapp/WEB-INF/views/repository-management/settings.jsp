<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />"><c:out value="${organization.displayName}" /></a> &rarr;
      <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}" />"><c:out value="${repository.displayName}" /></a> &rarr; 
      <span class="nav-current">Settings</span>
    </nav>
    <article>
      <h3><a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}/settings/profile" />">Edit profile</a></h3>
      <h3><a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}/settings/collaborators" />">Manage collaborators</a></h3>
      <h3><a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}/settings/delete" />">Delete repository</a></h3>
    </article>
