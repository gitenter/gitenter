<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${!folderStructure.isLeaf()}">
  <c:forEach var="folderOrFile" items="${folderStructure.childrenList()}">
    <p>${folderOrFile}</p>
    <c:set var="folderStructure" value="${folderOrFile}" scope="request"/>
    <jsp:include page="repository-folder-structure.jsp"/>
  </c:forEach>
</c:if>