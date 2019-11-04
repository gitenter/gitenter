<template>
  <div>
    <nav>
      <a href="/">Home</a> &rarr;
      <span class="nav-current">Sign Up</span>
    </nav>
    <article>
      <div>
        <form id="memberRegisterDTO" action="/register" method="POST">
          <table class="fill-in">
            <tr>
              <td>Username</td>
              <td>
                <input id="username" name="username" type="text" value=""/>
              </td>
            </tr>
            <tr>
              <td>Password</td>
              <td>
                <input id="password" name="password" type="password" value=""/>
              </td>
            </tr>
            <tr>
              <td>Display Name</td>
              <td>
                <input id="displayName" name="displayName" type="text" value=""/>
              </td>
            </tr>
            <tr>
              <td>Email address</td>
              <td>
                <input id="email" name="email" type="email" value=""/>
              </td>
            </tr>
            <tr>
              <td></td>
              <td class="button"><input type="submit" value="Register" /></td>
            </tr>
          </table>
          <div>
            <input type="hidden" name="_csrf" value="ebe28b79-3705-4e2e-8604-f89336182e35" />
          </div>
        </form>
      </div>
    </article>
  </div>
</template>

<script>
export default {
  layout: 'unauth'
}
</script>

<style></style>
