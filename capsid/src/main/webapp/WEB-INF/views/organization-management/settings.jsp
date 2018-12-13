<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />"><c:out value="${organization.displayName}" /></a> &rarr;
      <span class="nav-current">Settings</span>
    </nav>
    <article>
      <h3><a href="<s:url value="/organizations/${organization.id}/settings/profile" />">Profile</a></h3>
      <h3><a href="<s:url value="/organizations/${organization.id}/settings/members" />">Members</a></h3>
      <h3><a href="<s:url value="/organizations/${organization.id}/settings/managers" />">Managers</a></h3>
    </article>
