<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr;
      <span class="nav-current">Settings</span>
    </nav>
    <article>
      <h3><a href="<s:url value="/settings/profile" />">Edit profile</a></h3>
      <h3><a href="<s:url value="/settings/ssh" />">Manage SSH keys</a></h3>
      <h3><a href="<s:url value="/settings/account/password" />">Change password</a></h3>
      <h3><a href="<s:url value="/settings/account/delete" />">Delete account</a></h3>
    </article>
