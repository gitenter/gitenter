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
        <h3>Managers:</h3>
        <c:forEach var="member" items="${organization.managers}">
          <h5>
            ${member.displayName}
            <c:if test="${!member.id.equals(self.id)}" >
            <s:url var="remove_manager_url" value="/organizations/${organization.id}/managers/remove" />
            <sf:form method="POST" action="${remove_manager_url}">
              <input type="hidden" name="member_id" value="${member.id}" /> 
              <input class="delete" type="submit" value="x" />
            </sf:form>
            </c:if>
          </h5>
        </c:forEach>
        <s:url var="add_manager_url" value="/organizations/${organization.id}/managers/add" />
        <sf:form method="POST" action="${add_manager_url}">
          <table class="fill-in">
            <c:if test="${errorMessage != null}">
            <tr>
              <td></td>
              <td class="error">${errorMessage}</td>
            </tr>
            </c:if>
            <tr>
              <td>Username</td>
              <td><input type="text" name="username" /></td>
            </tr>
            <tr>
              <td></td>
              <td class="button"><input type="submit" value="Add a new manager" /></td>
            </tr>
          </table>
        </sf:form>
      </div>
    </article>
