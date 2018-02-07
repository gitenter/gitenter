<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />">${organization.displayName}</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}" />">${repository.displayName}</a> &rarr; 
      <span class="nav-current">Setup a new repository</span>
    </nav>
    <article>
      <div>
        <p>This repository has not been initialized yet.</p>
        <p>To initialize it, you want to add the following file with name <code>gitenter.properties</code> 
        at the root folder of your repository,</p>
        <pre><code># ---------------------------
# GitEnter configuration file
# ---------------------------

# Value on/off. Set it as off will overwrite all further setups.
enable_systemwide = on

# Paths one in each line:
# > include_paths = folder1
# > include_paths = folder2
# > include_paths = folder3
#
# Or paths have comma in between:
# > include_paths = folder1,folder2,folder3
#
# If nothing is specified, then all files are  included.
include_paths = doc</code></pre>
        <p>and add this server as your remote site,</p>
        <pre><code>git remote add origin git@${rootUrl}:${organization.name}/${repository.name}
git push origin master</code></pre>
      </div>
    </article>
