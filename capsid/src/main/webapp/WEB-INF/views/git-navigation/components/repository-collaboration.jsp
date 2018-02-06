<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

      <div>
        <p><span class="intro">Clone with SSH</span> <code>git clone git@${rootUrl}:${organization.name}/${repository.name}</code></p>
      </div>
      <div>
        <table class="hidden">
          <tr>
            <c:forEach var="role" items="${repositoryMemberRoleValues}">
            <td>
              <h3>${role.displayName}</h3>
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