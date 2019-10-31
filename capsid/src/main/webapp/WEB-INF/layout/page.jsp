<!DOCTYPE html>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>

<html lang="en">
  <head>
	<meta charset="utf-8">
	${googleSigninMetaElement}
    <title>GitEnter</title>
    <link rel="stylesheet" type="text/css" href="<s:url value="/resources/css/style.css" />" >
	<link rel="icon" type="image/x-icon" href="<s:url value="/resources/image/favicon.ico" />" />
	<script src="https://apis.google.com/js/platform.js" async defer></script>
  </head>
  <body>
    <t:insertAttribute name="header" />
    <t:insertAttribute name="body" />
    <t:insertAttribute name="footer" />
  </body>
</html>
