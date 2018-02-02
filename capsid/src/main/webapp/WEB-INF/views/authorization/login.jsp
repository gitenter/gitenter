<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

    <article>
      <div class="login">
      <sf:form method="POST" commandName="memberLoginBean" >
        <table class="fill-in">
          <tr>
            <td>Username</td>
            <td>
              <sf:input path="username" />
            </td>
          </tr>
          <tr>
            <td>Password</td>
            <td>
              <sf:password path="password" />
            </td>
          </tr>
          <tr>
            <td></td>
            <td class="word">
              <input id="remember_me" name="remember-me" type="checkbox"/>
              <label for="remember_me">Remember me</label>
            </td>
          </tr>
          <c:if test="${message != null}">
            <tr>
              <td></td>
              <td class="error">${message}</td>
            </tr>
          </c:if>
          <tr>
            <td></td>
            <td class="button">
              <input type="submit" value="Log in" />
              <input type="button" onclick="location.href='<s:url value="/register" />';" value="Sign up" />
            </td>
          </tr>
        </table>
      </sf:form>
      </div>
    </article>
