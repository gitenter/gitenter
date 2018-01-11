<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/fmt" prefix = "fmt" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />">${organization.displayName}</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}" />">${repository.displayName}</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}/branches/${branch}" />">Branch ${branch}</a> &rarr; 
      <span class="nav-current">Commits</span>
    </nav>
    <article>
      <table class="info">
        <tr>
          <th>Time</th>
          <th>User</th>
          <th>Message</th>
          <th>Browse files</th>
        </tr>
        <c:forEach var="commitInfo" items="${repository.commitInfos}">
        <tr>
          <td><fmt:formatDate type = "both" dateStyle = "medium" timeStyle = "short" value = "${commitInfo.getCommitDate()}" /></td>
          <td>${commitInfo.gitUserInfo.name}</td>
          <td>${commitInfo.fullMessage}</td>
          <td>
            <form method="GET" action="<s:url value="/organizations/${organization.id}/repositories/${repository.id}/commits/${commitInfo.commitSha.shaChecksumHash}" />" >
              <input type="submit" value="${fn:substring(commitInfo.commitSha.shaChecksumHash, 0, 6)}">
            </form>
          </td>
        </tr>
        </c:forEach>
      </table>
    </article>