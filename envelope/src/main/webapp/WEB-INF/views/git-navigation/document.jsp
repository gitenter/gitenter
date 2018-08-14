<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />"><c:out value="${organization.displayName}" /></a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}" />"><c:out value="${repository.displayName}" /></a> &rarr;
      <c:if test="${branch != null}">
        <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}/branches/${branch}" />">Branch: <c:out value="${branch}" /></a> &rarr; 
      </c:if>
      <c:if test="${shaChecksumHash != null}">
        <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}/commits/${shaChecksumHash}" />">Commit: ${fn:substring(shaChecksumHash, 0, 6)}</a> &rarr; 
      </c:if> 
      <span class="nav-current"><c:out value="${document.relativeFilepath}" /></span>
    </nav>
    <article>
      <div class="menu">
        <table class="hidden">
          <tr>
            <td class="right">
              <sf:form method="GET" action="/organizations/${organization.id}/repositories/${repository.id}/documents/directories/${relativeFilepath}">
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
      <div class="markdown">
        <%--
          TODO:
          No way to escape HTML special characters in here, as the markdown
          are transferred to these characters. Then what happens if user 
          write something malicious in their markdown?
        --%>
        ${content}
      </div>
    </article>