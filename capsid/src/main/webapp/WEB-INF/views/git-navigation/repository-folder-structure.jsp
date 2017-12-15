<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>

<c:choose>
<c:when test="${!folderStructure.isLeaf()}">
  <p>${folderStructure}</p>
  <c:forEach var="folderOrFile" items="${folderStructure.childrenList()}">
    <c:set var="folderStructure" value="${folderOrFile}" scope="request"/>
    <jsp:include page="repository-folder-structure.jsp"/>
  </c:forEach>
</c:when>
<c:otherwise>
  <p><a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}/directories/${folderStructure}" />">${folderStructure}</a></p>
</c:otherwise>
</c:choose>