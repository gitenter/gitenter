<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />">${organization.displayName}</a> &rarr;
      <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}" />">${repository.displayName}</a> &rarr; 
      <c:if test="${branchName != null}">
        <span class="nav-current">Branch: ${branchName.name}</span>
      </c:if>
      <c:if test="${shaChecksumHash != null}">
        <span class="nav-current">Commit: ${shaChecksumHash}</span>
      </c:if>
    </nav>
    <article>
      <sf:form method="GET" action="/organizations/${organization.id}/repositories/${repository.id}">
        <%-- 
          In here we cannot use sf:select and sf:options, as
          here is not targeting to return a general Bean.
          If do so, Spring will return error 
          "Neither BindingResult nor plain target object for bean name 'command' available as request attribute".
          
          TODO:
          Consider setup the selected value. The following code
          works but STS will give error. So leave it in here now.
          
          <c:if test="${b.name.equals(branchName.name)}">selected="selected"</c:if>
        --%>
        <select class="inline" name="branch">
          <c:forEach items="${repository.branchNames}" var="b" >
            <option value="${b.name}"><c:out value="Branch: ${b.name}" /></option>
          </c:forEach>
        </select> 
        <input class="menu" type="submit" value="Switch" />
      </sf:form>
      <c:if test="${branchName != null}">
        <form method="GET" action="<s:url value="/organizations/${organization.id}/repositories/${repository.id}/branches/${branchName.name}/commits/" />" >
          <input class="menu" type="submit" value="Browse Historical Commits">
        </form>
      </c:if>
      <c:set var="folderStructure" value="${folderStructure}" scope="request"/>
      <jsp:include page="commit-folder-structure.jsp"/>
    </article>
