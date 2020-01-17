<template>
  <div>
    <navigationBar />
    <article>
      <div class="left-wide">
        <h3>Organized Repositories</h3>

        <h3>Authored Repositories</h3>

        <h3>Currently reviewed repository</h3>
      </div>
      <div class="right-narrow">
        <h3>
          Managed organizations

          <form id="command" action="/organizations/create" method="GET">
            <input type="submit" value="+" />
          </form>
        </h3>

        <h5 v-for="organization in managedOrganizations">
          <nuxt-link :to="'/organizations/' + organization.id">{{ organization.displayName }}</nuxt-link>
        </h5>

        <h3>Belonged organizations</h3>

      </div>
      <div style="clear:both"></div>
    </article>
    <div class="container">
      <div>
        <logo />
      </div>
    </div>
  </div>
</template>

<router>
  {
    name: 'Home'
  }
</router>

<script>
import Logo from '~/components/Logo.vue'
import NavigationBar from '~/components/NavigationBar.vue'

const Cookie = process.client ? require('js-cookie') : undefined

export default {
  middleware: 'authenticated',
  layout: 'auth',

  components: {
    Logo,
    NavigationBar,
  },

  data() {
    return {
      managedOrganizations: [],
      belongedOrganizations: []
    }
  },

  mounted() {
    this.$axios.get('/users/me/organizations?role=manager', {
      headers: {
        'Authorization': "Bearer " + this.$store.state.auth.accessToken
      }
    })
    .then(response => {
      this.managedOrganizations = response.data
    })
    this.$axios.get('/users/me/organizations?role=ordinary_member', {
      headers: {
        'Authorization': "Bearer " + this.$store.state.auth.accessToken
      }
    })
    .then(response => {
      this.belongedOrganizations = response.data
    })
  },

  methods: {
  }
}
</script>

<style></style>
