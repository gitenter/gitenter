<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<c:choose>
<c:when test="${folderOrFile.isFile()}">
  <c:if test="${commit.includeFile(folderOrFile.relativePath)}">
    <li>
      <s:url var="file_url" value="${currentUrl}/documents/directories/${folderOrFile.relativePath}" />
      <sf:form method="GET" action="${file_url}">
        <input class="document-file" type="submit" value="${folderOrFile.name}" />
      </sf:form>
    </li>
  </c:if>
  <c:if test="${!commit.includeFile(folderOrFile.relativePath)}">
    <li><span class="non-document-file"><c:out value="${folderOrFile.name}" /></span></li>
  </c:if>
</c:when>
<c:otherwise>
  <li><span class="folder"><c:out value="${folderOrFile.name}" /></span></li>
  <ul>
    <c:forEach var="i" items="${folderOrFile.subpath}">
      <c:set var="folderOrFile" value="${i}" scope="request"/>
      <jsp:include page="commit-folder-structure.jsp"/>
    </c:forEach>
  </ul>
</c:otherwise>
</c:choose>