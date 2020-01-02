<template>
  <div>
    <article>
      <div class="login">
        <form @submit.prevent="login">
          <table class="fill-in">
            <tr>
              <td>Username</td>
              <td>
                <input id="username" v-model="username" name="username" type="text" value=""/>
              </td>
            </tr>
            <tr>
              <td>Password</td>
              <td>
                <input id="password" v-model="password" name="password" type="password" value=""/>
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
        </form>
      </div>
    </article>
  </div>
</template>

<script>
const Cookie = process.client ? require('js-cookie') : undefined

export default {
  middleware: 'notAuthenticated',
  layout: 'unauth',

  data() {
    return {
      username: '',
      password: '',
    }
  },

  methods: {
    login() {
      console.log("Login user "+this.username+"!!");
      setTimeout(() => { // we simulate the async request with timeout.
        const request = this.$axios.post(
          'http://localhost:8080/oauth/token',
          "grant_type=password&username="+self.username.value+"&password="+self.password.value,
          {
          auth: {
              username: 'gitenter-envuelope',
              password: 'secretpassword'
            },
            withCredentials: true,
            crossDomain: true
          })
        request.then((response) => {
            console.log(response);
            const auth = {
              accessToken: response.data.access_token
            }
            console.log(auth);
            this.$store.commit('setAuth', auth) // mutating to store for client rendering
            Cookie.set('auth', auth) // saving token in cookie for server rendering
            this.$router.push('/')
          })
          .catch((error) => {
            console.log(error);
          })
      }, 1000)
    }
  }
}
</script>

<style></style>
