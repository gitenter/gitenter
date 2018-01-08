<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

    <header>
      <a href="<c:url value="/" />"><img class="logo" src="<s:url value="/resources/image/logo.png" />" alt="enterovirus" height="48" width="192"></a>
      <sf:form method="POST" action="/logout">
        <input type="submit" value="Log out" />
      </sf:form>
      <sf:form method="GET" action="/settings">
        <input type="submit" value="Settings" />
      </sf:form>
      <div class="text">Logged in as <security:authentication property="principal.username" /></div>
    </header>
