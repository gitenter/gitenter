<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>

<c:choose>
<c:when test="${folderStructure.isLeaf()}">
  <p><a href="<s:url value="${currentUrl}/documents/directories/${folderStructure}" />">${folderStructure}</a></p>
</c:when>
<c:otherwise>
  <p>${folderStructure}</p>
</c:otherwise>
</c:choose>

<c:if test="${!folderStructure.isLeaf()}">
  <c:forEach var="folderOrFile" items="${folderStructure.childrenList()}">
    <c:set var="folderStructure" value="${folderOrFile}" scope="request"/>
    <jsp:include page="commit-folder-structure.jsp"/>
  </c:forEach>
</c:if>