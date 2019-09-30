<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>

    <header>
      <a href="<c:url value="/" />"><img class="logo" src="<s:url value="/resources/image/logo.png" />" alt="enterovirus" height="48" width="192"></a>
      
      <s:url var="help_url" value="/help/" />
      <sf:form method="GET" action="${help_url}">
        <input type="submit" value="Help" />
      </sf:form>
      <s:url var="pricing_url" value="/pricing/" />
      <sf:form method="GET" action="${pricing_url}">
        <input type="submit" value="Pricing" />
      </sf:form>
    </header>
