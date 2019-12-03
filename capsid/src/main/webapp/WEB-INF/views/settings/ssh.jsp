<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/settings" />">Settings</a> &rarr; 
      <span class="nav-current">Manage SSH keys</span>
    </nav>
    <article>
      <div>
        <sf:form method="POST" modelAttribute="sshKeyFieldDTO" >
        <table class="fill-in">
          <tr>
            <td>Previous keys</td>
            <td class="pre-fill">
              <c:if test="${user.sshKeys.size()==0}">N/A</c:if>
              <c:forEach var="sshKey" items="${user.sshKeys}">
                <p><code>${fn:substring(sshKey, 0, 40)} ...</code></p>
              </c:forEach>
            </td>
          </tr>
          <tr>
            <td></td>
            <td class="word">       
              <p>The key should be saved in a file named
              <code>id_rsa.pub</code>,
              <code>id_dsa.pub</code>,
              <code>identity.pub</code>,
              <code>id_ecdsa.pub</code>,
              or <code>id_ed25519.pub</code>,
              found under the <code>~/.ssh/</code> folder of your 
              home directory. It is a single line begins with the key type
              <code>ssh-rsa</code>,
              <code>ssh-dss</code>,
              <code>ssh-ed25519</code>,
              <code>ecdsa-sha2-nistp521</code>,
              <code>ecdsa-sha2-nistp384</code>,
              or <code>ecdsa-sha2-nistp256</code>,
              followed by the base64-encoded key and (optionally) the comment.</p>
            </td>
          </tr>
          <tr>
            <td>New key</td>
            <td>
              <sf:textarea class="bigger" path="value" />
              <sf:errors class="error" path="value" />
            </td>
          </tr>
          <c:if test="${errorMessage != null}">
            <tr>
              <td></td>
              <td class="error">${errorMessage}</td>
            </tr>
          </c:if>
          <tr>
            <td></td>
            <td class="button"><input type="submit" value="Add a new SSH key" /></td>
          </tr>
        </table>
      </sf:form>
      </div>
    </article>
