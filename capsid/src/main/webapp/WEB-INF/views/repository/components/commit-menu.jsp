<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

      <div class="menu">
        <table class="hidden">
          <tr>
            <td class="left">
              <c:if test="${branchName != null}">
              <form method="GET" action="<s:url value="/organizations/${organization.id}/repositories/${repository.id}/branches/${branchName}/commits/" />">
                <input type="submit" value="Browse Historical Commits">
              </form>
              </c:if>
            </td>
            <td class="right">
              <s:url var="repo_url" value="/organizations/${organization.id}/repositories/${repository.id}" />
              <sf:form method="GET" action="${repo_url}">
                <select name="branch">
                  <c:forEach items="${branchNames}" var="bName">
                    <option value="${bName}"><c:out value="Branch: ${bName}" /></option>
                  </c:forEach>
                </select>
                <input type="submit" value="Switch" />
              </sf:form>
            </td>
          </tr>
        </table>
      </div>