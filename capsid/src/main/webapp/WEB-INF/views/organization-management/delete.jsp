<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />"><c:out value="${organization.displayName}" /></a> &rarr;
      <a href="<s:url value="/organizations/${organization.id}/settings" />">Settings</a> &rarr;
      <span class="nav-current">Delete organization</span>
    </nav>
    <article>
      <div>
      <sf:form method="POST">
        <table class="fill-in">
          <tr>
            <td>Name</td>
            <td class="pre-fill">
              <c:out value="${organization.name}" />
            </td>
          </tr>
          <tr>
            <td>Please copy name</td>
            <td>
              <input type="text" id="copy_organization_name" name="copy_organization_name" />
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
            <td class="button"><input type="submit" value="Delete organization" /></td>
          </tr>
        </table>
      </sf:form>
      </div>
    </article>
