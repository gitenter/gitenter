<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>

<c:choose>
<c:when test="${folderStructure.isLeaf()}">
  <c:set var="filepath" value="${folderStructure}" />
  <c:if test="${documentMap.containsKey(filepath.toString())}">
    <p class="document-file"><a href="<s:url value="${currentUrl}/documents/directories/${filepath}" />">${filepath}</a></p>
  </c:if>
  <c:if test="${!documentMap.containsKey(filepath.toString())}">
    <p class="non-document-file">${filepath}</p>
  </c:if>
</c:when>
<c:otherwise>
  <p class="folder">${folderStructure}</p>
</c:otherwise>
</c:choose>

<c:if test="${!folderStructure.isLeaf()}">
  <c:forEach var="folderOrFile" items="${folderStructure.childrenList()}">
    <c:set var="folderStructure" value="${folderOrFile}" scope="request"/>
    <jsp:include page="commit-folder-structure.jsp"/>
  </c:forEach>
</c:if>