<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr;  
      <a href="<s:url value="/settings" />">Settings</a> &rarr;  
      <span class="nav-current">Delete account</span>
    </nav>
    <article>
      <div>
      <sf:form method="POST" action="/settings/account/delete" >
      	<table class="fill-in">
      	  <tr>
            <td>Username</td>
            <td class="pre-fill">
              <security:authentication property="principal.username" />
            </td>
          </tr>
          <tr>
            <td>Password</td>
            <td><input type="password" id="password" name="password" /></td>
          </tr>
          <c:if test="${errorMessage != null}">
          <tr>
            <td></td>
            <td class="error">${errorMessage}</td>
          </tr>
          </c:if>
      	  <tr>
            <td></td>
            <td class="button"><input type="submit" value="Delete account" /></td>
          </tr>
      	</table>
      </sf:form>
      </div>
    </article>
    
