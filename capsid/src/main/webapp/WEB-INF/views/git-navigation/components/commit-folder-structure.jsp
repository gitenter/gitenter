<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>

<c:choose>
<c:when test="${folderStructure.isLeaf()}">
  <c:set var="filepath" value="${folderStructure}" />
  <c:if test="${documentMap.containsKey(filepath.toString())}">
    <li><span class="document-file"><a href="<s:url value="${currentUrl}/documents/directories/${filepath}" />">${filepath}</a></span></li>
  </c:if>
  <c:if test="${!documentMap.containsKey(filepath.toString())}">
    <li><span class="non-document-file">${filepath}</span></li>
  </c:if>
</c:when>
<c:otherwise>
  <li><span class="folder">${folderStructure}</span></li>
</c:otherwise>
</c:choose>

<c:if test="${!folderStructure.isLeaf()}">
  <ul>
  <c:forEach var="folderOrFile" items="${folderStructure.childrenList()}">
    <c:set var="folderStructure" value="${folderOrFile}" scope="request"/>
    <jsp:include page="commit-folder-structure.jsp"/>
  </c:forEach>
  </ul>
</c:if>