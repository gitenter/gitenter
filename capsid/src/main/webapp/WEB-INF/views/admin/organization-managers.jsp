<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />">${organization.displayName}</a> &rarr;
      <span class="nav-current">Managers</span>
    </nav>
    <article>
      <div>
        <h2>Managers:</h2>
        <c:forEach var="member" items="${organization.managers}">
          <h3>${member.displayName}</h3>
        </c:forEach>
        <sf:form method="POST" action="/organizations/${organization.id}/managers/add">
          <input class="form-fill-in" name="managerName" />
          <input type="submit" value="Add" />
        </sf:form>
      </div>
    </article>
