<template>
  <div>
    <navigationBar />
    <article>
      <h3>
        <nuxt-link :to="'/organizations/' + organization.id + '/settings/profile'">
          Edit profile
        </nuxt-link>
      </h3>
      <h3>
        <nuxt-link :to="'/organizations/' + organization.id + '/settings/members'">
          Manage members
        </nuxt-link>
      </h3>
      <h3>
        <nuxt-link :to="'/organizations/' + organization.id + '/settings/managers'">
          Manage managers
        </nuxt-link>
      </h3>
      <h3>
        <nuxt-link :to="'/organizations/' + organization.id + '/settings/delete'">
          Delete organization
        </nuxt-link>
      </h3>
    </article>
  </div>
</template>

<router>
  {
    name: 'Repository settings'
  }
</router>

<script>
import NavigationBar from '~/components/NavigationBar.vue';

export default {
  middleware: 'authenticated',
  layout: 'auth',

  components: {
    NavigationBar,
  },

  data() {
    return {
      organization: ''
    };
  },

  mounted() {
    var organizationId = this.$route.params.organizationId;
    this.$axios.get('/organizations/'+organizationId, {
      headers: {
        'Authorization': "Bearer " + this.$store.state.auth.accessToken
      }
    })
    .then(response => {
      console.log(response.data);
      this.organization = response.data;
    });
  }
};
</script>

<style></style>
