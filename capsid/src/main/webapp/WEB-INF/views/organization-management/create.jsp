<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <span class="nav-current">Create a New Organization</span>
    </nav>
    <article>
      <div>
      <sf:form method="POST" modelAttribute="organizationDTO" >
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
          <c:if test="${message != null}">
            <tr>
              <td></td>
              <td class="error"><c:out value="${message}" /></td>
            </tr>
          </c:if>
          <tr>
            <td></td>
            <td class="button"><input type="submit" value="Create Organization" /></td>
          </tr>
        </table>
      </sf:form>
      </div>
    </article>