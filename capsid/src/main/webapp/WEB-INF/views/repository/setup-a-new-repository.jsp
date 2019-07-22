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
      <security:authorize access="hasPermission(#repository, T(com.gitenter.protease.domain.auth.RepositoryMemberRole).ORGANIZER)">
        <s:url var="repo_settings_url" value="/organizations/${organization.id}/repositories/${repository.id}/settings" />
        <sf:form method="GET" action="${repo_settings_url}">
          <input type="submit" value="Settings" />
        </sf:form>
      </security:authorize>
    </nav>
    <article>
      <div>
        <p>This repository has not been initialized yet.</p>
        <p>To initialize it, you want to add the following file with name <code>gitenter.properties</code> 
        at the root folder of your repository,</p>
        <pre><code># ---------------------------
# GitEnter configuration file
# ---------------------------

# Value on/off. Set it as off will overwrite all further setups.
enable_systemwide = on

# Paths one in each line:
# > include_paths = folder1
# > include_paths = folder2
# > include_paths = folder3
#
# Or paths have comma in between:
# > include_paths = folder1,folder2,folder3
#
# If nothing is specified, then all files are  included.
include_paths = doc</code></pre>
        <p>and add this server as your remote site,</p>
        <pre><code>git remote add origin <c:out value="${gitSshProtocolUrl}" />
git push origin master</code></pre>
      </div>
      <%--
        TODO:
        Since this repository has not been initialized yet,
        in the collaborator list, the clone link shouldn't appear. 
      --%>
      <jsp:include page="components/repository-collaboration.jsp"/>
    </article>
