<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

    <header>
      <a href="<c:url value="/" />"><img class="logo" src="<s:url value="/resources/image/logo.png" />" alt="enterovirus" height="48" width="192"></a>
      
      <s:url var="host_url" value="/about" />
      <sf:form method="GET" action="${host_url}">
        <input type="submit" value="About" />
      </sf:form>
    </header>
