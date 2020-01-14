<template>
  <div>
    <header>
      <a href="/"><img class="logo" src="/resources/image/logo.png" alt="enterovirus" height="48" width="192"></a>

      <form @submit.prevent="logout">
        <input id="logout" type="submit" value="Log out" />
      </form>

      <form action="/settings" method="GET">
        <input type="submit" value="Settings" />
      </form>

      <form action="/help/" method="GET">
        <input type="submit" value="Help" />
      </form>

      <div class="text">Logged in as {{ username }}</div>
    </header>

    <nuxt />

    <footer>
      <a href="/about/">About</a> | <a href="/contact/">Contact</a><br>
      Copyright &copy; 2017-2019 GitEnter
    </footer>
  </div>
</template>

<script>
const Cookie = process.client ? require('js-cookie') : undefined

export default {
  data() {
    return {
      username: this.$store.state.auth.username
    }
  },

  methods: {
    logout() {
      Cookie.remove('auth')
      this.$store.commit('setAuth', null)
      this.$router.push('/login')
    }
  }
}
</script>

<style></style>
