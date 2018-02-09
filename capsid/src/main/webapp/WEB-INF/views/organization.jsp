<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <span class="nav-current">${organization.displayName}</span>
    </nav>
    <article>
      <div class="left-wide">
        <h3>
          Repositories
          <s:url var="create_repo_url" value="/organizations/${organization.id}/repositories/create" />
          <security:authorize access="@securityService.checkManagerOfAnOrganization(authentication,#organization.id)">
            <sf:form method="GET" action="${create_repo_url}">
              <input type="submit" value="+" />
            </sf:form>
          </security:authorize>
        </h3>
        <c:forEach var="repository" items="${organization.repositories}">
          <h5>
            <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}" />">${repository.displayName}</a>
            <security:authorize access="@securityService.checkManagerOfAnOrganization(authentication,#organization.id)">
              <s:url var="repo_settings_url" value="/organizations/${organization.id}/repositories/${repository.id}/settings" />
              <sf:form method="GET" action="${repo_settings_url}">
                <input type="submit" value="Settings" />
              </sf:form>
          	</security:authorize>
          </h5>
          <p>${repository.description}</p>
        </c:forEach>
      </div>
      <div class="right-narrow">
        <h3>
          Managers
          <security:authorize access="@securityService.checkManagerOfAnOrganization(authentication,#organization.id)">
            <s:url var="manager_url" value="/organizations/${organization.id}/managers" />
            <sf:form method="GET" action="${manager_url}">
              <input type="submit" value="Settings" />
            </sf:form>
          </security:authorize>
        </h3>
        <ul class="user-list">
          <c:forEach var="member" items="${organization.managers}">
            <li><span class="user">${member.displayName}</span></li>
          </c:forEach>
        </ul>
      </div>
      <div style="clear:both"></div>
    </article>