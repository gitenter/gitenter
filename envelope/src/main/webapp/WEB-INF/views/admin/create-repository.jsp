<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <a href="<s:url value="/organizations/${organization.id}" />"><c:out value="${organization.displayName}" /></a> &rarr; 
      <span class="nav-current">Create a New Repository</span>
    </nav>
    <article>
      <div>
      <sf:form method="POST" modelAttribute="repositoryDTO" >
        <table class="fill-in">
          <tr>
            <td>Name</td>
            <td>
              <sf:input path="name" />
              <sf:errors class="error" path="name" /></td>
          </tr>
          <tr>
            <td>Display Name</td>
            <td>
              <sf:input path="displayName" />
              <sf:errors class="error" path="displayName" />
            </td>
          </tr>
          <tr>
            <td>Description</td>
            <td>
              <sf:textarea path="description" />
              <sf:errors class="error" path="description" />
            </td>
          </tr>
          <tr>
            <td>Privacy</td>
            <td class="word">
              <sf:radiobutton path="isPublic" value="true" label="Public" checked="checked" />
              <sf:radiobutton path="isPublic" value="false" label="Private" />
              <sf:errors class="error" path="isPublic" />
            </td>
          </tr>
          <tr>
            <td>Initialization with setup files?</td>
            <td class="word">
              <input type="radio" name="include_setup_files" id="include_setup_files_yes" value="true" /><label for="include_setup_files_yes">Yes</label>
              <input type="radio" name="include_setup_files" id="include_setup_files_no" value="false" checked /><label for="include_setup_files_no">No</label>
            </td>
          </tr>
          <tr>
            <td></td>
            <td class="button"><input type="submit" value="Create Repository" /></td>
          </tr>
        </table>
      </sf:form>
      </div>
    </article>