<template>
  <div>
    <navigationBar />
    <article>
      <div class="left-wide">
        <h1>
          {{ organization.displayName }}
          <form @submit.prevent="goSettings">
            <input
              type="submit"
              value="Settings"
            >
          </form>
        </h1>
        <h3>
          Repositories
          <form @submit.prevent="createRepository">
            <input
              type="submit"
              value="+"
            >
          </form>
        </h3>
      </div>
      <div class="right-narrow">
        <members v-bind:organizationId="organizationId" />
      </div>
      <div style="clear:both" />
    </article>
  </div>
</template>

<router>
  {
    name: 'Organization'
  }
</router>

<script>
import NavigationBar from '~/components/NavigationBar.vue';
import Members from '~/components/organization/Members.vue';

export default {
  middleware: 'authenticated',
  layout: 'auth',

  components: {
    NavigationBar,
    Members
  },

  data() {
    return {
      organizationId: this.$route.params.organizationId,
      organization: ''
    };
  },

  mounted() {
    this.$axios.get('/organizations/'+this.organizationId, {
      headers: {
        'Authorization': "Bearer " + this.$store.state.auth.accessToken
      }
    })
    .then(response => {
      console.log(response.data);
      this.organization = response.data;
    });
  },

  methods: {
    goSettings() {
      this.$router.push("/organizations/" + this.organization.id + "/settings");
    },
    createRepository() {
      this.$router.push("/organizations/" + this.organization.id + "/repositories/create");
    }
  }
};
</script>

<style></style>
