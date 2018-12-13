<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>

    <nav>
      <c:forEach var="node" items="${navigationPath}">
        <a href="<s:url value="${node.link}" />"><c:out value="${node.name}" /></a> &rarr; 
      </c:forEach>
      <span class="nav-current"><c:out value="${navigationEndNode.name}" /></span>
    </nav>
    <article>
      <div class="markdown">
        ${content}
      </div>
    </article>
    
