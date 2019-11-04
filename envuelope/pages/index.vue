<template>
  <div>
    <article>
      <div class="login">
        <form id="loginDTO" action="/login?logout" method="POST">
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
              <td></td>
              <td class="word">
                <input id="remember_me" name="remember-me" type="checkbox" checked />
                <label for="remember_me">Remember me</label>
              </td>
            </tr>

            <tr>
              <td></td>
              <td class="button">
                <input type="submit" value="Log in" />
                <input type="button" onclick="location.href='/register';" value="Sign up" />
              </td>
            </tr>
          </table>
        <div>
        <input type="hidden" name="_csrf" value="ebe28b79-3705-4e2e-8604-f89336182e35" />
        </div></form>
      </div>
    </article>
    <div class="container">
      <div>
        <logo />
      </div>
    </div>
  </div>
</template>

<script>
import Logo from '~/components/Logo.vue'

export default {
  layout: 'unauth',

  components: {
    Logo
  },
}
</script>

<style></style>
