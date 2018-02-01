<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />">${organization.displayName}</a> &rarr;
      <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}" />">${repository.displayName}</a> &rarr; 
      <span class="nav-current">Collaborators</span>
    </nav>
    <article>
      <div>
        <c:forEach var="role" items="${repositoryMemberRoleValues}">
        <h3>${role.displayName}</h3>
          <c:forEach var="map" items="${repository.repositoryMemberMaps}">
            <c:if test="${map.role == role}">
              <h5>
                ${map.member.displayName}
                <s:url var="remove_member_url" value="/organizations/${organization.id}/repositories/${repository.id}/collaborators/${map.id}/remove" />
                <sf:form method="POST" action="${remove_member_url}">
                  <input class="delete" type="submit" value="x" />
                </sf:form>
              </h5>
            </c:if> 
          </c:forEach>
        </c:forEach>
      </div>
      <div>
        <s:url var="add_collaborator_url" value="/organizations/${organization.id}/repositories/${repository.id}/collaborators/add" />
        <sf:form method="POST" action="${add_collaborator_url}">
          <table class="fill-in">
            <tr>
              <td class="form-intro">Username</td>
              <td><input type="text" class="form-fill-in" name="username" /></td>
            </tr>
            <tr>
              <td class="form-intro">Role</td>
              <td>
                <select name="role">
                  <c:forEach var="role" items="${repositoryMemberRoleValues}">
                  <option value="${role}">${role.displayName}</option>  
                  </c:forEach>
                </select>
              </td>
            </tr>
            <tr>
              <td></td>
              <td class="form-button"><input type="submit" value="Add a new Collaborator" /></td>
            </tr>
          </table>
        </sf:form>
      </div>
    </article>
