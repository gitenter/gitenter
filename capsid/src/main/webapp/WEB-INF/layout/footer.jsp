<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>

    <footer>
      <s:url var="about_url" value="/about/" />
      <s:url var="contact_url" value="/contact/" />
      <a href="${about_url}">About</a> | <a href="${contact_url}">Contact</a><br>
      Copyright &copy; 2017-2019 GitEnter
    </footer>
