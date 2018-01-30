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
      <sf:form method="POST" commandName="oneFieldBean" >
        <table class="fill-in">
          <tr>
            <td class="form-intro">SSH public key</td>
            <td><sf:textarea class="form-fill-in" path="value" />  <sf:errors class="form-error" path="value" /></td>
          </tr>
          <tr>
            <td></td>
            <td>
              The key should be found at <code>~/.ssh/id_rsa.pub</code> under 
              your home folder. It begins with <code>ssh-rsa</code> (or ...).
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
