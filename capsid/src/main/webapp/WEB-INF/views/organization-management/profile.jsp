<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />"><c:out value="${organization.displayName}" /></a> &rarr;
      <span class="nav-current">Profile</span>
    </nav>
    <article>
      <div>
      <sf:form method="POST" modelAttribute="organizationDTO" >
        <table class="fill-in">
          <tr>
            <td>Name</td>
            <td class="pre-fill">
              <c:out value="${organizationDTO.name}" />
              <sf:hidden path="name" />
            </td>
          </tr>
          <tr>
            <td>Display name</td>
            <td>
              <sf:input path="displayName" />
              <sf:errors class="error" path="displayName" />
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
