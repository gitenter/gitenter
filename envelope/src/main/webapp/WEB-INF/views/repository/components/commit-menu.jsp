<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

      <div class="menu">
        <table class="hidden">
          <tr>
            <td class="left">
              <c:if test="${branch != null}">
              <form method="GET" action="<s:url value="/organizations/${organization.id}/repositories/${repository.id}/branches/${branch}/commits/" />">
                <input type="submit" value="Browse Historical Commits">
              </form>
              </c:if>
            </td>
            <td class="right">
              <s:url var="repo_url" value="/organizations/${organization.id}/repositories/${repository.id}" />
              <sf:form method="GET" action="${repo_url}">
                <select name="branch">
                  <c:forEach items="${repository.branchNames}" var="b">
                    <option value="${b.name}"><c:out value="Branch: ${b.name}" /></option>
                  </c:forEach>
                </select>
                <input type="submit" value="Switch" />
              </sf:form>
            </td>
          </tr>
        </table>
      </div>