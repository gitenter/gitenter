<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>

    <nav>
      <a href="<s:url value="/" />">Main Page</a> &rarr;  
      <a href="<s:url value="/settings" />">Settings</a> &rarr;  
      <span class="nav-current">Account</span>
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
            <td class="setting-intro">Old password</td>
            <td><input class="setting-fill-in" type="password" name="old_password" /></td>
          </tr>
          <tr>
            <td class="setting-intro">New password</td>
            <td>
              <sf:password class="setting-fill-in" path="password" /> <sf:errors class="setting-error" path="password" />
              <c:if test="${errorMessage != null}">
                <span class="setting-error">${errorMessage}</span>
              </c:if>
              <c:if test="${successfulMessage != null}">
                <span class="setting-successful-message">${successfulMessage}</span>
              </c:if>
            </td>
          </tr>
          <tr>
            <td></td>
            <td class="setting-button"><input type="submit" value="Update password" /></td>
          </tr>
        </table>
      </sf:form>
      </div>
    </article>
    
