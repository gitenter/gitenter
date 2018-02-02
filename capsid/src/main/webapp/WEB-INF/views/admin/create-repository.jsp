<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />">${organization.displayName}</a> &rarr; 
      <span class="nav-current">Create a New Repository</span>
    </nav>
    <article>
      <div>
      <sf:form method="POST" commandName="repositoryBean" >
        <table class="fill-in">
          <tr>
            <td>Name</td>
            <td>
              <sf:input path="name" />
              <sf:errors class="error" path="name" /></td>
          </tr>
          <tr>
            <td>Display Name</td>
            <td>
              <sf:input path="displayName" />
              <sf:errors class="error" path="displayName" />
            </td>
          </tr>
          <tr>
            <td>Include setup files?</td>
            <td class="word">
              <input type="radio" name="include_setup_files" value="true" checked> Yes
              <input type="radio" name="include_setup_files" value="false"> No
            </td>
          </tr>
          <tr>
            <td></td>
            <td class="button"><input type="submit" value="Create Repository" /></td>
          </tr>
        </table>
      </sf:form>
      </div>
    </article>