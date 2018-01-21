<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />">${organization.displayName}</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}" />">${repository.displayName}</a> &rarr; 
      <c:if test="${branch != null}">
        <span class="nav-current">Branch: ${branch}</span>
      </c:if>
      <c:if test="${shaChecksumHash != null}">
        <span class="nav-current">Commit: ${fn:substring(shaChecksumHash, 0, 6)}</span>
      </c:if>
    </nav>
    <article>
      <div>
        <table class="hidden">
          <tr>
            <td class="left">
              <c:if test="${branch != null}">
              <form method="GET"
                action="<s:url value="/organizations/${organization.id}/repositories/${repository.id}/branches/${branch}/commits/" />">
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
      </div>
      <div>
        <h2>The system is turned off for this commit.</h2>
      </div>
    </article>
