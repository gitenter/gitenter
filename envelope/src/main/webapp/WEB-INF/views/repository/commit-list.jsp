<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/fmt" prefix = "fmt" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />"><c:out value="${organization.displayName}" /></a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}" />"><c:out value="${repository.displayName}" /></a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}/branches/${branchName}" />">Branch: <c:out value="${branchName}" /></a> &rarr; 
      <span class="nav-current">Commits</span>
    </nav>
    <article>
      <div class="menu">
        <table class="hidden">
          <tr>
            <td class="right">
              <s:url var="commit_list_url" value="/organizations/${organization.id}/repositories/${repository.id}/commits" />
              <sf:form method="GET" action="${commit_list_url}">
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
                <select name="branch">
                  <c:forEach items="${branches}" var="branch" >
                    <option value="${branch.name}"><c:out value="Branch: ${branch.name}" /></option>
                  </c:forEach>
                </select>
                <input type="submit" value="Switch" /> 
              </sf:form>
            </td>
          </tr>
        </table>
      </div>
      <div>
        <table class="info">
          <tr>
            <th>Commit</th>
            <th class="left">Log</th>
            <th>Status</th>
          </tr>
          <c:forEach var="commit" items="${commits}">
          <tr>
            <td>
              <form method="GET" action="<s:url value="/organizations/${organization.id}/repositories/${repository.id}/commits/${commit.sha}" />" >
                <input type="submit" value="${fn:substring(commit.sha, 0, 6)}">
              </form>
            </td>
            <td class="left">
              <p><c:out value="${commit.message}" /></p>
              <p class="minor">By <c:out value="${commit.author.name}" />, at <fmt:formatDate type="both" dateStyle="medium" timeStyle="short" value="${commit.timestamp}" /></p>
            </td>
<%--            <td><img src="<s:url value="/resources/image/status_icons/${commitLog.value.getClass().simpleName}.png" />" alt="${commitLog.value.getClass().simpleName}"></td> --%>
          </tr>
          </c:forEach>
        </table>
        <table class="hidden">
          <tr>
            <td class="right" width="50%"><c:if test="${page>0}"><a class="paging" href="?page=${page-1}">&laquo;</a></c:if></td>
            <td class="left" width="50%"><a class="paging" href="?page=${page+1}">&raquo;</a></td>
          </tr>
        </table>
      </div>
    </article>