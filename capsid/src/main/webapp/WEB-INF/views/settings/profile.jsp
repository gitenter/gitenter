<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/settings" />">Settings</a> &rarr; 
      <span class="nav-current">Profile</span>
    </nav>
    <article>
      <div>
      <sf:form method="POST" commandName="memberBean" >
        <table class="fill-in">
          <tr>
            <td class="form-intro">Username</td>
            <td class="form-pre-fill">
              ${memberBean.username}
              <sf:hidden path="username" />
            </td>
          </tr>
          <tr>
            <td class="form-intro">Display name</td>
            <td><sf:input class="form-fill-in" path="displayName" />  <sf:errors class="form-error" path="displayName" /></td>
          </tr>
          <tr>
            <td class="form-intro">Email address</td>
            <td><sf:input class="form-fill-in" path="email" type="email" />  <sf:errors class="form-error" path="email" /></td>
          </tr>
          <tr>
            <td></td>
            <td>
              <c:if test="${successfulMessage != null}">
                <span class="form-successful-message">${successfulMessage}</span>
              </c:if>
            </td>
          </tr>
          <tr>
            <td></td>
            <td class="form-button"><input type="submit" value="Update profile" /></td>
          </tr>
        </table>
      </sf:form>
      </div>
    </article>
