<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />"><c:out value="${organization.displayName}" /></a> &rarr;
      <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}" />"><c:out value="${repository.displayName}" /></a> &rarr; 
      <span class="nav-current">Settings</span>
    </nav>
    <article>
      <div>
      <sf:form method="POST" commandName="repositoryBean" >
        <table class="fill-in">
          <tr>
            <td>Username</td>
            <td class="pre-fill">
              <c:out value="${repositoryBean.name}" />
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
          <tr>
            <td>Description</td>
            <td>
              <sf:textarea path="description" />
              <sf:errors class="error" path="description" />
            </td>
          </tr>
          <tr>
            <td>Privacy</td>
            <td class="word">
              <sf:radiobutton path="isPublic" value="true" label="Public" />
              <sf:radiobutton path="isPublic" value="false" label="Private" />
              <sf:errors class="error" path="isPublic" />
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
            <td class="button"><input type="submit" value="Update settings" /></td>
          </tr>
        </table>
      </sf:form>
      </div>
    </article>
