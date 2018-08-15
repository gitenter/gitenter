<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<c:set var="elements" value="${fn:split(folderOrFile,'/')}" />
<c:set var="name" value="${elements[fn:length(elements)-1]}" />
<c:choose>
<c:when test="${folderOrFile.isLeaf()}">
  <c:if test="${documentMap.containsKey(folderOrFile.toString())}">
    <li>
      <s:url var="file_url" value="${currentUrl}/documents/directories/${folderOrFile}" />
      <sf:form method="GET" action="${file_url}">
        <input type="submit" value="${name}" />
      </sf:form>
    </li>
  </c:if>
  <c:if test="${!documentMap.containsKey(folderOrFile.toString())}">
    <li><span class="non-document-file"><c:out value="${name}" /></span></li>
  </c:if>
</c:when>
<c:otherwise>
  <li><span class="folder"><c:out value="${name}" /></span></li>
  <ul>
    <c:forEach var="i" items="${folderOrFile.childrenList()}">
      <c:set var="folderOrFile" value="${i}" scope="request"/>
      <jsp:include page="commit-folder-structure.jsp"/>
    </c:forEach>
  </ul>
</c:otherwise>
</c:choose>