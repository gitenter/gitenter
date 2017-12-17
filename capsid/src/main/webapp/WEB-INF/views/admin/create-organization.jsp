<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <span class="nav-current">Create a New Organization</span>
    </nav>
    <article>
      <div>
      <sf:form method="POST" commandName="organizationBean" >
        <table class="fill-in">
          <tr>
            <td class="form-intro">Name</td>
            <td><sf:input class="form-fill-in" path="name" /> <sf:errors class="form-error" path="name" /></td>
          </tr>
          <tr>
            <td class="form-intro">Display Name</td>
            <td><sf:input class="form-fill-in" path="displayName" />  <sf:errors class="form-error" path="displayName" /></td>
          </tr>
          <tr>
            <td></td>
            <td class="form-button"><input type="submit" value="Create Organization" /></td>
          </tr>
        </table>
      </sf:form>
      </div>
    </article>