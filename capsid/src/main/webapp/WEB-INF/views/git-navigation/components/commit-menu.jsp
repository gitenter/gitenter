<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

        <table class="hidden">
          <tr>
            <td class="left">
              <c:if test="${branch != null}">
              <form method="GET" action="<s:url value="/organizations/${organization.id}/repositories/${repository.id}/branches/${branch}/commits/" />">
                <input class="menu" type="submit" value="Browse Historical Commits">
              </form>
              </c:if>
            </td>
            <td class="right">
              <sf:form method="GET" action="/organizations/${organization.id}/repositories/${repository.id}">
                <select class="inline" name="branch">
                  <c:forEach items="${repository.branchNames}" var="b">
                    <option value="${b.name}"><c:out value="Branch: ${b.name}" /></option>
                  </c:forEach>
                </select>
                <input class="menu" type="submit" value="Switch" />
              </sf:form>
            </td>
          </tr>
        </table>