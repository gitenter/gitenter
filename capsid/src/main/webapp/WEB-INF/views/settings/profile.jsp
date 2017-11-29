<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>

    <nav>
      <a href="<s:url value="/" />">Main Page</a> &rarr; 
      <a href="<s:url value="/settings" />">Settings</a> &rarr; 
      <span class="nav-current">Profile</span>
    </nav>
    <article>
      <div>
      <sf:form method="POST" commandName="memberBean" >
        <table class="fill-in">
          <tr>
            <td class="setting-intro">Username</td>
            <td class="setting-pre-fill">
              ${memberBean.username}
              <sf:hidden path="username" value="${memberBean.username}" />
            </td>
          </tr>
          <tr>
            <td class="setting-intro">Display name</td>
            <td><sf:input class="setting-fill-in" path="displayName" />  <sf:errors class="setting-error" path="displayName" /></td>
          </tr>
          <tr>
            <td class="setting-intro">Email address</td>
            <td><sf:input class="setting-fill-in" path="email" type="email" />  <sf:errors class="setting-error" path="email" /></td>
              <c:if test="${successfulMessage != null}">
                <span class="setting-successful-message">${successfulMessage}</span>
              </c:if>
            </td>
          </tr>
          <tr>
            <td></td>
            <td class="setting-button"><input type="submit" value="Update profile" /></td>
          </tr>
        </table>
      </sf:form>
      </div>
    </article>
