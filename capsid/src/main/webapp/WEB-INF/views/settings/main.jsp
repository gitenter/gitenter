<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <span class="nav-underline"><span class="nav-current">Settings</span></span>
    </nav>
    <article>
      <h2><a href="<s:url value="/settings/profile" />">Profile</a></h2>
      <h2><a href="<s:url value="/settings/account" />">Account</a></h2>
    </article>
