<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

    <header>
      <a href="<c:url value="/" />"><img class="logo" src="<s:url value="/resources/image/logo.png" />" alt="enterovirus" height="48" width="192"></a>
      
      <%--
        the "url=" part should be meaningless. What I want is
        any page which the current user is logged in.
        Read carefully how "security:authorize" works, and its
        possible options. 
       --%>
      <security:authorize url="/">
        <s:url var="logout_url" value="/logout" />
        <sf:form method="POST" action="${logout_url}">
          <input type="submit" value="Log out" />
        </sf:form>
        
        <s:url var="settings_url" value="/settings" />
        <sf:form method="GET" action="${settings_url}">
          <input type="submit" value="Settings" />
        </sf:form>
        <div class="text">Logged in as <security:authentication property="principal.username" /></div>
      </security:authorize>
      <%--
        TODO:
        Otherwise show login and signup links?
       --%>
    </header>
