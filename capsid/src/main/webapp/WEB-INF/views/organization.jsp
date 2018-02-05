<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <span class="nav-current">${organization.displayName}</span>
    </nav>
    <article>
      <div class="left-wide">
        <h2>Repositories (<a href="<s:url value="/organizations/${organization.id}/repositories/create" />">+</a>):</h2>
        <c:forEach var="repository" items="${organization.repositories}">
          <h3>
            <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}" />">${repository.displayName}</a>
            (<a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}/collaborators" />">Collaborators</a>)
          </h3>
        </c:forEach>
      </div>
      <div class="right-narrow">
        <h2>Managers</h2>
        <ul class="user-list">
          <c:forEach var="member" items="${organization.managers}">
            <li><span class="user">${member.displayName}</span></li>
          </c:forEach>
        </ul>
        <c:if test="${isManager == true}">
          <p>(<a href="<s:url value="/organizations/${organization.id}/managers" />">settings ...</a>)</p>
        </c:if>
      </div>
      <div style="clear:both"></div>
    </article>