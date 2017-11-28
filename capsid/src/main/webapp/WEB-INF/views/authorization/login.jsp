<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

    <article>
      <div class="login">
      <sf:form method="POST" commandName="memberLoginBean" >
        <table class="fill-in">
          <tr>
            <td class="login-intro">Username</td>
            <td><sf:input class="login-fill-in" path="username" /></td>
          </tr>
          <tr>
            <td class="login-intro">Password</td>
            <td>
              <sf:password class="login-fill-in" path="password" />
              <c:if test="${message != null}">
                <span class="login-error">${message}</span>
              </c:if>
            </td>
          </tr>
          <tr>
            <td></td>
            <td>
              <input class="login-remember-me" id="remember_me" name="remember-me" type="checkbox"/>
              <label class="login-remember-me" for="remember_me">Remember me</label>
            </td>
          <tr>
            <td></td>
            <td class="login-button">
              <input class="login" type="submit" value="Log in" />
              <input class="signup" type="button" onclick="location.href='<s:url value="/register" />';" value="Sign up" />
            </td>
          </tr>
        </table>
      </sf:form>
      </div>
    </article>
