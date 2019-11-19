<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr;  
      <a href="<s:url value="/settings" />">Settings</a> &rarr;  
      <span class="nav-current">Change password</span>
    </nav>
    <article>
      <div>
      <sf:form method="POST" modelAttribute="userRegisterDTO" action="/settings/account/password" >
        <table class="fill-in">
          <tr>
            <td>Username</td>
            <td class="pre-fill">
              ${userRegisterDTO.username}
              <sf:hidden path="username" />
            </td>
          </tr>
          <tr>
            <td>Old password</td>
            <td><input id="old_password" type="password" name="old_password" /></td>
          </tr>
          <tr>
            <td>New password</td>
            <td>
              <sf:password path="password" /> 
              <sf:errors class="error" path="password" />
            </td>
          </tr>
          <c:if test="${errorMessage != null}">
          <tr>
            <td></td>
            <td class="error">${errorMessage}</td>
          </tr>
          </c:if>
          <c:if test="${successfulMessage != null}">
          <tr>
            <td></td>
            <td class="success">${successfulMessage}</td>
          </tr>
          </c:if>
          <tr>
            <td></td>
            <td class="button"><input type="submit" value="Update password" /></td>
          </tr>
        </table>
      </sf:form>
      </div>
    </article>
    
