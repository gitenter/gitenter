<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <span class="nav-current">Create Organization</span>
    </nav>
    <article>
      <div>
      <sf:form method="POST" commandName="organizationBean" >
        <table class="fill-in">
          <tr>
            <td class="setting-intro">Name</td>
            <td><sf:input class="setting-fill-in" path="name" /> <sf:errors class="setting-error" path="name" /></td>
          </tr>
          <tr>
            <td class="setting-intro">Display Name</td>
            <td><sf:input class="setting-fill-in" path="displayName" />  <sf:errors class="setting-error" path="displayName" /></td>
          </tr>
          <tr>
            <td></td>
            <td class="setting-button"><input type="submit" value="Create Organization" /></td>
          </tr>
        </table>
      </sf:form>
      </div>
    </article>