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
      <sf:form method="POST" modelAttribute="registerDTO" >
        <table class="fill-in">
          <tr>
            <td>Username</td>
            <td>
              <sf:input path="username" /> 
              <sf:errors class="error" path="username" />
            </td>
          </tr>
          <tr>
            <td>Password</td>
            <td>
              <sf:password path="password" /> 
              <sf:errors class="error" path="password" />
            </td>
          </tr>
          <tr>
            <td>Display Name</td>
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
          <tr>
            <td></td>
            <td class="button"><input type="submit" value="Register" /></td>
          </tr>
        </table>
      </sf:form>
      </div>
    </article>
    
