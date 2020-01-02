<template>
  <div>
    <article>
      <div v-if="$store.state.auth">
        <button @click="logout">
          Logout
        </button>
      </div>
      <p v-else>
        Please
        <NuxtLink to="/login">
          login
        </NuxtLink>.
      </p>
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

const Cookie = process.client ? require('js-cookie') : undefined

export default {
  middleware: 'authenticated',
  layout: 'unauth',

  components: {
    Logo
  },

  methods: {
    logout () {
      Cookie.remove('auth')
      this.$store.commit('setAuth', null)
    }
  }
}
</script>

<style></style>
