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
      <div class="left-narrow">
        <h2>Managers</h2>
        <ul class="user-list">
        <c:forEach var="member" items="${organization.managers}">
          <li>
            <c:if test="${!member.id.equals(self.id)}" >
              <span class="user-deletable">${member.displayName}</span>
              <s:url var="remove_manager_url" value="/organizations/${organization.id}/managers/remove" />
              <sf:form method="POST" action="${remove_manager_url}">
                <input type="hidden" name="member_id" value="${member.id}" /> 
                <input class="delete" type="submit" value="x" />
              </sf:form>
            </c:if>
            <c:if test="${member.id.equals(self.id)}" >
              <span class="user">${member.displayName}</span>
            </c:if>
          </li>
        </c:forEach>
        </ul>
      </div>
      <div class="right-wide">
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
      <div style="clear:both"></div>
    </article>
