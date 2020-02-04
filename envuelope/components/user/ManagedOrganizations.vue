<template>
  <div>
    <h3>
      Managed organizations

      <form
        id="command"
        action="/organizations/create"
        method="GET"
      >
        <input
          type="submit"
          value="+"
        >
      </form>
    </h3>

    <h5
      id = "managed-organizations"
      v-for="(organization, index) in managedOrganizations"
      :key="index"
    >
      <nuxt-link :to="'/organizations/' + organization.id">
        {{ organization.displayName }}
      </nuxt-link>
    </h5>
  </div>
</template>

<script>
export default {
  data: function() {
    return {
      managedOrganizations: []
    };
  },

  mounted() {
    this.$axios.get('/users/me/organizations', {
      params: {
        'role': "manager"
      },
      headers: {
        'Authorization': "Bearer " + this.$store.state.auth.accessToken
      }
    })
    .then(response => {
      this.managedOrganizations = response.data;
    });
  }
};
</script>
