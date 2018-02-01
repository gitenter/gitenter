<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/settings" />">Settings</a> &rarr; 
      <span class="nav-current">SSH keys</span>
    </nav>
    <article>
      <div>
      <sf:form method="POST" commandName="sshKeyFieldBean" >
        <table class="fill-in">
          <tr>
            <td class="form-intro">SSH public key</td>
            <td><sf:textarea class="form-fill-in-large" path="value" /></td>
          </tr>
          <tr>
            <td></td>
            <td>
              <sf:errors class="form-error" path="value" />
              <c:if test="${errorMessage != null}">
                <p><span class="form-error">${errorMessage}</span></p>
              </c:if>             
              <p>The key should be named
              <code>id_rsa.pub</code>,
              <code>id_dsa.pub</code>,
              <code>identity.pub</code>,
              <code>id_ecdsa.pub</code>,
              or <code>id_ed25519.pub</code>,
              found under the <code>~/.ssh/</code> folder of your 
              home directory. It begins with the key type
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
            <td></td>
            <td class="form-button"><input type="submit" value="Add a new SSH key" /></td>
          </tr>
        </table>
      </sf:form>
      </div>
    </article>
