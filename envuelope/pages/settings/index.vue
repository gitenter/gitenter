<template>
  <div>
    <navigationBar />
    <article>
      <h3><nuxt-link to="/settings/profile">Edit profile</nuxt-link></h3>
      <h3><nuxt-link to="/settings/ssh">Manage SSH keys</nuxt-link></h3>
      <h3><nuxt-link to="/settings/account/password">Change password</nuxt-link></h3>
      <h3><nuxt-link to="/settings/account/delete">Delete account</nuxt-link></h3>
    </article>
  </div>
</template>

<router>
  {
    name: 'Settings'
  }
</router>

<script>
import NavigationBar from '~/components/NavigationBar.vue'

export default {
  middleware: 'authenticated',
  layout: 'auth',

  components: {
    NavigationBar,
  },
}
</script>

<style></style>
