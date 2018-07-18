<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <span class="nav-current"><c:out value="${organization.displayName}" /></span>
    </nav>
    <article>
      <div class="left-wide">
        <h3>
          Repositories
          <s:url var="create_repo_url" value="/organizations/${organization.id}/repositories/create" />
          <security:authorize access="hasPermission(#organizationId, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MANAGER) or hasPermission(#organizationId, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MEMBER)">
            <sf:form method="GET" action="${create_repo_url}">
              <input type="submit" value="+" />
            </sf:form>
          </security:authorize>
        </h3>
        <c:forEach var="repository" items="${repositories}">
          <h5>
            <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}" />"><c:out value="${repository.displayName}" /></a>
            <span class="explanation">: 
              <c:if test="${repository.isPublic.equals(true)}">Public</c:if>
              <c:if test="${repository.isPublic.equals(false)}">Private</c:if>
            </span>
 <%--           <security:authorize access="@organizationService.isManagedBy(#organization.id, authentication)">--%>
              <s:url var="repo_settings_url" value="/organizations/${organization.id}/repositories/${repository.id}/settings" />
              <sf:form method="GET" action="${repo_settings_url}">
                <input type="submit" value="Settings" />
              </sf:form>
              <s:url var="repo_collaborators_url" value="/organizations/${organization.id}/repositories/${repository.id}/collaborators" />
              <sf:form method="GET" action="${repo_collaborators_url}">
                <input type="submit" value="Collaborators" />
              </sf:form>
 <%--         	</security:authorize>--%>
          </h5>
          <p><c:out value="${repository.description}" /></p>
        </c:forEach>
      </div>
      <div class="right-narrow">
        <h3>
          Members
          <security:authorize access="hasPermission(#organizationId, T(com.gitenter.protease.domain.auth.OrganizationMemberRole).MANAGER)">
            <s:url var="manager_url" value="/organizations/${organization.id}/settings" />
            <sf:form method="GET" action="${manager_url}">
              <input type="submit" value="Settings" />
            </sf:form>
          </security:authorize>
        </h3>
        <ul class="user-list">
          <c:forEach var="member" items="${members}">
            <li><span class="user"><c:out value="${member.displayName}" /></span></li>
          </c:forEach>
        </ul>
      </div>
      <div style="clear:both"></div>
    </article>