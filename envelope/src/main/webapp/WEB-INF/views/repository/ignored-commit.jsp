<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

    <jsp:include page="components/commit-nav.jsp"/>
    <article>
      <div>
        <jsp:include page="components/commit-menu.jsp"/>
      </div>
      <div>
        <h3>Turn off for Traceability Analysis</h3>
        <p>To turn it on, include a file with name <code>gitenter.properties</code>, and have <code>enable_systemwide = on</code> in its content.</p>
      </div>
      <jsp:include page="components/repository-collaboration.jsp"/>
    </article>
