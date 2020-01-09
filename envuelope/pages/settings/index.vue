<template>
  <div>
    <nav>
      <nuxt-link to="/">Home</nuxt-link> &rarr;
      <span class="nav-current">Settings</span>
    </nav>
    <article>
      <h3><nuxt-link to="/settings/profile">Edit profile</nuxt-link></h3>
      <h3><nuxt-link to="/settings/ssh">Manage SSH keys</nuxt-link></h3>
      <h3><nuxt-link to="/settings/account/password">Change password</nuxt-link></h3>
      <h3><nuxt-link to="/settings/account/delete">Delete account</nuxt-link></h3>
    </article>
  </div>
</template>

<script>
export default {
  middleware: 'authenticated',
  layout: 'auth'
}
</script>

<style></style>
