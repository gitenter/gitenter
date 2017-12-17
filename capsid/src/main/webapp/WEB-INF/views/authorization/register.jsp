<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <span class="nav-current">Sign Up</span>
    </nav>
    <article>
      <div>
      <sf:form method="POST" commandName="memberBean" >
        <table class="fill-in">
          <tr>
            <td class="form-intro">Username</td>
            <td><sf:input class="form-fill-in" path="username" /> <sf:errors class="form-error" path="username" /></td>
          </tr>
          <tr>
            <td class="form-intro">Password</td>
            <td><sf:password class="form-fill-in" path="password" /> <sf:errors class="form-error" path="password" /></td>
          </tr>
          <tr>
            <td class="form-intro">Display Name</td>
            <td><sf:input class="form-fill-in" path="displayName" />  <sf:errors class="form-error" path="displayName" /></td>
          </tr>
          <tr>
            <td class="form-intro">Email address</td>
            <td><sf:input class="form-fill-in" path="email" type="email" />  <sf:errors class="form-error" path="email" /></td>
          </tr>
          <tr>
            <td></td>
            <td class="form-button"><input type="submit" value="Register" /></td>
          </tr>
        </table>
      </sf:form>
      </div>
    </article>
    
