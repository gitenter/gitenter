<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />"><c:out value="${organization.displayName}" /></a> &rarr;
      <a href="<s:url value="/organizations/${organization.id}/settings" />">Settings</a> &rarr; 
      <span class="nav-current">Members</span>
    </nav>
    <article>
      <div class="left-narrow">
        <h5>Members</h5>
        <ul class="user-list">
        <c:forEach var="member" items="${members}">
          <li>
            <c:if test="${member.username != myUsername}">
              <span class="user-deletable"><c:out value="${member.displayName}" /></span>
              <s:url var="remove_member_url" value="/organizations/${organization.id}/settings/members/remove" />
              <sf:form method="POST" action="${remove_member_url}">
                <input type="hidden" name="username" value="${member.username}" /> 
                <input class="delete" type="submit" value="x" />
              </sf:form>
            </c:if>
            <c:if test="${member.username == myUsername}">
              <span class="user"><c:out value="${member.displayName}" /></span>
            </c:if>
          </li>
        </c:forEach>
        </ul>
      </div>
      <div class="right-wide">
        <s:url var="add_manager_url" value="/organizations/${organization.id}/settings/members/add" />
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
              <td><input id="username" name="username" type="text" /></td>
            </tr>
            <tr>
              <td></td>
              <td class="button"><input type="submit" value="Add a new member" /></td>
            </tr>
          </table>
        </sf:form>
      </div>
      <div style="clear:both"></div>
    </article>
