<c:set var="foldersAndFiles" value="${folderStructure.children()}"/>
<c:forEach var="folderOrFile" items="foldersAndFiles">
    <!-- TODO: print the node here -->
    <c:set var="folderStructure" value="${folderOrFile}" scope="request"/>
    <jsp:include page="repository-folder-structure.jsp"/>
</c:forEach>