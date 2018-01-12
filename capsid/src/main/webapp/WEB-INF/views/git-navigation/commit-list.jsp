<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/fmt" prefix = "fmt" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />">${organization.displayName}</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}" />">${repository.displayName}</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}/branches/${branch}" />">Branch: ${branch}</a> &rarr; 
      <span class="nav-current">Commits</span>
    </nav>
    <article>
      <table class="hidden">
        <tr>
          <td class="right">
            <sf:form method="GET" action="/organizations/${organization.id}/repositories/${repository.id}/commits">
              <%-- 
                In here we cannot use sf:select and sf:options, as
                here is not targeting to return a general Bean.
                If do so, Spring will return error 
                "Neither BindingResult nor plain target object for bean name 'command' available as request attribute".
                
                TODO:
                Consider setup the selected value. The following code
                works but STS will give error. So leave it in here now.
                
                <c:if test="${b.name.equals(branch)}">selected="selected"</c:if>
              --%>
              <select class="inline" name="branch">
                <c:forEach items="${repository.branchNames}" var="b" >
                  <option value="${b.name}"><c:out value="Branch: ${b.name}" /></option>
                </c:forEach>
              </select> 
            </sf:form>
        <input class="menu" type="submit" value="Switch" />
          </td>
        </tr>
      </table>
      <table class="info">
        <tr>
          <th>Status</th>
          <th class="left">Log</th>
          <th>Browse files</th>
        </tr>
        <c:forEach var="commitInfo" items="${repository.commitInfos}">
        <tr>
          <td></td>
          <td class="left">
            <p>${commitInfo.fullMessage}</p>
            <p>By ${commitInfo.gitUserInfo.name} at <fmt:formatDate type = "both" dateStyle = "medium" timeStyle = "short" value = "${commitInfo.getCommitDate()}" /></p>
          </td>
          <td>
            <form method="GET" action="<s:url value="/organizations/${organization.id}/repositories/${repository.id}/commits/${commitInfo.commitSha.shaChecksumHash}" />" >
              <input type="submit" value="${fn:substring(commitInfo.commitSha.shaChecksumHash, 0, 6)}">
            </form>
          </td>
        </tr>
        </c:forEach>
      </table>
    </article>