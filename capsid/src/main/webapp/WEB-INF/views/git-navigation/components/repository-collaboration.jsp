<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

      <div>
        <h3>
          Collaboration
          <security:authorize access="@securityService.checkManagerOfAnOrganization(authentication,#organization.id)">
            <s:url var="collaborators_url" value="/organizations/${organization.id}/repositories/${repository.id}/collaborators" />
            <sf:form method="GET" action="${collaborators_url}">
              <input type="submit" value="Settings" />
            </sf:form>
          </security:authorize>
        </h3>
        <table class="hidden">
          <tr>
            <c:forEach var="role" items="${repositoryMemberRoleValues}">
            <td>
              <h6>${role.displayName}</h6>
              <ul class="user-list">
                <c:forEach var="map" items="${repository.repositoryMemberMaps}">
                  <c:if test="${map.role == role}">
                    <li>
                      <span class="user">${map.member.displayName}</span>
                    </li>
                  </c:if> 
                </c:forEach>
              </ul>
            </td>
          </c:forEach>
          </tr>
        </table>
      </div>
      <div>
        <p><span class="intro">Clone with SSH</span> <code>git clone git@${rootUrl}:${organization.name}/${repository.name}</code></p>
      </div>