<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />"><c:out value="${organization.displayName}" /></a> &rarr;
      <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}" />"><c:out value="${repository.displayName}" /></a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}/settings" />">Settings</a> &rarr;
      <span class="nav-current">Delete repository</span>
    </nav>
    <article>
      <div>
      <sf:form method="POST">
        <table class="fill-in">
          <tr>
            <td>Name</td>
            <td class="pre-fill">
              <c:out value="${repository.name}" />
            </td>
          </tr>
          <tr>
            <td>Please copy name</td>
            <td>
              <input type="text" id="copy_repository_name" name="copy_repository_name" />
            </td>
          </tr>
          <c:if test="${errorMessage != null}">
          <tr>
            <td></td>
            <td class="error">${errorMessage}</td>
          </tr>
          </c:if>
      	  <tr>
            <td></td>
            <td class="button"><input type="submit" value="Delete repository" /></td>
          </tr>
        </table>
      </sf:form>
      </div>
    </article>
