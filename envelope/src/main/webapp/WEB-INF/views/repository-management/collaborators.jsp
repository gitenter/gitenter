<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />"><c:out value="${organization.displayName}" /></a> &rarr;
      <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}" />">${repository.displayName}</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}/settings" />">Settings</a> &rarr; 
      <span class="nav-current">Collaborators</span>
    </nav>
    <article>
      <div class="left-narrow">
        <c:forEach var="role" items="${collaboratorRoles}">
        <h5><c:out value="${role.displayName}" /></h5>
          <ul class="user-list">
            <c:forEach var="map" items="${repository.repositoryMemberMaps}">
              <c:if test="${map.role == role}">
                <li>
                  <c:if test="${map.isAlterable(operatorUsername)}">
                    <span class="user-deletable">${map.member.displayName}</span>
                    <s:url var="remove_member_url" value="/organizations/${organization.id}/repositories/${repository.id}/settings/collaborators/remove" />
                    <sf:form method="POST" action="${remove_member_url}">
                      <input type="hidden" name="to_be_remove_username" value="${map.member.username}" />
                      <input type="hidden" name="repository_member_map_id" value="${map.id}" />
                      <input class="delete" type="submit" value="x" />
                    </sf:form>
                  </c:if>
                  <c:if test="${!map.isAlterable(operatorUsername)}">
                    <span class="user"><c:out value="${map.member.displayName}" /></span>
                  </c:if>
                </li>
              </c:if> 
            </c:forEach>
          </ul>
        </c:forEach>
      </div>
      <div class="right-wide">
        <s:url var="add_collaborator_url" value="/organizations/${organization.id}/repositories/${repository.id}/settings/collaborators/add" />
        <sf:form method="POST" action="${add_collaborator_url}">
          <table class="fill-in">
            <tr>
              <td>Username</td>
              <td><input type="text" name="to_be_add_username" /></td>
            </tr>
            <tr>
              <td>Role</td>
              <td>
                <select name="roleName">
                  <c:forEach var="role" items="${collaboratorRoles}">
                  <option value="${role}">${role.displayName}</option>  
                  </c:forEach>
                </select>
              </td>
            </tr>
            <tr>
              <td></td>
              <td class="button"><input type="submit" value="Add a new Collaborator" /></td>
            </tr>
          </table>
        </sf:form>
      </div>
      <div style="clear:both"></div>
    </article>
