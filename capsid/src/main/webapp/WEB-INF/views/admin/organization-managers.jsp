<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />">${organization.displayName}</a> &rarr;
      <span class="nav-current">Managers</span>
    </nav>
    <article>
      <div>
        <h2>Managers:</h2>
        <c:forEach var="member" items="${organization.managers}">
          <h3>
            ${member.displayName}
            <sf:form method="POST" action="/organizations/${organization.id}/managers/${member.id}/remove">
              <input class="delete" type="submit" value="x" />
            </sf:form>
          </h3>
        </c:forEach>
        <sf:form method="POST" action="/organizations/${organization.id}/managers/add">
          <table class="fill-in">
            <tr>
              <td class="form-intro">Username</td>
              <td><input class="form-fill-in" name="managerName" /></td>
            </tr>
            <tr>
              <td></td>
              <td class="form-button"><input type="submit" value="Add a new manager" /></td>
            </tr>
          </table>
        </sf:form>
      </div>
    </article>