<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <span class="nav-underline"><span class="nav-current">Settings</span></span>
    </nav>
    <article>
      <h3><a href="<s:url value="/settings/profile" />">Profile</a></h3>
      <h3><a href="<s:url value="/settings/account" />">Account</a></h3>
      <h3><a href="<s:url value="/settings/ssh" />">SSH keys</a></h3>
    </article>
