<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

    <jsp:include page="components/commit-nav.jsp"/>
    <article>
      <div>
        <jsp:include page="components/commit-menu.jsp"/>
      </div>
      <div class="error">
        <h3>Trace Analyzer Error</h3>
        <p><c:out value="${errorMessage}" /></p>
      </div>
      <jsp:include page="components/repository-collaboration.jsp"/>
    </article>
