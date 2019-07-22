<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

      <div>
        <h3>
          Collaboration
<%--          <security:authorize access="@securityService.checkManagerOfAnOrganization(authentication,#organization.id)"> --%>
            <s:url var="collaborators_url" value="/organizations/${organization.id}/repositories/${repository.id}/collaborators" />
            <sf:form method="GET" action="${collaborators_url}">
              <input type="submit" value="Settings" />
            </sf:form>
<%--          </security:authorize> --%>
        </h3>
        <table class="hidden">
          <tr>
            <c:forEach var="role" items="${repositoryMemberRoleValues}">
            <td>
              <h6><c:out value="${role.displayName}" /></h6>
              <ul class="user-list">
                <c:forEach var="map" items="${repository.repositoryMemberMaps}">
                  <c:if test="${map.role == role}">
                    <li>
                      <span class="user"><c:out value="${map.member.displayName}" /></span>
                    </li>
                  </c:if> 
                </c:forEach>
              </ul>
            </td>
          </c:forEach>
          </tr>
        </table>
      </div>
      <%--
        Unlike GitHub ... everybody who has the reading access can
        clone an repository, in this site only people who can edit
        can clone it. Note that git "fork" and "pull request" is not
        what a review system want to do, people contribute by being a
        good reviewer. 
      --%>
<%--      <security:authorize access="@securityService.checkRepositoryEditability(authentication,#repository.id)"> --%>
      <div>
        <p><span class="intro">Clone with SSH</span> <code>git clone <c:out value="${gitSshProtocolUrl}" /></code></p>
      </div>
<%--      </security:authorize> --%>