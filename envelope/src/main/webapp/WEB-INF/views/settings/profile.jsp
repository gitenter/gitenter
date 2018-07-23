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
      <sf:form method="POST" modelAttribute="memberProfileDTO" >
        <table class="fill-in">
          <tr>
            <td>Username</td>
            <td class="pre-fill">
              ${memberProfileDTO.username}
              <sf:hidden path="username" />
            </td>
          </tr>
          <tr>
            <td>Display name</td>
            <td>
              <sf:input path="displayName" />
              <sf:errors class="error" path="displayName" />
            </td>
          </tr>
          <tr>
            <td>Email address</td>
            <td>
              <sf:input path="email" type="email" />
              <sf:errors class="error" path="email" />
            </td>
          </tr>
          <c:if test="${successfulMessage != null}">
          <tr>
            <td></td>
            <td class="success">${successfulMessage}</td>
          </tr>
          </c:if>
          <tr>
            <td></td>
            <td class="button"><input type="submit" value="Update profile" /></td>
          </tr>
        </table>
      </sf:form>
      </div>
    </article>
